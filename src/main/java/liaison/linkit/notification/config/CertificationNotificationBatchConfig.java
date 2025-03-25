package liaison.linkit.notification.config;

import java.util.ArrayList;
import java.util.List;

import liaison.linkit.notification.business.NotificationMapper;
import liaison.linkit.notification.domain.Notification;
import liaison.linkit.notification.domain.repository.notification.NotificationRepository;
import liaison.linkit.notification.domain.type.NotificationType;
import liaison.linkit.notification.domain.type.SubNotificationType;
import liaison.linkit.notification.presentation.dto.NotificationResponseDTO;
import liaison.linkit.profile.domain.activity.ProfileActivity;
import liaison.linkit.profile.domain.awards.ProfileAwards;
import liaison.linkit.profile.domain.education.ProfileEducation;
import liaison.linkit.profile.domain.license.ProfileLicense;
import liaison.linkit.profile.implement.activity.ProfileActivityQueryAdapter;
import liaison.linkit.profile.implement.awards.ProfileAwardsQueryAdapter;
import liaison.linkit.profile.implement.education.ProfileEducationQueryAdapter;
import liaison.linkit.profile.implement.license.ProfileLicenseQueryAdapter;
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
public class CertificationNotificationBatchConfig {

    // 스프링 배치 의존성
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    // adapters
    private final ProfileActivityQueryAdapter profileActivityQueryAdapter;
    private final ProfileAwardsQueryAdapter profileAwardsQueryAdapter;
    private final ProfileEducationQueryAdapter profileEducationQueryAdapter;
    private final ProfileLicenseQueryAdapter profileLicenseQueryAdapter;

    // notification repository
    private final NotificationRepository notificationRepository;

    // mappers
    private final NotificationMapper notificationMapper;

    @Bean
    public Job certificationNotificationProgressJob() {
        return new JobBuilder("certificationNotificationJob", jobRepository)
                .start(certificationNotificationProgressStep())
                .build();
    }

    @Bean
    public Step certificationNotificationProgressStep() {
        return new StepBuilder("certificationNotificationStep", jobRepository)
                .<Notification, Notification>chunk(100, transactionManager)
                .reader(certificationNotificationProgressReader())
                .writer(certificationNotificationProgressWriter())
                .allowStartIfComplete(true)
                .build();
    }

