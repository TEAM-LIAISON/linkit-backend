package liaison.linkit.matching.service;

import java.util.ArrayList;
import java.util.List;
import liaison.linkit.matching.business.MatchingMapper;
import liaison.linkit.matching.domain.Matching;
import liaison.linkit.matching.domain.type.MatchingStatusType;
import liaison.linkit.matching.domain.type.ReceiverDeleteStatus;
import liaison.linkit.matching.domain.type.ReceiverReadStatus;
import liaison.linkit.matching.domain.type.ReceiverType;
import liaison.linkit.matching.domain.type.SenderType;
import liaison.linkit.matching.exception.CannotRequestMyAnnouncementException;
import liaison.linkit.matching.exception.CannotRequestMyProfileException;
import liaison.linkit.matching.exception.CompletedMatchingReadBadRequestException;
import liaison.linkit.matching.exception.MatchingReceiverBadRequestException;
import liaison.linkit.matching.exception.MatchingRelationBadRequestException;
import liaison.linkit.matching.exception.MatchingSenderBadRequestException;
import liaison.linkit.matching.exception.MatchingStatusTypeBadRequestException;
import liaison.linkit.matching.exception.NotAllowMatchingBadRequestException;
import liaison.linkit.matching.exception.ReceivedMatchingReadBadRequestException;
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
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.UpdateReceivedMatchingRequestedStateToReadItem;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.UpdateReceivedMatchingRequestedStateToReadItems;
import liaison.linkit.member.domain.Member;
import liaison.linkit.member.implement.MemberQueryAdapter;
import liaison.linkit.notification.domain.type.NotificationType;
import liaison.linkit.notification.presentation.dto.NotificationResponseDTO.NotificationDetails;
import liaison.linkit.notification.service.NotificationService;
import liaison.linkit.profile.business.mapper.ProfilePositionMapper;
import liaison.linkit.profile.domain.position.ProfilePosition;
import liaison.linkit.profile.domain.profile.Profile;
import liaison.linkit.profile.implement.position.ProfilePositionQueryAdapter;
import liaison.linkit.profile.implement.profile.ProfileQueryAdapter;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO.ProfilePositionDetail;
import liaison.linkit.team.business.mapper.announcement.AnnouncementSkillMapper;
import liaison.linkit.team.business.mapper.announcement.TeamMemberAnnouncementMapper;
import liaison.linkit.team.business.mapper.scale.TeamScaleMapper;
import liaison.linkit.team.domain.announcement.AnnouncementPosition;
import liaison.linkit.team.domain.announcement.AnnouncementSkill;
import liaison.linkit.team.domain.announcement.TeamMemberAnnouncement;
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

        // Profile 케이스
        if (receiverType == null || receiverType.equals(ReceiverType.PROFILE)) {
            final String emailId = memberQueryAdapter.findEmailIdById(memberId);
            final Page<Matching> profileMatchingItems = matchingQueryAdapter.findReceivedToProfile(emailId, pageable);

            if (receiverType != null) {
                return profileMatchingItems.map(this::toMatchingReceivedMenu);
            }

            combinedMatchingItems.addAll(profileMatchingItems.getContent());
        }

        // Team 케이스
        if (receiverType == null || receiverType.equals(ReceiverType.TEAM)) {
            final List<Team> teams = teamMemberQueryAdapter.getAllTeamsInOwnerStateByMemberId(memberId);
            final Page<Matching> teamMatchingItems = matchingQueryAdapter.findReceivedToTeam(teams, pageable);

            if (receiverType != null) {
                return teamMatchingItems.map(this::toMatchingReceivedMenu);
            }

            combinedMatchingItems.addAll(teamMatchingItems.getContent());
        }

        // Announcement 케이스
        if (receiverType == null || receiverType.equals(ReceiverType.ANNOUNCEMENT)) {
            final List<Team> teams = teamMemberQueryAdapter.getAllTeamsInOwnerStateByMemberId(memberId);

            final List<Long> teamIds = teams.stream()
                    .map(Team::getId)
                    .toList();

            final List<TeamMemberAnnouncement> teamMemberAnnouncements = teamMemberAnnouncementQueryAdapter.getAllByTeamIds(teamIds);
            final List<Long> announcementIds = teamMemberAnnouncements.stream()
                    .map(TeamMemberAnnouncement::getId)
                    .toList();

            final Page<Matching> announcementMatchingItems = matchingQueryAdapter.findReceivedToAnnouncement(announcementIds, pageable);

            if (receiverType != null) {
                return announcementMatchingItems.map(this::toMatchingReceivedMenu);
            }

            combinedMatchingItems.addAll(announcementMatchingItems.getContent());
        }

        // Null 케이스: Profile, Team, Announcement 데이터를 모두 병합
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

    public UpdateReceivedMatchingRequestedStateToReadItems updateReceivedMatchingRequestedStateToRead(final Long memberId, final UpdateReceivedMatchingReadRequest request) {
        List<Long> matchingIds = request.getMatchingIds();

        if (matchingIds == null || matchingIds.isEmpty()) {
            throw new IllegalArgumentException("Request must include valid matching IDs.");
        }

        List<Matching> matchings = matchingQueryAdapter.findAllByIds(matchingIds);

        if (matchings.isEmpty()) {
            throw new IllegalArgumentException("No matchings found for the given IDs: " + matchingIds);
        }

        if (!matchings.stream()
                .allMatch(matching -> matching.getMatchingStatusType().equals(MatchingStatusType.REQUESTED))) {
            throw ReceivedMatchingReadBadRequestException.EXCEPTION;
        }

        matchings.forEach(matching ->
                matching.setReceiverReadStatus(ReceiverReadStatus.READ_REQUESTED_MATCHING));

        matchingCommandAdapter.updateAll(matchings);

        List<UpdateReceivedMatchingRequestedStateToReadItem> updateReceivedMatchingRequestedStateToReadItems = matchings.stream()
                .map(matching -> new UpdateReceivedMatchingRequestedStateToReadItem(
                        matching.getId(),
                        matching.getReceiverReadStatus()
                ))
                .toList();

        return matchingMapper.toUpdateMatchingReceivedToReadItems(updateReceivedMatchingRequestedStateToReadItems);
    }

    public UpdateReceivedMatchingCompletedStateReadItems updateReceivedMatchingCompletedStateToRead(final Long memberId, final UpdateReceivedMatchingReadRequest request) {
        List<Long> matchingIds = request.getMatchingIds();

        if (matchingIds == null || matchingIds.isEmpty()) {
            throw new IllegalArgumentException("Request must include valid matching IDs.");
        }

        List<Matching> matchings = matchingQueryAdapter.findAllByIds(matchingIds);

        if (!matchings.stream()
                .allMatch(matching -> matching.getMatchingStatusType().equals(MatchingStatusType.COMPLETED))) {
            throw CompletedMatchingReadBadRequestException.EXCEPTION;
        }

        matchings.forEach(matching ->
                matching.setReceiverReadStatus(ReceiverReadStatus.READ_COMPLETED_MATCHING));

        matchingCommandAdapter.updateAll(matchings);

        List<UpdateReceivedMatchingCompletedStateReadItem> updateReceivedMatchingCompletedStateReadItems = matchings.stream()
                .map(matching -> new UpdateReceivedMatchingCompletedStateReadItem(
                        matching.getId(),
                        matching.getReceiverReadStatus()
                ))
                .toList();

        return matchingMapper.toUpdateMatchingCompletedToReadItems(updateReceivedMatchingCompletedStateReadItems);
    }

    public UpdateMatchingStatusTypeResponse updateMatchingStatusType(final Long memberId, final Long matchingId, final UpdateMatchingStatusTypeRequest updateMatchingStatusTypeRequest) {
        if (updateMatchingStatusTypeRequest == null || updateMatchingStatusTypeRequest.getMatchingStatusType().equals(MatchingStatusType.REQUESTED)) {
            throw MatchingStatusTypeBadRequestException.EXCEPTION;
        }

        final Matching matching = matchingQueryAdapter.findByMatchingId(matchingId);

        matchingCommandAdapter.updateMatchingStatusType(matching, updateMatchingStatusTypeRequest.getMatchingStatusType());

        return matchingMapper.toUpdateMatchingStatusTypeResponse(matching, updateMatchingStatusTypeRequest.getMatchingStatusType());
    }

    public DeleteRequestedMatchingItems deleteRequestedMatchingItems(final Long memberId, final DeleteRequestedMatchingRequest request) {
        List<Long> matchingIds = request.getMatchingIds();

        if (matchingIds == null || matchingIds.isEmpty()) {
            throw new IllegalArgumentException("Request must include valid matching IDs.");
        }

        List<Matching> matchings = matchingQueryAdapter.findAllByIds(matchingIds);

        matchings.forEach(matching ->
                matching.setReceiverDeleteStatus(ReceiverDeleteStatus.DELETED));

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

    public MatchingResponseDTO.AddMatchingResponse addMatching(final Long memberId, final MatchingRequestDTO.AddMatchingRequest addMatchingRequest) {

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
            if (teamMemberQueryAdapter.findMembersByTeamCode(team.getTeamCode()).contains(memberQueryAdapter.findById(memberId))) {
                throw CannotRequestMyAnnouncementException.EXCEPTION;
            }
        }

        final Matching matching = matchingMapper.toMatching(addMatchingRequest);
        matchingCommandAdapter.addMatching(matching);

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

        if (addMatchingRequest.getSenderType().equals(SenderType.TEAM)) {
            final Team senderTeam = teamQueryAdapter.findByTeamCode(addMatchingRequest.getSenderTeamCode());
            TeamScaleItem senderTeamScaleItem = new TeamScaleItem();
            if (teamScaleQueryAdapter.existsTeamScaleByTeamId(senderTeam.getId())) {
                final TeamScale teamScale = teamScaleQueryAdapter.findTeamScaleByTeamId(senderTeam.getId());
                senderTeamScaleItem = teamScaleMapper.toTeamScaleItem(teamScale);
            }

            senderTeamInformation = matchingMapper.toSenderTeamInformation(senderTeam, senderTeamScaleItem);
        }

        if (addMatchingRequest.getReceiverType().equals(ReceiverType.PROFILE)) {
            final Profile receiverProfile = profileQueryAdapter.findByEmailId(addMatchingRequest.getReceiverEmailId());
            ProfilePositionDetail receiverProfilePositionDetail = new ProfilePositionDetail();
            if (profilePositionQueryAdapter.existsProfilePositionByProfileId(receiverProfile.getId())) {
                final ProfilePosition profilePosition = profilePositionQueryAdapter.findProfilePositionByProfileId(receiverProfile.getId());
                receiverProfilePositionDetail = profilePositionMapper.toProfilePositionDetail(profilePosition);
            }

            receiverProfileInformation = matchingMapper.toReceiverProfileInformation(receiverProfile, receiverProfilePositionDetail);
        }

        if (addMatchingRequest.getReceiverType().equals(ReceiverType.TEAM)) {
            final Team receiverTeam = teamQueryAdapter.findByTeamCode(addMatchingRequest.getSenderTeamCode());
            TeamScaleItem receiverTeamScaleItem = new TeamScaleItem();
            if (teamScaleQueryAdapter.existsTeamScaleByTeamId(receiverTeam.getId())) {
                final TeamScale teamScale = teamScaleQueryAdapter.findTeamScaleByTeamId(receiverTeam.getId());
                receiverTeamScaleItem = teamScaleMapper.toTeamScaleItem(teamScale);
            }

            receiverTeamInformation = matchingMapper.toReceiverTeamInformation(receiverTeam, receiverTeamScaleItem);
        }

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

        NotificationDetails notificationDetails = NotificationDetails.matching(
                getSenderName(matching),
                getReceiverName(matching),
                matching.getMatchingStatusType().name()
        );

        Long receiverMemberId = getReceiverMemberId(matching);

        notificationService.sendNotification(
                receiverMemberId,
                NotificationType.MATCHING,
                notificationDetails
        );

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

        // (C) ReceivedMatchingMenu 빌드
        return matchingMapper.toMatchingReceivedMenu(matching, senderProfileInformation, senderTeamInformation, receiverProfileInformation, receiverTeamInformation, receiverAnnouncementInformation);
    }

    private RequestedMatchingMenu toMatchingRequestedMenu(final Matching matching) {
        // 발신자
        SenderProfileInformation senderProfileInformation = new SenderProfileInformation();
        SenderTeamInformation senderTeamInformation = new SenderTeamInformation();

        // 수신자
        ReceiverProfileInformation receiverProfileInformation = new ReceiverProfileInformation();
        ReceiverTeamInformation receiverTeamInformation = new ReceiverTeamInformation();
        ReceiverAnnouncementInformation receiverAnnouncementInformation = new ReceiverAnnouncementInformation();

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

        // (C) RequestedMatchingMenu 빌드
        return matchingMapper.toMatchingRequestedMenu(matching, senderProfileInformation, senderTeamInformation, receiverProfileInformation, receiverTeamInformation, receiverAnnouncementInformation);
    }


    // 발신자 이름 조회 헬퍼 메서드
    private String getSenderName(Matching matching) {
        if (matching.getSenderType() == SenderType.PROFILE) {
            Member member = memberQueryAdapter.findByEmailId(matching.getSenderEmailId());
            return member.getMemberBasicInform().getMemberName();
        } else {
            Team team = teamQueryAdapter.findByTeamCode(matching.getSenderTeamCode());
            return team.getTeamName();
        }
    }

    // 수신자 이름 조회 헬퍼 메서드
    private String getReceiverName(Matching matching) {
        switch (matching.getReceiverType()) {
            case PROFILE -> {
                Member member = memberQueryAdapter.findByEmailId(matching.getSenderEmailId());
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
}
