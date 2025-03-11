package liaison.linkit.team.config;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

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

        // 전날 날짜를 문자열로 변환 (YYYY-MM-DD 형식)
        String previousDayString = previousDay.toString(); // 예: "2025-03-10"

        log.info("배치 처리 기준 날짜: {}", previousDayString);

        // 상시 모집이 아닌 모든 공고 조회
        List<TeamMemberAnnouncement> allNonPermanentAnnouncements =
                teamMemberAnnouncementQueryAdapter.findAllByIsNotPermanentRecruitment();

        // 전날이 마감일인 공고만 필터링
        List<TeamMemberAnnouncement> announcementsToProcess =
                allNonPermanentAnnouncements.stream()
                        .filter(
                                announcement -> {
                                    // 마감일자가 전날과 일치하는지 확인
                                    String endDate = announcement.getAnnouncementEndDate();

                                    // null 체크 및 공고가 아직 진행 중인지 확인
                                    return endDate != null
                                            && endDate.equals(previousDayString)
                                            && announcement.isAnnouncementInProgress();
                                })
                        .collect(Collectors.toList());

        log.info("전체 상시 모집이 아닌 공고 수: {}", allNonPermanentAnnouncements.size());
        log.info("처리할 전날({}) 마감 공고 수: {}", previousDayString, announcementsToProcess.size());

        return new ListItemReader<>(announcementsToProcess);
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
