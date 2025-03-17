package liaison.linkit.notification.config;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import liaison.linkit.notification.business.NotificationMapper;
import liaison.linkit.notification.domain.Notification;
import liaison.linkit.notification.domain.repository.notification.NotificationRepository;
import liaison.linkit.notification.domain.type.NotificationType;
import liaison.linkit.notification.domain.type.SubNotificationType;
import liaison.linkit.notification.presentation.dto.NotificationResponseDTO;
import liaison.linkit.profile.domain.profile.Profile;
import liaison.linkit.profile.implement.profile.ProfileQueryAdapter;
import liaison.linkit.team.domain.team.Team;
import liaison.linkit.team.domain.teamMember.TeamMember;
import liaison.linkit.team.implement.team.TeamQueryAdapter;
import liaison.linkit.team.implement.teamMember.TeamMemberQueryAdapter;
import liaison.linkit.visit.implement.ProfileVisitQueryAdapter;
import liaison.linkit.visit.implement.TeamVisitQueryAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class VisitorNotificationBatchConfig {
    // 스프링 배치 의존성
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    private final ProfileVisitQueryAdapter profileVisitQueryAdapter;
    private final TeamVisitQueryAdapter teamVisitQueryAdapter;
    private final TeamQueryAdapter teamQueryAdapter;

    private final ProfileQueryAdapter profileQueryAdapter;

    private final NotificationMapper notificationMapper;

    // notification repository
    private final NotificationRepository notificationRepository;

    // 알림 발송 간격 (일주일)
    private static final long NOTIFICATION_INTERVAL_DAYS = 7;
    private final TeamMemberQueryAdapter teamMemberQueryAdapter;

    @Bean
    public Job visitorNotificationProgressJob() {
        return new JobBuilder("visitorNotificationProgressJob", jobRepository)
                .start(visitorNotificationProgressStep())
                .build();
    }

    @Bean
    public Step visitorNotificationProgressStep() {
        return new StepBuilder("visitorNotificationStep", jobRepository)
                .<Notification, Notification>chunk(100, transactionManager)
                .reader(visitorNotificationProgressReader())
                .writer(visitorNotificationProgressWriter())
                .allowStartIfComplete(true)
                .build();
    }

    // notification Repo 에서 해당 회원에게 visitor notification이 발송된 이력이 있는지를 조사한다.
    // 조사할 때 기준은 오늘 날짜 기준으로 1주일 시간이 흐르지 않았으면 발송되었다고 판단하고 일주일이 넘어가면 발송 안되었다고 판단한다.
    // 발송되었다고 판단되면 notificationsToProcess 추가한다.

    // 방문자가 1명이라도 있어야지 알림 기능은 작동 가능하다.
    // 방문자가 없으면 알림 기능은 작동하지 않는다.

    // profile_visits와 team_visits 두 가지 방문자 타입이 있다.
    // 각 테이블을 모두 조회하여 방문자가 있는지 확인한다.
    // 방문자가 있으면 notificationsToProcess에 추가한다.

    @Bean
    @StepScope
    public ItemReader<Notification> visitorNotificationProgressReader() {
        List<Notification> notificationsToProcess = new ArrayList<>();
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusDays(NOTIFICATION_INTERVAL_DAYS);

        // 1. 프로필 방문자 처리
        processProfileVisitorNotifications(notificationsToProcess, oneWeekAgo);

        // 2. 팀 방문자 처리
        processTeamVisitorNotifications(notificationsToProcess, oneWeekAgo);

        log.info("방문자 알림 생성 대상 항목 수: {}", notificationsToProcess.size());
        return new ListItemReader<>(notificationsToProcess);
    }

    private void processProfileVisitorNotifications(
            List<Notification> notificationsToProcess, LocalDateTime oneWeekAgo) {
        // 프로필별 방문자 수를 계산 (QueryDSL 사용)
        Map<Long, Long> profileVisitCounts =
                profileVisitQueryAdapter.countVisitsPerProfileWithinLastWeek();

        if (profileVisitCounts.isEmpty()) {
            log.info("최근 프로필 방문 기록이 없습니다.");
            return;
        }

        // 각 프로필에 대해 알림 생성
        for (Map.Entry<Long, Long> entry : profileVisitCounts.entrySet()) {
            Long visitedProfileId = entry.getKey();
            Long visitorCount = entry.getValue();

            if (visitorCount == 0) {
                continue; // 방문자가 없으면 알림 생성 안함
            }

            // 방문받은 프로필 정보 조회 (visitedProfileId에 해당하는 프로필)
            // 여기서는 visitedProfileId로 직접 프로필을 조회해야 함
            Profile visitedProfile = profileQueryAdapter.findById(visitedProfileId);
            if (visitedProfile == null) {
                log.warn("프로필 ID {}에 해당하는 프로필을 찾을 수 없습니다.", visitedProfileId);
                continue;
            }

            // 방문받은 프로필의 소유자 ID 조회
            Long profileOwnerMemberId = visitedProfile.getMember().getId();

            // 최근에 방문자 알림을 보낸 적이 있는지 확인
            boolean recentNotificationExists =
                    notificationRepository
                            .existsByReceiverMemberIdAndNotificationTypeAndCreatedAtAfterOneWeekAgo(
                                    profileOwnerMemberId,
                                    NotificationType.VISITOR,
                                    SubNotificationType.PROFILE_VISITOR,
                                    oneWeekAgo);

            if (!recentNotificationExists) {
                // 알림 생성
                NotificationResponseDTO.NotificationDetails visitorNotificationDetails =
                        NotificationResponseDTO.NotificationDetails.profileVisitorCount(
                                visitedProfile.getMember().getEmailId(),
                                visitorCount,
                                "PROFILE_VISITOR");

                Notification notification =
                        notificationMapper.toNotification(
                                profileOwnerMemberId,
                                NotificationType.VISITOR,
                                SubNotificationType.PROFILE_VISITOR,
                                visitorNotificationDetails);

                notificationsToProcess.add(notification);
                log.debug(
                        "프로필 ID {}에 대한 방문자 알림 생성 (프로필 소유자 ID: {}, 방문자 수: {})",
                        visitedProfileId,
                        profileOwnerMemberId,
                        visitorCount);
            }
        }
    }

    /** 팀 방문자 알림 처리 메서드 */
    private void processTeamVisitorNotifications(
            List<Notification> notificationsToProcess, LocalDateTime oneWeekAgo) {
        // 팀별 방문자 수를 계산 (QueryDSL 사용)
        Map<Long, Long> teamVisitCounts = teamVisitQueryAdapter.countVisitsPerTeamWithinLastWeek();

        if (teamVisitCounts.isEmpty()) {
            log.info("최근 팀 방문 기록이 없습니다.");
            return;
        }

        // 각 팀에 대해 알림 생성
        for (Map.Entry<Long, Long> entry : teamVisitCounts.entrySet()) {
            Long visitedTeamId = entry.getKey();
            Long visitorCount = entry.getValue();

            if (visitorCount == 0) {
                continue; // 방문자가 없으면 알림 생성 안함
            }

            // 방문받은 팀 정보 조회
            Team visitedTeam = teamQueryAdapter.findById(visitedTeamId);
            if (visitedTeam == null) {
                log.warn("팀 ID {}에 해당하는 팀을 찾을 수 없습니다.", visitedTeamId);
                continue;
            }

            // 팀 관리자 및 오너 목록 조회
            List<TeamMember> teamManagers = teamMemberQueryAdapter.getAllTeamManagers(visitedTeam);
            if (teamManagers.isEmpty()) {
                log.warn("팀 ID {}의 관리자가 존재하지 않습니다.", visitedTeamId);
                continue;
            }

            // 각 관리자와 오너에게 알림 생성
            for (TeamMember manager : teamManagers) {
                // 팀 멤버의 회원 ID 가져오기
                Long receiverMemberId = manager.getMember().getId();

                // 최근에 방문자 알림을 보낸 적이 있는지 확인
                boolean recentNotificationExists =
                        notificationRepository
                                .existsByReceiverMemberIdAndNotificationTypeAndCreatedAtAfterOneWeekAgo(
                                        receiverMemberId,
                                        NotificationType.VISITOR,
                                        SubNotificationType.TEAM_VISITOR,
                                        oneWeekAgo);

                if (!recentNotificationExists) {
                    // 알림 상세 정보 생성
                    NotificationResponseDTO.NotificationDetails visitorNotificationDetails =
                            NotificationResponseDTO.NotificationDetails.teamVisitorCount(
                                    visitedTeam.getTeamCode(), visitorCount, "TEAM_VISITOR");

                    // 알림 객체 생성
                    Notification notification =
                            notificationMapper.toNotification(
                                    receiverMemberId,
                                    NotificationType.VISITOR,
                                    SubNotificationType.TEAM_VISITOR,
                                    visitorNotificationDetails);

                    notificationsToProcess.add(notification);
                    log.debug(
                            "팀 ID {}에 대한 방문자 알림 생성 (회원 ID: {}, 방문자 수: {})",
                            visitedTeamId,
                            receiverMemberId,
                            visitorCount);
                }
            }
        }
    }

    @Bean
    public ItemWriter<Notification> visitorNotificationProgressWriter() {
        return items -> {
            if (!items.isEmpty()) {
                notificationRepository.saveAll(items);
                log.info("방문자 알림 {} 건 저장 완료", items.size());
            }
        };
    }
}
