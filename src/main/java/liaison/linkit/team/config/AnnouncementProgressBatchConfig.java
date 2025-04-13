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

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

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

    /** 마감일이 지나고 아직 진행 중인 공고만 필터링하는 Reader */
    @Bean
    @StepScope
    public ItemReader<TeamMemberAnnouncement> announcementProgressReader(
            @Value("#{jobParameters['time']}") Long time) {

        LocalDateTime executionDateTime =
                LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.of("Asia/Seoul"));
        LocalDate today = executionDateTime.toLocalDate(); // 실행일 기준

        log.info("배치 실행일 기준 날짜 (today): {}", today);

        List<TeamMemberAnnouncement> allNonPermanentAnnouncements =
                teamMemberAnnouncementQueryAdapter.findAllByIsNotPermanentRecruitment();

        List<TeamMemberAnnouncement> announcementsToProcess =
                allNonPermanentAnnouncements.stream()
                        .filter(
                                announcement -> {
                                    String endDateStr = announcement.getAnnouncementEndDate();

                                    try {
                                        return endDateStr != null
                                                && LocalDate.parse(endDateStr).isBefore(today)
                                                && announcement.isAnnouncementInProgress();
                                    } catch (Exception e) {
                                        log.warn(
                                                "공고 ID {}: 날짜 파싱 실패 - {}",
                                                announcement.getId(),
                                                endDateStr);
                                        return false;
                                    }
                                })
                        .collect(Collectors.toList());

        log.info("전체 상시 모집이 아닌 공고 수: {}", allNonPermanentAnnouncements.size());
        log.info("처리 대상 (마감일이 지난) 공고 수: {}", announcementsToProcess.size());

        return new ListItemReader<>(announcementsToProcess);
    }

    /** 모집 공고 처리 Processor - 공고 상태를 확인하고 DTO로 변환 */
    @Bean
    public ItemProcessor<TeamMemberAnnouncement, AnnouncementProgressDTO>
            announcementProgressProcessor() {
        return announcement -> {
            try {
                log.debug("공고 ID {} 처리 중", announcement.getId());

                AnnouncementProgressDTO dto = new AnnouncementProgressDTO();
                dto.setTeamMemberAnnouncement(announcement);
                dto.setTeamMemberAnnouncementInProgress(false); // 마감 처리

                return dto;
            } catch (Exception e) {
                log.error("공고 ID {}: 처리 중 오류 발생: {}", announcement.getId(), e.getMessage(), e);
                return null;
            }
        };
    }

    /** 마감 처리된 공고 정보를 저장하는 Writer */
    @Bean
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