    @Bean
    @StepScope
    public ItemReader<Notification> certificationNotificationProgressReader() {

        // profileActivity, profileAwards, profileEducation, profileLicense 모두 조회해야함.
        // DB에 인증 완료된 항목은 isVerified가 true로 처리됨.
        // true 값을 가지는 항목들에 대해서만 mongoDB에 존재하는지 존재성 여부를 판단
        // 존재하는 경우에는 굳이 추가 알림을 보낼 필요가 없음.
        // mongoDB에 존재하지 않는 경우에 알림을 보내서 처리할 수 있도록 함.
        List<Notification> notificationsToProcess = new ArrayList<>();

        // 1. 이력 인증 항목 처리
        List<ProfileActivity> verifiedActivities =
                profileActivityQueryAdapter.getByIsActivityVerifiedTrue();
        for (ProfileActivity activity : verifiedActivities) {
            // 해당 이력이 이미 알림을 받은 이력인지 확인
            boolean hasNotification =
                    notificationRepository.existsByCertificationTypeAndItemId(
                            NotificationType.CERTIFICATION,
                            SubNotificationType.ACTIVITY_CERTIFICATION_ACCEPTED,
                            activity.getId(),
                            "ACTIVITY");

            if (!hasNotification) {
                // 새로운 알림 생성
                NotificationResponseDTO.NotificationDetails
                        activityCertificationAcceptedNotification =
                                NotificationResponseDTO.NotificationDetails.certificationAccepted(
                                        activity.getId(), "ACTIVITY");

                // 객체만 생성
                Notification savedNotification =
                        notificationMapper.toNotification(
                                activity.getProfile().getMember().getId(),
                                NotificationType.CERTIFICATION,
                                SubNotificationType.ACTIVITY_CERTIFICATION_ACCEPTED,
                                activityCertificationAcceptedNotification);

                // 해당 객체를 처리해야할 DTO에 저장
                notificationsToProcess.add(savedNotification);
            }
        }

        // 2. 학력 인증 항목 처리
        List<ProfileEducation> verifiedEducations =
                profileEducationQueryAdapter.getByIsEducationVerifiedTrue();
        for (ProfileEducation education : verifiedEducations) {
            // 해당 이력이 이미 알림을 받은 이력인지 확인
            boolean hasNotification =
                    notificationRepository.existsByCertificationTypeAndItemId(
                            NotificationType.CERTIFICATION,
                            SubNotificationType.EDUCATION_CERTIFICATION_ACCEPTED,
                            education.getId(),
                            "EDUCATION");

            if (!hasNotification) {
                // 새로운 알림 생성
                NotificationResponseDTO.NotificationDetails
                        educationCertificationAcceptedNotification =
                                NotificationResponseDTO.NotificationDetails.certificationAccepted(
                                        education.getId(), "EDUCATION");

                // 객체만 생성
                Notification savedNotification =
                        notificationMapper.toNotification(
                                education.getProfile().getMember().getId(),
                                NotificationType.CERTIFICATION,
                                SubNotificationType.EDUCATION_CERTIFICATION_ACCEPTED,
                                educationCertificationAcceptedNotification);

                // 해당 객체를 처리해야할 DTO에 저장
                notificationsToProcess.add(savedNotification);
            }
        }

        // 3. 수상 인증 항목 처리
        List<ProfileAwards> verifiedAwards = profileAwardsQueryAdapter.getByIsAwardsVerifiedTrue();
        for (ProfileAwards award : verifiedAwards) {
            boolean hasNotification =
                    notificationRepository.existsByCertificationTypeAndItemId(
                            NotificationType.CERTIFICATION,
                            SubNotificationType.AWARDS_CERTIFICATION_ACCEPTED,
                            award.getId(),
                            "AWARDS");

            if (!hasNotification) {
                NotificationResponseDTO.NotificationDetails
                        awardsCertificationAcceptedNotification =
                                NotificationResponseDTO.NotificationDetails.certificationAccepted(
                                        award.getId(), "AWARDS");

                Notification savedNotification =
                        notificationMapper.toNotification(
                                award.getProfile().getMember().getId(),
                                NotificationType.CERTIFICATION,
                                SubNotificationType.AWARDS_CERTIFICATION_ACCEPTED,
                                awardsCertificationAcceptedNotification);

                notificationsToProcess.add(savedNotification);
            }
        }

        // 4. 자격증 인증 항목 처리
        List<ProfileLicense> verifiedLicenses =
                profileLicenseQueryAdapter.getByIsLicenseVerifiedTrue();
        for (ProfileLicense license : verifiedLicenses) {
            boolean hasNotification =
                    notificationRepository.existsByCertificationTypeAndItemId(
                            NotificationType.CERTIFICATION,
                            SubNotificationType.LICENSE_CERTIFICATION_ACCEPTED,
                            license.getId(),
                            "LICENSE");

            if (!hasNotification) {
                NotificationResponseDTO.NotificationDetails
                        licenseCertificationAcceptedNotification =
                                NotificationResponseDTO.NotificationDetails.certificationAccepted(
                                        license.getId(), "LICENSE");

                Notification savedNotification =
                        notificationMapper.toNotification(
                                license.getProfile().getMember().getId(),
                                NotificationType.CERTIFICATION,
                                SubNotificationType.LICENSE_CERTIFICATION_ACCEPTED,
                                licenseCertificationAcceptedNotification);

                notificationsToProcess.add(savedNotification);
            }
        }

        log.info("인증 알림 생성 대상 항목 수: {}", notificationsToProcess.size());
        return new ListItemReader<>(notificationsToProcess);
    }

    @Bean
    public ItemWriter<Notification> certificationNotificationProgressWriter() {
        return items -> {
            if (!items.isEmpty()) {
                notificationRepository.saveAll(items);
                log.info("인증 알림 {} 건 저장 완료", items.size());
            }
        };
    }
}
