package liaison.linkit.matching.service;

import jakarta.mail.MessagingException;
import java.util.ArrayList;
import java.util.List;
import liaison.linkit.chat.implement.ChatRoomQueryAdapter;
import liaison.linkit.common.business.RegionMapper;
import liaison.linkit.common.implement.RegionQueryAdapter;
import liaison.linkit.common.presentation.RegionResponseDTO.RegionDetail;
import liaison.linkit.mail.service.AsyncMatchingEmailService;
import liaison.linkit.matching.business.MatchingMapper;
import liaison.linkit.matching.domain.Matching;
import liaison.linkit.matching.domain.MatchingEmailContent;
import liaison.linkit.matching.domain.type.MatchingStatusType;
import liaison.linkit.matching.domain.type.ReceiverDeleteStatus;
import liaison.linkit.matching.domain.type.ReceiverReadStatus;
import liaison.linkit.matching.domain.type.ReceiverType;
import liaison.linkit.matching.domain.type.SenderDeleteStatus;
import liaison.linkit.matching.domain.type.SenderType;
import liaison.linkit.matching.exception.CannotRequestMyAnnouncementException;
import liaison.linkit.matching.exception.CannotRequestMyProfileException;
import liaison.linkit.matching.exception.MatchingReceiverBadRequestException;
import liaison.linkit.matching.exception.MatchingRelationBadRequestException;
import liaison.linkit.matching.exception.MatchingSenderBadRequestException;
import liaison.linkit.matching.exception.MatchingStatusTypeBadRequestException;
import liaison.linkit.matching.exception.NotAllowMatchingBadRequestException;
import liaison.linkit.matching.exception.UpdateMatchingStatusTypeBadRequestException;
import liaison.linkit.matching.implement.MatchingCommandAdapter;
import liaison.linkit.matching.implement.MatchingQueryAdapter;
import liaison.linkit.matching.presentation.dto.MatchingRequestDTO;
import liaison.linkit.matching.presentation.dto.MatchingRequestDTO.DeleteReceivedMatchingRequest;
import liaison.linkit.matching.presentation.dto.MatchingRequestDTO.DeleteRequestedMatchingRequest;
import liaison.linkit.matching.presentation.dto.MatchingRequestDTO.UpdateMatchingStatusTypeRequest;
import liaison.linkit.matching.presentation.dto.MatchingRequestDTO.UpdateReceivedMatchingReadRequest;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.DeleteReceivedMatchingItem;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.DeleteReceivedMatchingItems;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.DeleteRequestedMatchingItem;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.DeleteRequestedMatchingItems;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.MatchingNotificationMenu;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.ReceivedMatchingMenu;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.ReceiverAnnouncementInformation;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.ReceiverProfileInformation;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.ReceiverTeamInformation;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.RequestedMatchingMenu;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.SelectMatchingRequestToProfileMenu;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.SelectMatchingRequestToTeamMenu;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.SenderProfileInformation;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.SenderTeamInformation;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.UpdateMatchingStatusTypeResponse;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.UpdateReceivedMatchingCompletedStateReadItem;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.UpdateReceivedMatchingCompletedStateReadItems;
import liaison.linkit.member.domain.Member;
import liaison.linkit.member.implement.MemberQueryAdapter;
import liaison.linkit.notification.business.NotificationMapper;
import liaison.linkit.notification.domain.type.NotificationType;
import liaison.linkit.notification.domain.type.SubNotificationType;
import liaison.linkit.notification.presentation.dto.NotificationResponseDTO.NotificationDetails;
import liaison.linkit.notification.service.HeaderNotificationService;
import liaison.linkit.notification.service.NotificationService;
import liaison.linkit.profile.business.mapper.ProfilePositionMapper;
import liaison.linkit.profile.domain.position.ProfilePosition;
import liaison.linkit.profile.domain.profile.Profile;
import liaison.linkit.profile.domain.region.ProfileRegion;
import liaison.linkit.profile.implement.position.ProfilePositionQueryAdapter;
import liaison.linkit.profile.implement.profile.ProfileQueryAdapter;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO.ProfilePositionDetail;
import liaison.linkit.team.business.mapper.announcement.AnnouncementSkillMapper;
import liaison.linkit.team.business.mapper.announcement.TeamMemberAnnouncementMapper;
import liaison.linkit.team.business.mapper.scale.TeamScaleMapper;
import liaison.linkit.team.domain.announcement.AnnouncementPosition;
import liaison.linkit.team.domain.announcement.AnnouncementSkill;
import liaison.linkit.team.domain.announcement.TeamMemberAnnouncement;
import liaison.linkit.team.domain.region.TeamRegion;
import liaison.linkit.team.domain.scale.TeamScale;
import liaison.linkit.team.domain.team.Team;
import liaison.linkit.team.implement.announcement.AnnouncementPositionQueryAdapter;
import liaison.linkit.team.implement.announcement.AnnouncementSkillQueryAdapter;
import liaison.linkit.team.implement.announcement.TeamMemberAnnouncementQueryAdapter;
import liaison.linkit.team.implement.scale.TeamScaleQueryAdapter;
import liaison.linkit.team.implement.team.TeamQueryAdapter;
import liaison.linkit.team.implement.teamMember.TeamMemberQueryAdapter;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO.AnnouncementPositionItem;
import liaison.linkit.team.presentation.team.dto.TeamResponseDTO.TeamScaleItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MatchingService {

    private final MatchingCommandAdapter matchingCommandAdapter;
    private final MatchingQueryAdapter matchingQueryAdapter;

    private final MatchingMapper matchingMapper;

    private final TeamQueryAdapter teamQueryAdapter;

    private final TeamMemberQueryAdapter teamMemberQueryAdapter;
    private final TeamMemberAnnouncementQueryAdapter teamMemberAnnouncementQueryAdapter;
    private final MemberQueryAdapter memberQueryAdapter;
    private final ProfileQueryAdapter profileQueryAdapter;
    private final ProfilePositionQueryAdapter profilePositionQueryAdapter;
    private final ProfilePositionMapper profilePositionMapper;
    private final TeamScaleQueryAdapter teamScaleQueryAdapter;
    private final TeamScaleMapper teamScaleMapper;
    private final AnnouncementPositionQueryAdapter announcementPositionQueryAdapter;
    private final TeamMemberAnnouncementMapper teamMemberAnnouncementMapper;
    private final AnnouncementSkillQueryAdapter announcementSkillQueryAdapter;
    private final AnnouncementSkillMapper announcementSkillMapper;

    private final NotificationService notificationService;
    private final ChatRoomQueryAdapter chatRoomQueryAdapter;
    private final AsyncMatchingEmailService asyncMatchingEmailService;
    private final RegionQueryAdapter regionQueryAdapter;
    private final RegionMapper regionMapper;
    private final NotificationMapper notificationMapper;
    private final HeaderNotificationService headerNotificationService;

    @Transactional(readOnly = true)
    public SelectMatchingRequestToProfileMenu selectMatchingRequestToProfileMenu(final Long memberId, final String emailId) {
        // 1. 프로필 조회
        final Profile senderProfile = profileQueryAdapter.findByMemberId(memberId);

        if (senderProfile.getMember().getEmailId().equals(emailId)) {
            throw NotAllowMatchingBadRequestException.EXCEPTION;
        }

        log.info("Selecting matching request to profile {}", senderProfile);
        ProfilePositionDetail senderProfilePositionDetail = new ProfilePositionDetail();

        if (profilePositionQueryAdapter.existsProfilePositionByProfileId(senderProfile.getId())) {
            final ProfilePosition profilePosition = profilePositionQueryAdapter.findProfilePositionByProfileId(senderProfile.getId());
            senderProfilePositionDetail = profilePositionMapper.toProfilePositionDetail(profilePosition);
        }

        // 2. 팀 정보 조회 및 변환
        List<SenderTeamInformation> senderTeamInformations = new ArrayList<>();
        boolean isTeamInformationExists = false;

        if (teamMemberQueryAdapter.existsTeamOwnerByMemberId(memberId)) {
            isTeamInformationExists = true;
            final List<Team> teams = teamMemberQueryAdapter.getAllTeamsInOwnerStateByMemberId(memberId);
            senderTeamInformations = teams.stream()
                    .map(team -> SenderTeamInformation.builder()
                            .teamCode(team.getTeamCode())
                            .teamName(team.getTeamName())
                            .teamLogoImagePath(team.getTeamLogoImagePath())
                            .build())
                    .toList();
        }

        final Profile receiverProfile = profileQueryAdapter.findByEmailId(emailId);
        log.info("Selecting matching request to profile {}", receiverProfile);
        ProfilePositionDetail receiverProfilePositionDetail = new ProfilePositionDetail();
        if (profilePositionQueryAdapter.existsProfilePositionByProfileId(receiverProfile.getId())) {
            final ProfilePosition profilePosition = profilePositionQueryAdapter.findProfilePositionByProfileId(receiverProfile.getId());
            receiverProfilePositionDetail = profilePositionMapper.toProfilePositionDetail(profilePosition);
        }

        return matchingMapper.toSelectMatchingRequestToProfileMenu(
                isTeamInformationExists,
                senderProfile,
                senderProfilePositionDetail,
                senderTeamInformations,
                receiverProfile,
                receiverProfilePositionDetail
        );
    }

    @Transactional(readOnly = true)
    public SelectMatchingRequestToTeamMenu selectMatchingRequestToTeamMenu(final Long memberId, final String teamCode) {
        // 1. 프로필 조회
        final Profile senderProfile = profileQueryAdapter.findByMemberId(memberId);

        ProfilePositionDetail senderProfilePositionDetail = new ProfilePositionDetail();
        if (profilePositionQueryAdapter.existsProfilePositionByProfileId(senderProfile.getId())) {
            final ProfilePosition profilePosition = profilePositionQueryAdapter.findProfilePositionByProfileId(senderProfile.getId());
            senderProfilePositionDetail = profilePositionMapper.toProfilePositionDetail(profilePosition);
        }

        // 2. 팀 정보 조회 및 변환
        List<SenderTeamInformation> senderTeamInformations = new ArrayList<>();
        boolean isTeamInformationExists = false;

        if (teamMemberQueryAdapter.existsTeamOwnerByMemberId(memberId)) {
            isTeamInformationExists = true;
            final List<Team> teams = teamMemberQueryAdapter.getAllTeamsInOwnerStateByMemberId(memberId);
            senderTeamInformations = teams.stream()
                    .map(team -> {
                        TeamScaleItem teamScaleItem = new TeamScaleItem();
                        if (teamScaleQueryAdapter.existsTeamScaleByTeamId(team.getId())) {
                            final TeamScale teamScale = teamScaleQueryAdapter.findTeamScaleByTeamId(team.getId());
                            teamScaleItem = teamScaleMapper.toTeamScaleItem(teamScale);
                        }

                        return SenderTeamInformation.builder()
                                .teamCode(team.getTeamCode())
                                .teamName(team.getTeamName())
                                .teamLogoImagePath(team.getTeamLogoImagePath())
                                .teamScaleItem(teamScaleItem)
                                .build();
                    }).toList();

        }

        final Team receiverTeam = teamQueryAdapter.findByTeamCode(teamCode);

        TeamScaleItem receiveTeamScaleItem = null;
        if (teamScaleQueryAdapter.existsTeamScaleByTeamId(receiverTeam.getId())) {
            final TeamScale teamScale = teamScaleQueryAdapter.findTeamScaleByTeamId(receiverTeam.getId());
            receiveTeamScaleItem = teamScaleMapper.toTeamScaleItem(teamScale);
        }

        return matchingMapper.toSelectMatchingRequestTeamMenu(isTeamInformationExists, senderProfile, senderProfilePositionDetail, senderTeamInformations, receiverTeam, receiveTeamScaleItem);
    }

    // 매칭 상단 알림 조회 (수정 필요)
    @Transactional(readOnly = true)
    public MatchingNotificationMenu getMatchingNotificationMenu(final Long memberId) {
        int receivedMatchingNotificationCount = 0;
        int requestedMatchingNotificationCount = 0;

        // 내 프로필에 대한 수신함 우선 판단
        /**
         *  내가 가지고 있는 memberId에 대하여 Matching 엔티티에서 emailId와 동일한 값을 찾는다
         *
         */

        // 해당 회원이 오너인 팀이 존재한다면
        if (teamMemberQueryAdapter.existsTeamOwnerByMemberId(memberId)) {
            // 해당 회원이 오너로 등록된 팀들의 teamCode를 가져온다.
            final List<Team> myTeams = teamMemberQueryAdapter.getAllTeamsInOwnerStateByMemberId(memberId);

            // 2. 팀 코드 목록 추출
            List<String> myTeamCodes = myTeams.stream()
                    .map(Team::getTeamCode)
                    .toList();

            // "TEAM" 매칭(= 팀으로 수신) 건수
            receivedMatchingNotificationCount += matchingQueryAdapter.countByReceiverTeamCodes(myTeamCodes);

            // 2-2) 팀 ID 목록
            List<Long> myTeamIds = myTeams.stream()
                    .map(Team::getId)
                    .toList();

            // 3) 팀이 만든 공고 목록 -> 공고 ID 목록
            List<TeamMemberAnnouncement> announcements =
                    teamMemberAnnouncementQueryAdapter.getAllByTeamIds(myTeamIds);

            List<Long> announcementIds = announcements.stream()
                    .map(TeamMemberAnnouncement::getId)
                    .toList();

            // "ANNOUNCEMENT" 매칭(= 공고로 수신) 건수
            receivedMatchingNotificationCount += matchingQueryAdapter.countByReceiverAnnouncementIds(announcementIds);
        }

        return matchingMapper.toMatchingMenuResponse(receivedMatchingNotificationCount, requestedMatchingNotificationCount);
    }

    public Page<ReceivedMatchingMenu> getReceivedMatchingMenuResponse(final Long memberId, final ReceiverType receiverType, Pageable pageable) {
        List<Matching> combinedMatchingItems = new ArrayList<>();
        log.info("Start getReceivedMatchingMenuResponse: memberId={}, receiverType={}", memberId, receiverType);

        // Profile 케이스
        if (receiverType == null || receiverType.equals(ReceiverType.PROFILE)) {
            final String emailId = memberQueryAdapter.findEmailIdById(memberId);
            log.info("Profile case: emailId={}", emailId);

            final Page<Matching> profileMatchingItems = matchingQueryAdapter.findReceivedToProfile(emailId, pageable);
            log.info("Profile matching items size: {}", profileMatchingItems.getContent().size());

            if (receiverType != null) {
                log.info("Returning Profile case directly.");
                return profileMatchingItems.map(this::toMatchingReceivedMenu);
            }

            combinedMatchingItems.addAll(profileMatchingItems.getContent());
        }

        // Team 케이스
        if (receiverType == null || receiverType.equals(ReceiverType.TEAM)) {
            final List<Team> teams = teamMemberQueryAdapter.getAllTeamsInOwnerStateByMemberId(memberId);
            log.info("Team case: teams size={}", teams.size());

            final Page<Matching> teamMatchingItems = matchingQueryAdapter.findReceivedToTeam(teams, pageable);
            log.info("Team matching items size: {}", teamMatchingItems.getContent().size());

            if (receiverType != null) {
                log.info("Returning Team case directly.");
                return teamMatchingItems.map(this::toMatchingReceivedMenu);
            }

            combinedMatchingItems.addAll(teamMatchingItems.getContent());
        }

        // Announcement 케이스
        if (receiverType == null || receiverType.equals(ReceiverType.ANNOUNCEMENT)) {
            final List<Team> teams = teamMemberQueryAdapter.getAllTeamsInOwnerStateByMemberId(memberId);
            log.info("Announcement case: teams size={}", teams.size());

            final List<Long> teamIds = teams.stream()
                    .map(Team::getId)
                    .toList();
            log.info("Team IDs size: {}", teamIds.size());

            final List<TeamMemberAnnouncement> teamMemberAnnouncements = teamMemberAnnouncementQueryAdapter.getAllByTeamIds(teamIds);
            log.info("TeamMemberAnnouncements size: {}", teamMemberAnnouncements.size());

            final List<Long> announcementIds = teamMemberAnnouncements.stream()
                    .map(TeamMemberAnnouncement::getId)
                    .toList();
            log.info("Announcement IDs size: {}", announcementIds.size());

            final Page<Matching> announcementMatchingItems = matchingQueryAdapter.findReceivedToAnnouncement(announcementIds, pageable);
            log.info("Announcement matching items size: {}", announcementMatchingItems.getContent().size());

            if (receiverType != null) {
                log.info("Returning Announcement case directly.");
                return announcementMatchingItems.map(this::toMatchingReceivedMenu);
            }

            combinedMatchingItems.addAll(announcementMatchingItems.getContent());
        }

        // Null 케이스: Profile, Team, Announcement 데이터를 모두 병합
        log.info("Combining all matching items: combined size={}", combinedMatchingItems.size());
        return new PageImpl<>(
                combinedMatchingItems.stream()
                        .map(this::toMatchingReceivedMenu)
                        .toList(),
                pageable,
                combinedMatchingItems.size()
        );
    }

    public Page<RequestedMatchingMenu> getRequestedMatchingMenuResponse(final Long memberId, final SenderType senderType, Pageable pageable) {
        List<Matching> combinedMatchingItems = new ArrayList<>();

        // senderType이 ANNOUNCEMENT인 경우 빈 Page 반환
        if (senderType != null && senderType.equals(SenderType.ANNOUNCEMENT)) {
            return Page.empty(pageable);
        }

        // Profile 케이스
        if (senderType == null || senderType.equals(SenderType.PROFILE)) {
            final String emailId = memberQueryAdapter.findEmailIdById(memberId);
            final Page<Matching> profileMatchingItems = matchingQueryAdapter.findRequestedByProfile(emailId, pageable);

            if (senderType != null) {
                return profileMatchingItems.map(this::toMatchingRequestedMenu);
            }

            combinedMatchingItems.addAll(profileMatchingItems.getContent());
        }

        // Team 케이스
        if (senderType == null || senderType.equals(SenderType.TEAM)) {
            final List<Team> teams = teamMemberQueryAdapter.getAllTeamsInOwnerStateByMemberId(memberId);
            final Page<Matching> teamMatchingItems = matchingQueryAdapter.findRequestedByTeam(teams, pageable);

            if (senderType != null) {
                return teamMatchingItems.map(this::toMatchingRequestedMenu);
            }

            combinedMatchingItems.addAll(teamMatchingItems.getContent());
        }

        // Null 케이스: Profile, Team, Announcement 데이터를 모두 병합
        return new PageImpl<>(
                combinedMatchingItems.stream()
                        .map(this::toMatchingRequestedMenu)
                        .toList(),
                pageable,
                combinedMatchingItems.size()
        );
    }

    public UpdateReceivedMatchingCompletedStateReadItems updateReceivedMatchingStateToRead(final Long memberId, final UpdateReceivedMatchingReadRequest request) {
        // 1) 요청 검증
        List<Long> matchingIds = request.getMatchingIds();
        if (matchingIds == null || matchingIds.isEmpty()) {
            throw new IllegalArgumentException("Request must include valid matching IDs.");
        }

        // 2) 매칭 조회
        List<Matching> matchings = matchingQueryAdapter.findAllByIds(matchingIds);
        if (matchings.isEmpty()) {
            throw new IllegalArgumentException("No matchings found for the given IDs: " + matchingIds);
        }

        // 3) 상태별로 읽음 처리
        for (Matching matching : matchings) {
            MatchingStatusType statusType = matching.getMatchingStatusType();

            switch (statusType) {
                case REQUESTED:
                    // 이미 READ_REQUESTED_MATCHING 상태인지 확인
                    if (matching.getReceiverReadStatus() != ReceiverReadStatus.READ_REQUESTED_MATCHING) {
                        matching.setReceiverReadStatus(ReceiverReadStatus.READ_REQUESTED_MATCHING);
                    }
                    break;

                case COMPLETED:
                    // 이미 READ_COMPLETED_MATCHING 상태인지 확인
                    if (matching.getReceiverReadStatus() != ReceiverReadStatus.READ_COMPLETED_MATCHING) {
                        matching.setReceiverReadStatus(ReceiverReadStatus.READ_COMPLETED_MATCHING);
                    }
                    break;

                default:
                    // REQUESTED, COMPLETED가 아닌 경우 별도 처리
                    // 예: "READ_REQUESTED_MATCHING와 READ_COMPLETED_MATCHING은 REQUESTED 또는 COMPLETED 상태에서만 적용 가능합니다."
                    // 필요하다면 로그 남기거나, 예외를 던질 수 있음
                    log.warn("Matching ID {} has status {}, which is not handled by read logic.",
                            matching.getId(), statusType);
                    break;
            }
        }

        // 4) DB에 반영 (bulk update)
        matchingCommandAdapter.updateAll(matchings);

        // 5) 응답 DTO 구성
        List<UpdateReceivedMatchingCompletedStateReadItem> readItems = matchings.stream()
                .map(m -> new UpdateReceivedMatchingCompletedStateReadItem(
                        m.getId(),
                        m.getReceiverReadStatus()
                ))
                .toList();

        return matchingMapper.toUpdateMatchingCompletedToReadItems(readItems);
    }

    // 수신자가 매칭 요청 상태를 업데이트한다.
    public UpdateMatchingStatusTypeResponse updateMatchingStatusType(
            final Long memberId, final Long matchingId, final UpdateMatchingStatusTypeRequest updateMatchingStatusTypeRequest
    ) throws MessagingException {
        log.info("updateMatchingStatusType 호출: memberId={}, matchingId={}, updateRequest={}",
                memberId, matchingId, updateMatchingStatusTypeRequest);

        if (updateMatchingStatusTypeRequest == null || updateMatchingStatusTypeRequest.getMatchingStatusType().equals(MatchingStatusType.REQUESTED)) {
            log.error("Invalid updateMatchingStatusTypeRequest: {}", updateMatchingStatusTypeRequest);
            throw MatchingStatusTypeBadRequestException.EXCEPTION;
        }

        final Matching matching = matchingQueryAdapter.findByMatchingId(matchingId);
        log.info("Matching 정보 조회 성공: {}", matching);

        if (!getReceiverMemberId(matching).equals(memberId)) {
            log.error("수신자 ID 불일치: 요청자 ID={}, 수신자 ID={}", memberId, getReceiverMemberId(matching));
            throw UpdateMatchingStatusTypeBadRequestException.EXCEPTION;
        }

        matchingCommandAdapter.updateMatchingStatusType(matching, updateMatchingStatusTypeRequest.getMatchingStatusType());
        log.info("매칭 상태 업데이트 성공: matchingId={}, statusType={}", matchingId, updateMatchingStatusTypeRequest.getMatchingStatusType());

        // COMPLETED 상태로 변경된 경우 비동기 이메일 발송
        if (updateMatchingStatusTypeRequest.getMatchingStatusType() == MatchingStatusType.COMPLETED) {
            log.info("매칭 상태 COMPLETED - 이메일 발송 준비");
            MatchingEmailContent emailContent = generateMatchingCompletedEmailContent(matching);

            asyncMatchingEmailService.sendMatchingCompletedEmails(
                    emailContent.getSenderMailTitle(),
                    emailContent.getSenderMailSubTitle(),
                    emailContent.getSenderMailSubText(),

                    emailContent.getReceiverMailTitle(),
                    emailContent.getReceiverMailSubTitle(),
                    emailContent.getReceiverMailSubText(),

                    getSenderEmail(matching),
                    getSenderName(matching),
                    getSenderLogoImagePath(matching),
                    getSenderPositionOrTeamSizeText(matching),
                    getSenderPositionOrTeamSize(matching),
                    getSenderRegionDetail(matching),

                    getReceiverEmail(matching),
                    getReceiverName(matching),
                    getReceiverLogoImagePath(matching),
                    getReceiverPositionOrTeamSizeText(matching),
                    getReceiverPositionOrTeamSize(matching),
                    getReceiverRegionOrAnnouncementSkillText(matching),
                    getReceiverRegionOrAnnouncementSkill(matching)
            );
            log.info("이메일 발송 완료");

            // 매칭 성사된 경우, 수신자에게 알림 발송
            NotificationDetails receiverNotificationDetails = NotificationDetails.matchingAccepted(
                    matchingId,
                    getSenderLogoImagePath(matching),
                    getSenderName(matching)
            );

            notificationService.alertNewNotification(
                    notificationMapper.toNotification(
                            getReceiverMemberId(matching),
                            NotificationType.MATCHING,
                            SubNotificationType.MATCHING_ACCEPTED,
                            receiverNotificationDetails
                    )
            );
            log.info("수신자에게 알림 발송 완료: memberId={}, matchingId={}", getReceiverMemberId(matching), matchingId);

            headerNotificationService.publishNotificationCount(getReceiverMemberId(matching));
            log.info("수신자 헤더 알림 카운트 업데이트 완료: memberId={}", getReceiverMemberId(matching));

            // 매칭 성사된 경우, 발신자에게 알림 발송
            NotificationDetails senderNotificationDetails = NotificationDetails.matchingAccepted(
                    matchingId,
                    getReceiverLogoImagePath(matching),
                    getReceiverName(matching)
            );

            notificationService.alertNewNotification(
                    notificationMapper.toNotification(
                            getSenderMemberId(matching),
                            NotificationType.MATCHING,
                            SubNotificationType.MATCHING_ACCEPTED,
                            senderNotificationDetails
                    )
            );
            log.info("발신자에게 알림 발송 완료: memberId={}, matchingId={}", getSenderMemberId(matching), matchingId);

            headerNotificationService.publishNotificationCount(getSenderMemberId(matching));
            log.info("발신자 헤더 알림 카운트 업데이트 완료: memberId={}", getSenderMemberId(matching));
        } else {
            NotificationDetails senderRejectedNotificationDetails = NotificationDetails.matchingRejected(
                    matchingId,
                    getReceiverLogoImagePath(matching),
                    getReceiverName(matching)
            );

            notificationService.alertNewNotification(
                    notificationMapper.toNotification(
                            getSenderMemberId(matching),
                            NotificationType.MATCHING,
                            SubNotificationType.MATCHING_REJECTED,
                            senderRejectedNotificationDetails
                    )
            );

            headerNotificationService.publishNotificationCount(getSenderMemberId(matching));
        }

        UpdateMatchingStatusTypeResponse response = matchingMapper.toUpdateMatchingStatusTypeResponse(matching, updateMatchingStatusTypeRequest.getMatchingStatusType());
        log.info("매칭 상태 업데이트 응답 생성 완료: {}", response);

        return response;
    }


    public DeleteRequestedMatchingItems deleteRequestedMatchingItems(final Long memberId, final DeleteRequestedMatchingRequest request) {
        List<Long> matchingIds = request.getMatchingIds();

        if (matchingIds == null || matchingIds.isEmpty()) {
            throw new IllegalArgumentException("Request must include valid matching IDs.");
        }

        List<Matching> matchings = matchingQueryAdapter.findAllByIds(matchingIds);

        matchings.forEach(matching ->
                matching.setSenderDeleteStatus(SenderDeleteStatus.DELETED));

        matchingCommandAdapter.updateAll(matchings);

        List<DeleteRequestedMatchingItem> deleteRequestedMatchingItems = matchings.stream()
                .map(matching -> new DeleteRequestedMatchingItem(
                        matching.getId(),
                        matching.getSenderDeleteStatus()
                ))
                .toList();

        return matchingMapper.toDeleteRequestedMatchingItems(deleteRequestedMatchingItems);
    }

    public DeleteReceivedMatchingItems deleteReceivedMatchingItems(final Long memberId, final DeleteReceivedMatchingRequest request) {
        List<Long> matchingIds = request.getMatchingIds();

        if (matchingIds == null || matchingIds.isEmpty()) {
            throw new IllegalArgumentException("Request must include valid matching IDs.");
        }

        List<Matching> matchings = matchingQueryAdapter.findAllByIds(matchingIds);

        matchings.forEach(matching ->
                matching.setReceiverDeleteStatus(ReceiverDeleteStatus.DELETED));

        matchingCommandAdapter.updateAll(matchings);

        List<DeleteReceivedMatchingItem> deleteReceivedMatchingItems = matchings.stream()
                .map(matching -> new DeleteReceivedMatchingItem(
                        matching.getId(),
                        matching.getReceiverDeleteStatus()
                ))
                .toList();

        return matchingMapper.toDeleteReceivedMatchingItems(deleteReceivedMatchingItems);
    }

    public MatchingResponseDTO.AddMatchingResponse addMatching(final Long memberId, final MatchingRequestDTO.AddMatchingRequest addMatchingRequest) throws MessagingException {

        if (addMatchingRequest.getSenderTeamCode() != null && addMatchingRequest.getReceiverAnnouncementId() != null) {
            throw MatchingRelationBadRequestException.EXCEPTION;
        }

        if (addMatchingRequest.getSenderType().equals(SenderType.PROFILE)) {
            if (addMatchingRequest.getSenderEmailId() == null) {
                throw MatchingSenderBadRequestException.EXCEPTION;
            }
        }

        if (addMatchingRequest.getSenderType().equals(SenderType.TEAM)) {
            if (addMatchingRequest.getSenderTeamCode() == null) {
                throw MatchingSenderBadRequestException.EXCEPTION;
            }
        }

        if (addMatchingRequest.getReceiverType().equals(ReceiverType.PROFILE)) {
            if (addMatchingRequest.getReceiverEmailId() == null) {
                throw MatchingReceiverBadRequestException.EXCEPTION;
            }

            final Profile profile = profileQueryAdapter.findByEmailId(addMatchingRequest.getReceiverEmailId());
            if (profile.getMember().getId().equals(memberId)) {
                throw CannotRequestMyProfileException.EXCEPTION;
            }
        }

        if (addMatchingRequest.getReceiverType().equals(ReceiverType.TEAM)) {
            if (addMatchingRequest.getReceiverTeamCode() == null) {
                throw MatchingReceiverBadRequestException.EXCEPTION;
            }

            if (teamMemberQueryAdapter.findMembersByTeamCode(addMatchingRequest.getReceiverTeamCode()).contains(memberQueryAdapter.findById(memberId))) {
                throw CannotRequestMyProfileException.EXCEPTION;
            }
        }

        if (addMatchingRequest.getReceiverType().equals(ReceiverType.ANNOUNCEMENT)) {
            if (addMatchingRequest.getReceiverAnnouncementId() == null) {
                throw MatchingReceiverBadRequestException.EXCEPTION;
            }
            final Team team = teamMemberAnnouncementQueryAdapter.getTeamMemberAnnouncement(addMatchingRequest.getReceiverAnnouncementId()).getTeam();
            log.info("team 공고 지원의 팀 이름: {}", team.getTeamName());
            if (teamMemberQueryAdapter.findMembersByTeamCode(team.getTeamCode()).contains(memberQueryAdapter.findById(memberId))) {
                log.info("팀원에 속해있어요.");
                throw CannotRequestMyAnnouncementException.EXCEPTION;
            }
        }

        final Matching matching = matchingMapper.toMatching(addMatchingRequest);
        log.info("Add matching 0: " + matching);
        final Matching savedMatching = matchingCommandAdapter.addMatching(matching);

        log.info("Add matching 0-1: " + matching);

        MatchingEmailContent emailContent = generateMatchingRequestedEmailContent(matching);

        asyncMatchingEmailService.sendMatchingRequestedEmail(
                emailContent.getReceiverMailTitle(),
                emailContent.getReceiverMailSubTitle(),
                emailContent.getReceiverMailSubText(),

                getReceiverEmail(matching),
                getSenderName(matching),
                getSenderLogoImagePath(matching),
                getSenderPositionOrTeamSizeText(matching),
                getSenderPositionOrTeamSize(matching),
                getSenderRegionDetail(matching)
        );

        log.info("Add matching 0-2: " + matching);

        SenderProfileInformation senderProfileInformation = new SenderProfileInformation();
        SenderTeamInformation senderTeamInformation = new SenderTeamInformation();
        ReceiverProfileInformation receiverProfileInformation = new ReceiverProfileInformation();
        ReceiverTeamInformation receiverTeamInformation = new ReceiverTeamInformation();
        ReceiverAnnouncementInformation receiverAnnouncementInformation = new ReceiverAnnouncementInformation();

        if (addMatchingRequest.getSenderType().equals(SenderType.PROFILE)) {
            final Profile senderProfile = profileQueryAdapter.findByEmailId(addMatchingRequest.getSenderEmailId());
            ProfilePositionDetail senderProfilePositionDetail = new ProfilePositionDetail();

            if (profilePositionQueryAdapter.existsProfilePositionByProfileId(senderProfile.getId())) {
                final ProfilePosition profilePosition = profilePositionQueryAdapter.findProfilePositionByProfileId(senderProfile.getId());
                senderProfilePositionDetail = profilePositionMapper.toProfilePositionDetail(profilePosition);
            }

            senderProfileInformation = matchingMapper.toSenderProfileInformation(senderProfile, senderProfilePositionDetail);
        }

        log.info("Adding new matching 1: " + matching);

        if (addMatchingRequest.getSenderType().equals(SenderType.TEAM)) {
            final Team senderTeam = teamQueryAdapter.findByTeamCode(addMatchingRequest.getSenderTeamCode());
            TeamScaleItem senderTeamScaleItem = new TeamScaleItem();
            if (teamScaleQueryAdapter.existsTeamScaleByTeamId(senderTeam.getId())) {
                final TeamScale teamScale = teamScaleQueryAdapter.findTeamScaleByTeamId(senderTeam.getId());
                senderTeamScaleItem = teamScaleMapper.toTeamScaleItem(teamScale);
            }

            senderTeamInformation = matchingMapper.toSenderTeamInformation(senderTeam, senderTeamScaleItem);
        }

        log.info("Adding new matching 2: " + matching);

        if (addMatchingRequest.getReceiverType().equals(ReceiverType.PROFILE)) {
            final Profile receiverProfile = profileQueryAdapter.findByEmailId(addMatchingRequest.getReceiverEmailId());
            ProfilePositionDetail receiverProfilePositionDetail = new ProfilePositionDetail();
            if (profilePositionQueryAdapter.existsProfilePositionByProfileId(receiverProfile.getId())) {
                final ProfilePosition profilePosition = profilePositionQueryAdapter.findProfilePositionByProfileId(receiverProfile.getId());
                receiverProfilePositionDetail = profilePositionMapper.toProfilePositionDetail(profilePosition);
            }

            receiverProfileInformation = matchingMapper.toReceiverProfileInformation(receiverProfile, receiverProfilePositionDetail);
        }

        log.info("Adding new matching 3: " + matching);

        if (addMatchingRequest.getReceiverType().equals(ReceiverType.TEAM)) {
            final Team receiverTeam = teamQueryAdapter.findByTeamCode(addMatchingRequest.getReceiverTeamCode());
            TeamScaleItem receiverTeamScaleItem = new TeamScaleItem();
            if (teamScaleQueryAdapter.existsTeamScaleByTeamId(receiverTeam.getId())) {
                final TeamScale teamScale = teamScaleQueryAdapter.findTeamScaleByTeamId(receiverTeam.getId());
                receiverTeamScaleItem = teamScaleMapper.toTeamScaleItem(teamScale);
            }

            receiverTeamInformation = matchingMapper.toReceiverTeamInformation(receiverTeam, receiverTeamScaleItem);
        }

        log.info("Adding new matching 4: " + matching);

        if (addMatchingRequest.getReceiverType().equals(ReceiverType.ANNOUNCEMENT)) {
            final TeamMemberAnnouncement receiverAnnouncement = teamMemberAnnouncementQueryAdapter.getTeamMemberAnnouncement(addMatchingRequest.getReceiverAnnouncementId());

            // 포지션 조회
            AnnouncementPositionItem announcementPositionItem = new AnnouncementPositionItem();
            if (announcementPositionQueryAdapter.existsAnnouncementPositionByTeamMemberAnnouncementId(receiverAnnouncement.getId())) {
                AnnouncementPosition announcementPosition = announcementPositionQueryAdapter.findAnnouncementPositionByTeamMemberAnnouncementId(receiverAnnouncement.getId());
                announcementPositionItem = teamMemberAnnouncementMapper.toAnnouncementPositionItem(announcementPosition);
            }

            // 스킬 조회
            List<AnnouncementSkill> announcementSkills = announcementSkillQueryAdapter.getAnnouncementSkills(receiverAnnouncement.getId());
            List<TeamMemberAnnouncementResponseDTO.AnnouncementSkillName> announcementSkillNames = announcementSkillMapper.toAnnouncementSkillNames(announcementSkills);

            receiverAnnouncementInformation = matchingMapper.toReceiverAnnouncementInformation(receiverAnnouncement, announcementPositionItem, announcementSkillNames);
        }

        log.info("Adding new matching 5: " + matching);

        log.info("getSenderLogoImagePath: " + getSenderLogoImagePath(matching));

        final NotificationDetails receiverNotificationDetails = NotificationDetails.matchingRequested(
                // matchingTargetName 필요
                savedMatching.getId(),
                getSenderLogoImagePath(matching),
                getSenderName(matching)
        );

        log.info("receiverNotificationDetails: " + receiverNotificationDetails);

        notificationService.alertNewNotification(
                notificationMapper.toNotification(
                        getReceiverMemberId(matching),
                        NotificationType.MATCHING,
                        SubNotificationType.MATCHING_REQUESTED,
                        receiverNotificationDetails
                )
        );

        log.info("Adding new matching 6: " + matching);

        headerNotificationService.publishNotificationCount(getReceiverMemberId(matching));

        log.info("Adding new matching 7: " + matching);

        return matchingMapper.toAddMatchingResponse(matching, senderProfileInformation, senderTeamInformation, receiverProfileInformation, receiverTeamInformation, receiverAnnouncementInformation);
    }

    private ReceivedMatchingMenu toMatchingReceivedMenu(final Matching matching) {
        // 발신자
        SenderProfileInformation senderProfileInformation = new SenderProfileInformation();
        SenderTeamInformation senderTeamInformation = new SenderTeamInformation();

        // 수신자
        ReceiverProfileInformation receiverProfileInformation = new ReceiverProfileInformation();
        ReceiverTeamInformation receiverTeamInformation = new ReceiverTeamInformation();
        ReceiverAnnouncementInformation receiverAnnouncementInformation = new ReceiverAnnouncementInformation();

        Long chatRoomId = null;
        if (chatRoomQueryAdapter.existsChatRoomByMatchingId(matching.getId())) {
            chatRoomId = chatRoomQueryAdapter.getChatRoomIdByMatchingId(matching.getId());
        }

        // (A) 발신자 정보 설정
        if (matching.getSenderType() == SenderType.PROFILE) {
            // 1) senderEmailId로 Profile 조회
            Profile senderProfile = profileQueryAdapter.findByEmailId(matching.getSenderEmailId());
            // 2) 프로필 포지션 조회
            ProfilePositionDetail senderProfilePositionDetail = new ProfilePositionDetail();
            if (profilePositionQueryAdapter.existsProfilePositionByProfileId(senderProfile.getId())) {
                ProfilePosition profilePosition =
                        profilePositionQueryAdapter.findProfilePositionByProfileId(senderProfile.getId());
                senderProfilePositionDetail = profilePositionMapper.toProfilePositionDetail(profilePosition);
            }
            // 3) senderProfileInfo 구성
            senderProfileInformation = matchingMapper.toSenderProfileInformation(senderProfile, senderProfilePositionDetail);
        } else {
            // 1) senderTeamCode로 Team 조회
            Team senderTeam = teamQueryAdapter.findByTeamCode(matching.getSenderTeamCode());
            // 2) 팀 규모 조회
            TeamScaleItem senderTeamScaleItem = new TeamScaleItem();
            if (teamScaleQueryAdapter.existsTeamScaleByTeamId(senderTeam.getId())) {
                TeamScale teamScale = teamScaleQueryAdapter.findTeamScaleByTeamId(senderTeam.getId());
                senderTeamScaleItem = teamScaleMapper.toTeamScaleItem(teamScale);
            }
            // 3) senderTeamInfo 구성
            senderTeamInformation = matchingMapper.toSenderTeamInformation(senderTeam, senderTeamScaleItem);
        }

        // (B) 수신자 정보 설정
        if (matching.getReceiverType() == ReceiverType.PROFILE) {
            // 1) receiverEmailId로 Profile 조회
            Profile receiverProfile = profileQueryAdapter.findByEmailId(matching.getReceiverEmailId());
            // 2) 프로필 포지션 조회
            ProfilePositionDetail receiverProfilePositionDetail = new ProfilePositionDetail();
            if (profilePositionQueryAdapter.existsProfilePositionByProfileId(receiverProfile.getId())) {
                ProfilePosition profilePosition =
                        profilePositionQueryAdapter.findProfilePositionByProfileId(receiverProfile.getId());
                receiverProfilePositionDetail = profilePositionMapper.toProfilePositionDetail(profilePosition);
            }
            // 3) receiverProfileInfo 구성
            receiverProfileInformation = matchingMapper.toReceiverProfileInformation(receiverProfile, receiverProfilePositionDetail);
        } else if (matching.getReceiverType() == ReceiverType.TEAM) {
            // 1) receiverTeamCode로 Team 조회
            Team receiverTeam = teamQueryAdapter.findByTeamCode(matching.getReceiverTeamCode());
            // 2) 팀 규모 조회
            TeamScaleItem receiverTeamScaleItem = new TeamScaleItem();
            if (teamScaleQueryAdapter.existsTeamScaleByTeamId(receiverTeam.getId())) {
                TeamScale teamScale = teamScaleQueryAdapter.findTeamScaleByTeamId(receiverTeam.getId());
                receiverTeamScaleItem = teamScaleMapper.toTeamScaleItem(teamScale);
            }
            // 3) receiverTeamInfo 구성
            receiverTeamInformation = matchingMapper.toReceiverTeamInformation(receiverTeam, receiverTeamScaleItem);
        } else if (matching.getReceiverType() == ReceiverType.ANNOUNCEMENT) {
            final TeamMemberAnnouncement receiverAnnouncement = teamMemberAnnouncementQueryAdapter.getTeamMemberAnnouncement(matching.getReceiverAnnouncementId());

            // 포지션 조회
            AnnouncementPositionItem announcementPositionItem = new AnnouncementPositionItem();
            if (announcementPositionQueryAdapter.existsAnnouncementPositionByTeamMemberAnnouncementId(receiverAnnouncement.getId())) {
                AnnouncementPosition announcementPosition = announcementPositionQueryAdapter.findAnnouncementPositionByTeamMemberAnnouncementId(receiverAnnouncement.getId());
                announcementPositionItem = teamMemberAnnouncementMapper.toAnnouncementPositionItem(announcementPosition);
            }

            // 스킬 조회
            List<AnnouncementSkill> announcementSkills = announcementSkillQueryAdapter.getAnnouncementSkills(receiverAnnouncement.getId());
            List<TeamMemberAnnouncementResponseDTO.AnnouncementSkillName> announcementSkillNames = announcementSkillMapper.toAnnouncementSkillNames(announcementSkills);

            receiverAnnouncementInformation = matchingMapper.toReceiverAnnouncementInformation(receiverAnnouncement, announcementPositionItem, announcementSkillNames);

        }

        return matchingMapper.toMatchingReceivedMenu(matching,
                chatRoomId, senderProfileInformation, senderTeamInformation, receiverProfileInformation, receiverTeamInformation,
                receiverAnnouncementInformation);
    }

    private RequestedMatchingMenu toMatchingRequestedMenu(final Matching matching) {
        // 발신자
        SenderProfileInformation senderProfileInformation = new SenderProfileInformation();
        SenderTeamInformation senderTeamInformation = new SenderTeamInformation();

        // 수신자
        ReceiverProfileInformation receiverProfileInformation = new ReceiverProfileInformation();
        ReceiverTeamInformation receiverTeamInformation = new ReceiverTeamInformation();
        ReceiverAnnouncementInformation receiverAnnouncementInformation = new ReceiverAnnouncementInformation();

        Long chatRoomId = null;
        if (chatRoomQueryAdapter.existsChatRoomByMatchingId(matching.getId())) {
            chatRoomId = chatRoomQueryAdapter.getChatRoomIdByMatchingId(matching.getId());
        }

        // (A) 발신자 정보 설정 (senderXxx)
        if (matching.getSenderType() == SenderType.PROFILE) {
            Profile senderProfile = profileQueryAdapter.findByEmailId(matching.getSenderEmailId());
            ProfilePositionDetail senderProfilePositionDetail = new ProfilePositionDetail();
            if (profilePositionQueryAdapter.existsProfilePositionByProfileId(senderProfile.getId())) {
                ProfilePosition profilePosition =
                        profilePositionQueryAdapter.findProfilePositionByProfileId(senderProfile.getId());
                senderProfilePositionDetail = profilePositionMapper.toProfilePositionDetail(profilePosition);
            }
            senderProfileInformation = matchingMapper.toSenderProfileInformation(senderProfile, senderProfilePositionDetail);
        } else {
            Team senderTeam = teamQueryAdapter.findByTeamCode(matching.getSenderTeamCode());
            TeamScaleItem senderTeamScaleItem = new TeamScaleItem();
            if (teamScaleQueryAdapter.existsTeamScaleByTeamId(senderTeam.getId())) {
                TeamScale teamScale = teamScaleQueryAdapter.findTeamScaleByTeamId(senderTeam.getId());
                senderTeamScaleItem = teamScaleMapper.toTeamScaleItem(teamScale);
            }
            senderTeamInformation = matchingMapper.toSenderTeamInformation(senderTeam, senderTeamScaleItem);
        }

        // (B) 수신자 정보 설정 (receiverXxx)
        if (matching.getReceiverType() == ReceiverType.PROFILE) {
            Profile receiverProfile = profileQueryAdapter.findByEmailId(matching.getReceiverEmailId());
            ProfilePositionDetail receiverProfilePositionDetail = new ProfilePositionDetail();
            if (profilePositionQueryAdapter.existsProfilePositionByProfileId(receiverProfile.getId())) {
                ProfilePosition profilePosition =
                        profilePositionQueryAdapter.findProfilePositionByProfileId(receiverProfile.getId());
                receiverProfilePositionDetail = profilePositionMapper.toProfilePositionDetail(profilePosition);
            }
            receiverProfileInformation = matchingMapper.toReceiverProfileInformation(receiverProfile, receiverProfilePositionDetail);
        } else if (matching.getReceiverType() == ReceiverType.TEAM) {
            Team receiverTeam = teamQueryAdapter.findByTeamCode(matching.getReceiverTeamCode());
            TeamScaleItem receiverTeamScaleItem = new TeamScaleItem();
            if (teamScaleQueryAdapter.existsTeamScaleByTeamId(receiverTeam.getId())) {
                TeamScale teamScale = teamScaleQueryAdapter.findTeamScaleByTeamId(receiverTeam.getId());
                receiverTeamScaleItem = teamScaleMapper.toTeamScaleItem(teamScale);
            }
            receiverTeamInformation = matchingMapper.toReceiverTeamInformation(receiverTeam, receiverTeamScaleItem);
        } else if (matching.getReceiverType() == ReceiverType.ANNOUNCEMENT) {
            final TeamMemberAnnouncement receiverAnnouncement = teamMemberAnnouncementQueryAdapter.getTeamMemberAnnouncement(matching.getReceiverAnnouncementId());

            // 포지션 조회
            AnnouncementPositionItem announcementPositionItem = new AnnouncementPositionItem();
            if (announcementPositionQueryAdapter.existsAnnouncementPositionByTeamMemberAnnouncementId(receiverAnnouncement.getId())) {
                AnnouncementPosition announcementPosition = announcementPositionQueryAdapter.findAnnouncementPositionByTeamMemberAnnouncementId(receiverAnnouncement.getId());
                announcementPositionItem = teamMemberAnnouncementMapper.toAnnouncementPositionItem(announcementPosition);
            }

            // 스킬 조회
            List<AnnouncementSkill> announcementSkills = announcementSkillQueryAdapter.getAnnouncementSkills(receiverAnnouncement.getId());
            List<TeamMemberAnnouncementResponseDTO.AnnouncementSkillName> announcementSkillNames = announcementSkillMapper.toAnnouncementSkillNames(announcementSkills);

            receiverAnnouncementInformation = matchingMapper.toReceiverAnnouncementInformation(receiverAnnouncement, announcementPositionItem, announcementSkillNames);
        }

        return matchingMapper.toMatchingRequestedMenu(matching, chatRoomId, senderProfileInformation, senderTeamInformation, receiverProfileInformation, receiverTeamInformation,
                receiverAnnouncementInformation);
    }


    // 발신자 이름 조회 헬퍼 메서드
    private String getSenderName(final Matching matching) {
        if (matching.getSenderType() == SenderType.PROFILE) {
            Member member = memberQueryAdapter.findByEmailId(matching.getSenderEmailId());
            return member.getMemberBasicInform().getMemberName();
        } else {
            final Team team = teamQueryAdapter.findByTeamCode(matching.getSenderTeamCode());
            log.info("team code " + team.getTeamCode());
            return team.getTeamName();
        }
    }

    // 수신자 이름 조회 헬퍼 메서드
    private String getReceiverName(Matching matching) {
        switch (matching.getReceiverType()) {
            case PROFILE -> {
                Member member = memberQueryAdapter.findByEmailId(matching.getReceiverEmailId());
                return member.getMemberBasicInform().getMemberName();
            }
            case TEAM -> {
                Team team = teamQueryAdapter.findByTeamCode(matching.getReceiverTeamCode());
                return team.getTeamName();
            }
            case ANNOUNCEMENT -> {
                TeamMemberAnnouncement announcement = teamMemberAnnouncementQueryAdapter
                        .getTeamMemberAnnouncement(matching.getReceiverAnnouncementId());
                return announcement.getTeam().getTeamName();
            }
            default -> throw new IllegalStateException("Unexpected receiver type: " + matching.getReceiverType());
        }
    }

    // 수신자 memberId 조회 헬퍼 메서드
    private Long getReceiverMemberId(Matching matching) {
        switch (matching.getReceiverType()) {
            case PROFILE -> {
                Profile profile = profileQueryAdapter.findByEmailId(matching.getReceiverEmailId());
                return profile.getMember().getId();
            }
            case TEAM, ANNOUNCEMENT -> {
                String teamCode = matching.getReceiverType() == ReceiverType.TEAM
                        ? matching.getReceiverTeamCode()
                        : teamMemberAnnouncementQueryAdapter
                                .getTeamMemberAnnouncement(matching.getReceiverAnnouncementId())
                                .getTeam()
                                .getTeamCode();
                return teamMemberQueryAdapter.findTeamOwnerByTeamCode(teamCode).getId();
            }
            default -> throw new IllegalStateException("Unexpected receiver type: " + matching.getReceiverType());
        }
    }

    private Long getSenderMemberId(final Matching matching) {
        if (matching.getSenderType() == SenderType.PROFILE) {
            return memberQueryAdapter.findByEmailId(matching.getSenderEmailId()).getId();
        } else {
            final Team team = teamQueryAdapter.findByTeamCode(matching.getSenderTeamCode());
            return teamMemberQueryAdapter.getTeamOwnerMemberId(team);
        }
    }

    // 발신자 로고 이미지 조회 헬퍼 메서드
    private String getSenderLogoImagePath(final Matching matching) {
        if (matching.getSenderType() == SenderType.PROFILE) {
            final Profile profile = profileQueryAdapter.findByEmailId(matching.getSenderEmailId());
            return profile.getProfileImagePath();
        } else {
            final Team team = teamQueryAdapter.findByTeamCode(matching.getSenderTeamCode());
            log.info("team logo image " + team.getTeamLogoImagePath());
            return team.getTeamLogoImagePath();
        }
    }

    // 수신자 로고 이미지 조회 헬퍼 메서드
    private String getReceiverLogoImagePath(final Matching matching) {
        switch (matching.getReceiverType()) {
            case PROFILE -> {
                Profile profile = profileQueryAdapter.findByEmailId(matching.getReceiverEmailId());
                return profile.getProfileImagePath();
            }
            case TEAM, ANNOUNCEMENT -> {
                String teamCode = matching.getReceiverType() == ReceiverType.TEAM
                        ? matching.getReceiverTeamCode()
                        : teamMemberAnnouncementQueryAdapter
                                .getTeamMemberAnnouncement(matching.getReceiverAnnouncementId())
                                .getTeam()
                                .getTeamCode();
                return teamQueryAdapter.findByTeamCode(teamCode).getTeamLogoImagePath();
            }
            default -> throw new IllegalStateException("Unexpected receiver type: " + matching.getReceiverType());
        }
    }

    // 발신자의 포지션 또는 규모 정보
    private String getSenderPositionOrTeamSize(final Matching matching) {
        if (matching.getSenderType() == SenderType.PROFILE) {
            final Profile profile = profileQueryAdapter.findByEmailId(matching.getSenderEmailId());

            ProfilePositionDetail senderProfilePositionDetail = new ProfilePositionDetail();

            if (profilePositionQueryAdapter.existsProfilePositionByProfileId(profile.getId())) {
                final ProfilePosition profilePosition = profilePositionQueryAdapter.findProfilePositionByProfileId(profile.getId());
                senderProfilePositionDetail = profilePositionMapper.toProfilePositionDetail(profilePosition);
            }

            return senderProfilePositionDetail.getMajorPosition();
        } else {
            final Team team = teamQueryAdapter.findByTeamCode(matching.getSenderTeamCode());
            TeamScaleItem teamScaleItem = null;
            if (teamScaleQueryAdapter.existsTeamScaleByTeamId(team.getId())) {
                final TeamScale teamScale = teamScaleQueryAdapter.findTeamScaleByTeamId(team.getId());
                teamScaleItem = teamScaleMapper.toTeamScaleItem(teamScale);
            }
            log.info("team code " + team.getTeamCode());

            return teamScaleItem.getTeamScaleName();
        }
    }

    // 수신자의 포지션 또는 규모 정보
    private String getReceiverPositionOrTeamSize(Matching matching) {
        switch (matching.getReceiverType()) {
            case PROFILE -> {
                final Profile profile = profileQueryAdapter.findByEmailId(matching.getReceiverEmailId());

                ProfilePositionDetail senderProfilePositionDetail = new ProfilePositionDetail();

                if (profilePositionQueryAdapter.existsProfilePositionByProfileId(profile.getId())) {
                    final ProfilePosition profilePosition = profilePositionQueryAdapter.findProfilePositionByProfileId(profile.getId());
                    senderProfilePositionDetail = profilePositionMapper.toProfilePositionDetail(profilePosition);
                }

                return senderProfilePositionDetail.getMajorPosition();
            }
            case TEAM, ANNOUNCEMENT -> {
                String teamCode = matching.getReceiverType() == ReceiverType.TEAM
                        ? matching.getReceiverTeamCode()
                        : teamMemberAnnouncementQueryAdapter
                                .getTeamMemberAnnouncement(matching.getReceiverAnnouncementId())
                                .getTeam()
                                .getTeamCode();
                final Team team = teamQueryAdapter.findByTeamCode(teamCode);
                // 팀 규모 조회
                TeamScaleItem teamScaleItem = null;
                if (teamScaleQueryAdapter.existsTeamScaleByTeamId(team.getId())) {
                    final TeamScale teamScale = teamScaleQueryAdapter.findTeamScaleByTeamId(team.getId());
                    teamScaleItem = teamScaleMapper.toTeamScaleItem(teamScale);
                }
                return teamScaleItem.getTeamScaleName();

            }
            default -> throw new IllegalStateException("Unexpected receiver type: " + matching.getReceiverType());
        }
    }

    // 발신자의 지역 정보
    private String getSenderRegionDetail(Matching matching) {
        if (matching.getSenderType() == SenderType.PROFILE) {
            final Profile profile = profileQueryAdapter.findByEmailId(matching.getSenderEmailId());
            // 팀 지역 조회
            RegionDetail regionDetail = new RegionDetail();
            if (regionQueryAdapter.existsProfileRegionByProfileId((profile.getId()))) {
                final ProfileRegion profileRegion = regionQueryAdapter.findProfileRegionByProfileId(profile.getId());
                regionDetail = regionMapper.toRegionDetail(profileRegion.getRegion());
            }
            return buildRegionString(regionDetail.getCityName(), regionDetail.getDivisionName());
        } else {
            final Team team = teamQueryAdapter.findByTeamCode(matching.getSenderTeamCode());
            RegionDetail regionDetail = new RegionDetail();
            if (regionQueryAdapter.existsTeamRegionByTeamId((team.getId()))) {
                final TeamRegion teamRegion = regionQueryAdapter.findTeamRegionByTeamId(team.getId());
                regionDetail = regionMapper.toRegionDetail(teamRegion.getRegion());
            }
            return buildRegionString(regionDetail.getCityName(), regionDetail.getDivisionName());
        }
    }

    // 수신자의 지역 정보
    private String getReceiverRegionOrAnnouncementSkill(Matching matching) {
        switch (matching.getReceiverType()) {
            case PROFILE -> {
                Profile profile = profileQueryAdapter.findByEmailId(matching.getReceiverEmailId());
                // 팀 지역 조회
                RegionDetail regionDetail = new RegionDetail();
                if (regionQueryAdapter.existsProfileRegionByProfileId((profile.getId()))) {
                    final ProfileRegion profileRegion = regionQueryAdapter.findProfileRegionByProfileId(profile.getId());
                    regionDetail = regionMapper.toRegionDetail(profileRegion.getRegion());
                }

                return buildRegionString(regionDetail.getCityName(), regionDetail.getDivisionName());
            }
            case TEAM -> {
                String teamCode = matching.getReceiverTeamCode();
                final Team team = teamQueryAdapter.findByTeamCode(teamCode);

                RegionDetail regionDetail = new RegionDetail();
                if (regionQueryAdapter.existsTeamRegionByTeamId((team.getId()))) {
                    final TeamRegion teamRegion = regionQueryAdapter.findTeamRegionByTeamId(team.getId());
                    regionDetail = regionMapper.toRegionDetail(teamRegion.getRegion());
                }
                return buildRegionString(regionDetail.getCityName(), regionDetail.getDivisionName());
            }
            case ANNOUNCEMENT -> {
                final TeamMemberAnnouncement teamMemberAnnouncement = teamMemberAnnouncementQueryAdapter.getTeamMemberAnnouncement(matching.getReceiverAnnouncementId());
                // 스킬 조회
                List<AnnouncementSkill> announcementSkills = announcementSkillQueryAdapter.getAnnouncementSkills(teamMemberAnnouncement.getId());
                List<TeamMemberAnnouncementResponseDTO.AnnouncementSkillName> announcementSkillNames = announcementSkillMapper.toAnnouncementSkillNames(announcementSkills);

                // 리스트가 비어있지 않다면 첫 번째 값을 반환
                if (!announcementSkillNames.isEmpty()) {
                    return announcementSkillNames.get(0).getAnnouncementSkillName();
                }

                // 리스트가 비어 있다면 null 반환 또는 예외 처리
                return null;
            }
            default -> throw new IllegalStateException("Unexpected receiver type: " + matching.getReceiverType());
        }
    }

    // 발신자의 이메일 정보
    private String getSenderEmail(Matching matching) {
        if (matching.getSenderType() == SenderType.PROFILE) {
            final Profile profile = profileQueryAdapter.findByEmailId(matching.getSenderEmailId());
            return profile.getMember().getEmail();
        } else {
            final Team team = teamQueryAdapter.findByTeamCode(matching.getSenderTeamCode());
            final Member member = teamMemberQueryAdapter.findTeamOwnerByTeamCode(team.getTeamCode());
            return member.getEmail();
        }
    }

    // 수신자의 이메일 정보
    private String getReceiverEmail(Matching matching) {
        switch (matching.getReceiverType()) {
            case PROFILE -> {
                Profile profile = profileQueryAdapter.findByEmailId(matching.getReceiverEmailId());
                log.info("Found profile email: " + profile.getMember().getId());
                return profile.getMember().getEmail();
            }
            case TEAM, ANNOUNCEMENT -> {
                String teamCode = matching.getReceiverType() == ReceiverType.TEAM
                        ? matching.getReceiverTeamCode()
                        : teamMemberAnnouncementQueryAdapter
                                .getTeamMemberAnnouncement(matching.getReceiverAnnouncementId())
                                .getTeam()
                                .getTeamCode();
                log.info("teamCode: " + teamCode);
                final Team team = teamQueryAdapter.findByTeamCode(teamCode);
                log.info("Team: " + team.getTeamName());
                final Member member = teamMemberQueryAdapter.findTeamOwnerByTeamCode(team.getTeamCode());

                log.info("member email: " + member.getEmail());
                return member.getEmail();
            }
            default -> throw new IllegalStateException("Unexpected receiver type: " + matching.getReceiverType());
        }
    }

    // 지역 정보를 문자열로 조합하는 유틸리티 메서드
    private String buildRegionString(String cityName, String divisionName) {
        if (cityName != null && divisionName != null) {
            return cityName + " " + divisionName;
        } else if (cityName != null) {
            return cityName;
        } else if (divisionName != null) {
            return divisionName;
        }
        return "";
    }

    private String getSenderPositionOrTeamSizeText(final Matching matching) {
        if (matching.getSenderType() == SenderType.PROFILE) {
            return "포지션";
        } else {
            return "규모";
        }
    }

    private String getReceiverPositionOrTeamSizeText(final Matching matching) {
        if (matching.getReceiverType() == ReceiverType.PROFILE) {
            return "포지션";
        } else {
            return "규모";
        }
    }

    private String getReceiverRegionOrAnnouncementSkillText(final Matching matching) {
        if (matching.getReceiverType() == ReceiverType.PROFILE) {
            return "지역";
        } else if (matching.getReceiverType() == ReceiverType.TEAM) {
            return "지역";
        } else if (matching.getReceiverType() == ReceiverType.ANNOUNCEMENT) {
            return "요구 스킬";
        }
        return null;
    }


    /**
     * 이메일 제목, 소제목, 세부 내용을 생성하는 메서드
     */
    private MatchingEmailContent generateMatchingCompletedEmailContent(Matching matching) {
        String senderMailTitle;
        String senderMailSubTitle;
        String senderMailSubText;
        String receiverMailTitle;
        String receiverMailSubTitle;
        String receiverMailSubText;

        if (matching.getReceiverType().equals(ReceiverType.ANNOUNCEMENT)) {
            senderMailTitle = "지원 수락";
            senderMailSubTitle = "팀의 지원 수락!";
            senderMailSubText = "지원이 수락되었어요";

            receiverMailTitle = "지원 수락";
            receiverMailSubTitle = "님의 지원 수락!";
            receiverMailSubText = "지원을 수락했어요";
        } else {
            senderMailTitle = "매칭 성사";
            senderMailSubTitle = generateSubTitle(matching.getSenderType(), matching.getReceiverType(), true);
            senderMailSubText = "매칭이 성사되었어요";

            receiverMailTitle = "매칭 성사";
            receiverMailSubTitle = generateSubTitle(matching.getSenderType(), matching.getReceiverType(), false);
            receiverMailSubText = "매칭이 성사되었어요";
        }

        return new MatchingEmailContent(
                senderMailTitle, senderMailSubTitle, senderMailSubText,
                receiverMailTitle, receiverMailSubTitle, receiverMailSubText
        );
    }

    private MatchingEmailContent generateMatchingRequestedEmailContent(Matching matching) {
        String receiverMailTitle;
        String receiverMailSubTitle;
        String receiverMailSubText;

        if (matching.getReceiverType().equals(ReceiverType.ANNOUNCEMENT)) {
            receiverMailTitle = "공고 지원";
            receiverMailSubTitle = "님의 공고 지원";
            receiverMailSubText = "새로운 지원이 왔어요";
        } else if (matching.getSenderType().equals(SenderType.TEAM)) {
            receiverMailTitle = "매칭 요청";
            receiverMailSubTitle = "팀의 매칭 요청";
            receiverMailSubText = "새로운 매칭 요청이 왔어요";
        } else {
            receiverMailTitle = "매칭 요청";
            receiverMailSubTitle = "님의 매칭 요청";
            receiverMailSubText = "새로운 매칭 요청이 왔어요";
        }

        return new MatchingEmailContent(
                receiverMailTitle, receiverMailSubTitle, receiverMailSubText
        );
    }

    /**
     * 이메일 소제목 생성
     */
    private String generateSubTitle(SenderType senderType, ReceiverType receiverType, boolean isSender) {
        if (senderType.equals(SenderType.PROFILE) && receiverType.equals(ReceiverType.PROFILE)) {
            return isSender ? "님과 매칭 성사" : "님과 매칭 성사";
        } else if (senderType.equals(SenderType.PROFILE) && receiverType.equals(ReceiverType.TEAM)) {
            return isSender ? "팀과 매칭 성사" : "님과 매칭 성사";
        } else if (senderType.equals(SenderType.TEAM) && receiverType.equals(ReceiverType.PROFILE)) {
            return isSender ? "님과 매칭 성사" : "팀과 매칭 성사";
        } else if (senderType.equals(SenderType.TEAM) && receiverType.equals(ReceiverType.TEAM)) {
            return "팀과 매칭 성사";
        }
        return "매칭 성사";
    }

}
