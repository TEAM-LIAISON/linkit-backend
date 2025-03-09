package liaison.linkit.mail.config;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import liaison.linkit.mail.presentation.dto.AnnouncementAdvertiseDTO;
import liaison.linkit.mail.service.AsyncAnnouncementAdvertiseEmailService;
import liaison.linkit.profile.domain.profile.Profile;
import liaison.linkit.profile.implement.profile.ProfileQueryAdapter;
import liaison.linkit.team.business.assembler.common.AnnouncementCommonAssembler;
import liaison.linkit.team.domain.announcement.AnnouncementAdvertisingLog;
import liaison.linkit.team.domain.announcement.TeamMemberAnnouncement;
import liaison.linkit.team.domain.repository.announcement.AnnouncementAdvertisingLogRepository;
import liaison.linkit.team.implement.announcement.TeamMemberAnnouncementCommandAdapter;
import liaison.linkit.team.implement.announcement.TeamMemberAnnouncementQueryAdapter;
import liaison.linkit.team.implement.teamMember.TeamMemberQueryAdapter;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class AnnouncementAdvertiseBatchConfig {

    // 스프링 배치 의존성
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    // 어댑터 및 서비스 의존성
    private final ProfileQueryAdapter profileQueryAdapter;
    private final TeamMemberAnnouncementQueryAdapter teamMemberAnnouncementQueryAdapter;
    private final TeamMemberAnnouncementCommandAdapter teamMemberAnnouncementCommandAdapter;
    private final TeamMemberQueryAdapter teamMemberQueryAdapter;
    private final AnnouncementCommonAssembler announcementCommonAssembler;
    private final AsyncAnnouncementAdvertiseEmailService asyncAnnouncementAdvertiseEmailService;
    private final AnnouncementAdvertisingLogRepository announcementAdvertisingLogRepository;

    /** 모집 공고 광고 이메일 발송 작업 */
    @Bean
    public Job announcementAdvertiseJob() {
        return new JobBuilder("announcementAdvertiseJob", jobRepository)
                .start(announcementAdvertiseStep())
                .build();
    }

    /** 모집 공고 광고 이메일 발송 스텝 */
    @Bean
    public Step announcementAdvertiseStep() {
        return new StepBuilder("announcementAdvertiseStep", jobRepository)
                .<TeamMemberAnnouncement, AnnouncementAdvertiseDTO>chunk(100, transactionManager)
                .reader(advertiseAnnouncementReader(null))
                .processor(advertiseAnnouncementProcessor())
                .writer(advertiseAnnouncementWriter())
                .allowStartIfComplete(true)
                .build();
    }

    /** 광고 발송이 필요한 모집 공고를 조회하는 Reader 아직 광고 메일이 발송되지 않은 공고를 조회합니다. */
    @Bean
    @StepScope
    public ItemReader<TeamMemberAnnouncement> advertiseAnnouncementReader(
            @Value("#{jobParameters['time']}") Long time) {

        // 24시간 전 시간 계산
        LocalDateTime twentyFourHoursAgo =
                LocalDateTime.now(ZoneId.of("Asia/Seoul")).minusHours(24);
        log.info("광고 발송 대상 공고 조회 시작: 기준 시간 = {}", twentyFourHoursAgo);

        // 쿼리 어댑터를 사용해 조건에 맞는 공고 목록 조회
        List<TeamMemberAnnouncement> announcements =
                teamMemberAnnouncementQueryAdapter.findRecentPublicAnnouncementsNotAdvertised(
                        twentyFourHoursAgo);
        log.info("광고 발송 대상 공고 조회 완료: 총 {}개 공고 발견", announcements.size());

        // 리스트 기반 Reader 구현
        final AtomicInteger counter = new AtomicInteger(0);

        return () -> {
            int index = counter.getAndIncrement();
            if (index < announcements.size()) {
                return announcements.get(index);
            } else {
                return null; // 읽을 항목이 더 이상 없음을 표시
            }
        };
    }

    /** 모집 공고를 이메일 발송을 위한 DTO로 변환하는 Processor 마케팅 수신에 동의하고 포지션 대분류가 일치하는 회원을 대상으로 DTO를 생성합니다. */
    @Bean
    public ItemProcessor<TeamMemberAnnouncement, AnnouncementAdvertiseDTO>
            advertiseAnnouncementProcessor() {
        // 배치 ID 생성 (배치 실행 단위별 처리를 위한 식별자)
        final String batchId = UUID.randomUUID().toString();
        log.info("광고 발송 배치 시작: 배치 ID = {}", batchId);

        return announcement -> {
            try {
                // 이미 광고가 발송된 공고인지 확인 (중복 방지)
                if (announcementAdvertisingLogRepository.existsByTeamMemberAnnouncementId(
                        announcement.getId())) {
                    log.info("공고 ID {}: 이미 광고가 발송된 공고입니다.", announcement.getId());
                    return null;
                }

                // 공고의 포지션 정보 추출
                final TeamMemberAnnouncementResponseDTO.AnnouncementPositionItem positionItem =
                        announcementCommonAssembler.fetchAnnouncementPositionItem(announcement);

                // 공고의 스킬 정보 추출
                final List<TeamMemberAnnouncementResponseDTO.AnnouncementSkillName> skillNames =
                        announcementCommonAssembler.fetchAnnouncementSkills(announcement);

                // 포지션 대분류 추출
                final String majorPosition = positionItem.getMajorPosition();

                // 팀 정보 추출
                final String teamCode = announcement.getTeam().getTeamCode();
                final String teamName = announcement.getTeam().getTeamName();
                final String teamLogoPath = announcement.getTeam().getTeamLogoImagePath();

                // 메일을 보낼 대상 목록 조회 (마케팅 수신 동의한 사용자 중 포지션 대분류가 일치하는 사용자)
                List<Profile> receivers =
                        profileQueryAdapter.findByMarketingConsentAndMajorPosition(majorPosition);

                // 공고를 올린 팀의 멤버 ID 목록 조회
                List<Long> teamMemberIds = teamMemberQueryAdapter.getAllTeamMemberIds(teamCode);

                // 수신 대상 필터링 (팀 멤버 제외)
                List<Profile> filteredReceivers =
                        receivers.stream()
                                .filter(
                                        receiver ->
                                                !teamMemberIds.contains(
                                                        receiver.getMember().getId()))
                                .toList();

                log.info(
                        "공고 ID {}: 포지션 '{}' 매칭된 수신 대상자 {} 명",
                        announcement.getId(),
                        majorPosition,
                        filteredReceivers.size());

                // 수신 대상자가 없으면 처리 생략
                if (filteredReceivers.isEmpty()) {
                    log.info("공고 ID {}: 포지션 매칭된 수신 대상자가 없어 처리를 생략합니다.", announcement.getId());
                    return null;
                }

                // 스킬 이름만 추출
                List<String> skillNamesList =
                        skillNames.stream()
                                .map(
                                        TeamMemberAnnouncementResponseDTO.AnnouncementSkillName
                                                ::getAnnouncementSkillName)
                                .toList();

                // DTO 생성 및 반환
                return AnnouncementAdvertiseDTO.builder()
                        .announcementId(announcement.getId())
                        .announcementTitle(announcement.getAnnouncementTitle())
                        .teamName(teamName)
                        .teamCode(teamCode)
                        .teamLogoImagePath(teamLogoPath)
                        .majorPosition(positionItem.getMajorPosition())
                        .subPosition(positionItem.getSubPosition())
                        .skillNames(skillNamesList)
                        .batchId(batchId)
                        .receivers(filteredReceivers)
                        .build();
            } catch (Exception e) {
                log.error("공고 ID {}: 처리 중 오류 발생: {}", announcement.getId(), e.getMessage(), e);
                return null;
            }
        };
    }

    /** 이메일을 개별 발송하고 로그를 기록하는 Writer */
    @Bean
    @StepScope
    public ItemWriter<AnnouncementAdvertiseDTO> advertiseAnnouncementWriter() {
        return items -> {
            for (AnnouncementAdvertiseDTO item : items) {
                if (item == null || item.getReceivers() == null || item.getReceivers().isEmpty()) {
                    continue;
                }

                Long announcementId = item.getAnnouncementId();
                List<Profile> receivers = item.getReceivers();
                int receiverCount = receivers.size();

                log.info("공고 ID {}: {} 명에게 이메일 발송 시작", announcementId, receiverCount);

                // 성공 및 실패 카운트
                AtomicInteger successCount = new AtomicInteger(0);
                AtomicInteger failCount = new AtomicInteger(0);

                // 각 수신자에게 개별 이메일 발송
                sendEmailsToReceivers(item, receivers, successCount, failCount);

                // 발송 결과 처리 및 로그 저장
                processEmailSendingResults(
                        item, announcementId, successCount, failCount, receiverCount);
            }
        };
    }

    /** 각 수신자에게 이메일을 비동기적으로 발송하는 메서드 */
    private void sendEmailsToReceivers(
            AnnouncementAdvertiseDTO item,
            List<Profile> receivers,
            AtomicInteger successCount,
            AtomicInteger failCount) {

        for (Profile receiver : receivers) {
            try {
                // AsyncAnnouncementAdvertiseEmailService를 통해 이메일 발송
                asyncAnnouncementAdvertiseEmailService.sendAnnouncementAdvertiseEmail(
                        receiver.getMember().getEmail(), // 수신자 이메일
                        item.getMajorPosition(), // 포지션 대분류
                        item.getSubPosition(), // 포지션 소분류
                        item.getTeamCode(), // 팀 코드
                        item.getTeamLogoImagePath(), // 팀 로고 이미지 경로
                        item.getTeamName(), // 팀 이름
                        item.getAnnouncementTitle(), // 공고 제목
                        item.getSkillNames(), // 스킬 목록
                        item.getAnnouncementId() // 공고 ID
                        );

                successCount.incrementAndGet();
            } catch (Exception e) {
                log.error(
                        "이메일 발송 실패 - 공고 ID: {}, 수신자: {}, 오류: {}",
                        item.getAnnouncementId(),
                        receiver.getMember().getEmail(),
                        e.getMessage());
                failCount.incrementAndGet();
            }
        }
    }

    /** 이메일 발송 결과를 처리하고 로그를 저장하는 메서드 */
    private void processEmailSendingResults(
            AnnouncementAdvertiseDTO item,
            Long announcementId,
            AtomicInteger successCount,
            AtomicInteger failCount,
            int totalReceivers) {

        try {
            // 공고 상태 업데이트
            TeamMemberAnnouncement announcement =
                    teamMemberAnnouncementQueryAdapter.findById(announcementId);

            if (announcement != null && successCount.get() > 0) {
                // 하나 이상 성공적으로 발송되었으면 광고 발송 완료로 표시
                announcement.markAdvertisingMailAsSent();
                teamMemberAnnouncementCommandAdapter.addTeamMemberAnnouncement(announcement);

                // 성공 로그 생성 및 저장
                saveSuccessLog(announcementId, item.getBatchId());

                log.info(
                        "공고 ID {}: 광고 이메일 발송 완료 (총 {} 명 중 {} 명 성공, {} 명 실패)",
                        announcementId,
                        totalReceivers,
                        successCount.get(),
                        failCount.get());
            } else if (successCount.get() == 0) {
                // 모두 실패한 경우 실패 로그 기록
                saveFailureLog(announcementId, item.getBatchId(), "모든 이메일 발송 실패");

                log.error(
                        "공고 ID {}: 광고 이메일 발송 완전 실패 (대상자 {} 명 모두 실패)",
                        announcementId,
                        totalReceivers);
            }
        } catch (Exception e) {
            log.error("공고 ID {}: 상태 업데이트 또는 로그 저장 실패 - 오류: {}", announcementId, e.getMessage(), e);
        }
    }

    /** 이메일 발송 성공 로그를 저장하는 메서드 */
    private void saveSuccessLog(Long announcementId, String batchId) {
        AnnouncementAdvertisingLog log =
                AnnouncementAdvertisingLog.builder()
                        .teamMemberAnnouncementId(announcementId)
                        .sentAt(LocalDateTime.now(ZoneId.of("Asia/Seoul")))
                        .status("SUCCESS")
                        .batchId(batchId)
                        .build();

        announcementAdvertisingLogRepository.save(log);
    }

    /** 이메일 발송 실패 로그를 저장하는 메서드 */
    private void saveFailureLog(Long announcementId, String batchId, String failReason) {
        AnnouncementAdvertisingLog log =
                AnnouncementAdvertisingLog.builder()
                        .teamMemberAnnouncementId(announcementId)
                        .sentAt(LocalDateTime.now(ZoneId.of("Asia/Seoul")))
                        .status("FAILED")
                        .failReason(failReason)
                        .batchId(batchId)
                        .build();

        announcementAdvertisingLogRepository.save(log);
    }
}
