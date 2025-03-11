package liaison.linkit.team.config;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

import liaison.linkit.team.domain.announcement.TeamMemberAnnouncement;
import liaison.linkit.team.implement.announcement.TeamMemberAnnouncementCommandAdapter;
import liaison.linkit.team.implement.announcement.TeamMemberAnnouncementQueryAdapter;
import liaison.linkit.team.presentation.announcement.dto.AnnouncementProgressDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class AnnouncementProgressBatchConfig {

    // 스프링 배치 의존성
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    // Adapters
    private final TeamMemberAnnouncementQueryAdapter teamMemberAnnouncementQueryAdapter;
    private final TeamMemberAnnouncementCommandAdapter teamMemberAnnouncementCommandAdapter;

    /** 모집 공고 상태 업데이트 작업 */
    @Bean
    public Job announcementProgressJob() {
        return new JobBuilder("announcementProgressJob", jobRepository)
                .start(announcementProgressStep())
                .build();
    }

    /** 모집 공고 상태 업데이트 스텝 */
    @Bean
    public Step announcementProgressStep() {
        return new StepBuilder("announcementProgressStep", jobRepository)
                .<TeamMemberAnnouncement, AnnouncementProgressDTO>chunk(100, transactionManager)
                .reader(announcementProgressReader(null))
                .processor(announcementProgressProcessor())
                .writer(announcementProgressWriter())
                .allowStartIfComplete(true)
                .build();
    }

    /** 모집 마감 상태로 변경이 필요한 모든 모집 공고를 조회하는 Reader */
    @Bean
    @StepScope
    public ItemReader<TeamMemberAnnouncement> announcementProgressReader(
            @Value("#{jobParameters['time']}") Long time) {

        // 실행 시간을 기준으로 전날 날짜의 시작과 끝 시간 계산
        LocalDateTime executionDateTime =
                LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.of("Asia/Seoul"));

        // 전날 날짜 구하기
        LocalDate previousDay = executionDateTime.toLocalDate().minusDays(1);

        // 전날의 시작 시간 (00:00:00)
        LocalDateTime startDateTime = previousDay.atStartOfDay();

        // 전날의 종료 시간 (23:59:59.999999999)
        LocalDateTime endDateTime = previousDay.atTime(LocalTime.MAX);

        log.info("배치 조회 시작 시간: {}", startDateTime);
        log.info("배치 조회 종료 시간: {}", endDateTime);

        List<TeamMemberAnnouncement> announcements =
                teamMemberAnnouncementQueryAdapter
                        .findAllByEndDateTimeBetweenAndIsNotPermanentRecruitment(
                                startDateTime, endDateTime);

        log.info("처리할 마감 공고 수: {}", announcements.size());

        return new ListItemReader<>(announcements);
    }

    /** 모집 공고 처리 Processor - 공고 상태를 확인하고 DTO로 변환 */
    @Bean
    public ItemProcessor<TeamMemberAnnouncement, AnnouncementProgressDTO>
            announcementProgressProcessor() {
        return announcement -> {
            try {
                log.debug("공고 ID {} 처리 중", announcement.getId());

                // 공고의 현재 상태 체크 필요 시 여기서 수행

                // DTO로 변환 - 마감 처리 필요한 것으로 표시
                AnnouncementProgressDTO progressDTO = new AnnouncementProgressDTO();
                progressDTO.setTeamMemberAnnouncement(announcement);
                progressDTO.setTeamMemberAnnouncementInProgress(false); // 마감 처리

                return progressDTO;
            } catch (Exception e) {
                log.error("공고 ID {}: 처리 중 오류 발생: {}", announcement.getId(), e.getMessage(), e);
                return null; // 오류 발생 시 해당 항목 건너뛰기
            }
        };
    }

    /** 마감 처리된 공고 정보를 저장하는 Writer */
    @Bean
    @StepScope
    public ItemWriter<AnnouncementProgressDTO> announcementProgressWriter() {
        return items -> {
            log.info("총 {} 개의 공고 마감 처리 시작", items.size());

            for (AnnouncementProgressDTO item : items) {
                log.debug("공고 ID {} 상태 업데이트", item.getTeamMemberAnnouncement().getId());

                teamMemberAnnouncementCommandAdapter.updateTeamMemberAnnouncementClosedState(
                        item.getTeamMemberAnnouncement(),
                        item.isTeamMemberAnnouncementInProgress());
            }

            log.info("공고 마감 처리 완료");
        };
    }
}
