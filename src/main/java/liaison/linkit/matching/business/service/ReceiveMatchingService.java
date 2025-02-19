package liaison.linkit.matching.business.service;

import jakarta.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import liaison.linkit.chat.implement.ChatRoomQueryAdapter;
import liaison.linkit.mail.mapper.MatchingMailContentMapper;
import liaison.linkit.mail.service.AsyncMatchingEmailService;
import liaison.linkit.matching.business.mapper.MatchingMapper;
import liaison.linkit.matching.business.resolver.MatchingInfoResolver;
import liaison.linkit.matching.business.validator.MatchingValidator;
import liaison.linkit.matching.domain.Matching;
import liaison.linkit.matching.domain.MatchingEmailContent;
import liaison.linkit.matching.domain.type.MatchingStatusType;
import liaison.linkit.matching.domain.type.ReceiverDeleteStatus;
import liaison.linkit.matching.domain.type.ReceiverReadStatus;
import liaison.linkit.matching.domain.type.ReceiverType;
import liaison.linkit.matching.domain.type.SenderType;
import liaison.linkit.matching.implement.MatchingCommandAdapter;
import liaison.linkit.matching.implement.MatchingQueryAdapter;
import liaison.linkit.matching.presentation.dto.MatchingRequestDTO.DeleteReceivedMatchingRequest;
import liaison.linkit.matching.presentation.dto.MatchingRequestDTO.UpdateMatchingStatusTypeRequest;
import liaison.linkit.matching.presentation.dto.MatchingRequestDTO.UpdateReceivedMatchingReadRequest;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.DeleteReceivedMatchingItem;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.DeleteReceivedMatchingItems;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.ReceivedMatchingMenu;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.ReceiverAnnouncementInformation;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.ReceiverProfileInformation;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.ReceiverTeamInformation;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.SenderProfileInformation;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.SenderTeamInformation;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.UpdateMatchingStatusTypeResponse;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.UpdateReceivedMatchingCompletedStateReadItem;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.UpdateReceivedMatchingCompletedStateReadItems;
import liaison.linkit.member.implement.MemberQueryAdapter;
import liaison.linkit.notification.business.handler.NotificationHandler;
import liaison.linkit.notification.presentation.dto.NotificationResponseDTO.NotificationDetails;
import liaison.linkit.notification.service.HeaderNotificationService;
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
public class ReceiveMatchingService {

    private final MatchingQueryAdapter matchingQueryAdapter;
    private final MatchingCommandAdapter matchingCommandAdapter;

    private final MatchingMapper matchingMapper;
    private final MemberQueryAdapter memberQueryAdapter;
    private final TeamMemberQueryAdapter teamMemberQueryAdapter;
    private final TeamMemberAnnouncementQueryAdapter teamMemberAnnouncementQueryAdapter;
    private final MatchingValidator matchingValidator;
    private final MatchingInfoResolver matchingInfoResolver;

    private final MatchingMailContentMapper matchingMailContentMapper;
    private final ChatRoomQueryAdapter chatRoomQueryAdapter;
    private final ProfileQueryAdapter profileQueryAdapter;
    private final ProfilePositionQueryAdapter profilePositionQueryAdapter;
    private final ProfilePositionMapper profilePositionMapper;
    private final TeamQueryAdapter teamQueryAdapter;
    private final TeamScaleQueryAdapter teamScaleQueryAdapter;
    private final TeamScaleMapper teamScaleMapper;

    private final NotificationHandler notificationHandler;
    private final AsyncMatchingEmailService asyncMatchingEmailService;
    private final HeaderNotificationService headerNotificationService;
    private final AnnouncementPositionQueryAdapter announcementPositionQueryAdapter;
    private final TeamMemberAnnouncementMapper teamMemberAnnouncementMapper;
    private final AnnouncementSkillQueryAdapter announcementSkillQueryAdapter;
    private final AnnouncementSkillMapper announcementSkillMapper;

    // 매칭 수신함에서 매칭 데이터 삭제 처리
    public DeleteReceivedMatchingItems deleteReceivedMatchingItems(
        final DeleteReceivedMatchingRequest req
    ) {
        List<Long> matchingIds = req.getMatchingIds();
        List<Matching> matches = matchingQueryAdapter.findAllByIds(matchingIds);

        matches.forEach(matching -> matching.setReceiverDeleteStatus(ReceiverDeleteStatus.DELETED));
        matchingCommandAdapter.updateAll(matches);

        List<DeleteReceivedMatchingItem> deleteReceivedMatchingItems = matchingMapper.toDeleteReceivedMatchingItemList(matches);
        return matchingMapper.toDeleteReceivedMatchingItems(deleteReceivedMatchingItems);
    }

    // 매칭 수신함에서 읽음 처리
    public UpdateReceivedMatchingCompletedStateReadItems updateReceivedMatchingStateToRead(
        final UpdateReceivedMatchingReadRequest request
    ) {
        List<Matching> matches = matchingQueryAdapter.findAllByIds(request.getMatchingIds());
        matches.forEach(this::updateMatchingReadStatus);

        matchingCommandAdapter.updateAll(matches);

        List<UpdateReceivedMatchingCompletedStateReadItem> readItems = matchingMapper.toUpdateReceivedMatchingCompletedStateReadItems(matches);
        return matchingMapper.toUpdateMatchingCompletedToReadItems(readItems);
    }

    // 매칭 상태에 따라 읽음 처리
    private void updateMatchingReadStatus(Matching matching) {
        MatchingStatusType statusType = matching.getMatchingStatusType();

        Map<MatchingStatusType, ReceiverReadStatus> statusMapping = Map.of(
            MatchingStatusType.REQUESTED, ReceiverReadStatus.READ_REQUESTED_MATCHING,
            MatchingStatusType.COMPLETED, ReceiverReadStatus.READ_COMPLETED_MATCHING
        );

        if (statusMapping.containsKey(statusType)) {
            ReceiverReadStatus newReadStatus = statusMapping.get(statusType);
            if (matching.getReceiverReadStatus() != newReadStatus) {
                matching.setReceiverReadStatus(newReadStatus);
            }
        }
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

    // 수신자가 매칭 요청 상태를 업데이트한다.
    public UpdateMatchingStatusTypeResponse updateMatchingStatusType(
        final Long memberId, final Long matchingId, final UpdateMatchingStatusTypeRequest req
    ) throws MessagingException, UnsupportedEncodingException {

        matchingValidator.validateUpdateStatusRequest(req);

        final Matching matching = matchingQueryAdapter.findByMatchingId(matchingId);
        matchingValidator.ensureReceiverAuthorized(memberId, matchingInfoResolver.getReceiverMemberId(matching));

        matchingCommandAdapter.updateMatchingStatusType(matching, req.getMatchingStatusType());

        // COMPLETED 상태로 변경된 경우 비동기 이메일 발송
        if (req.getMatchingStatusType() == MatchingStatusType.COMPLETED) {
            MatchingEmailContent emailContent = matchingMailContentMapper.generateMatchingCompletedEmailContent(matching);

            asyncMatchingEmailService.sendMatchingCompletedEmails(
                emailContent.getSenderMailTitle(),
                emailContent.getSenderMailSubTitle(),
                emailContent.getSenderMailSubText(),

                emailContent.getReceiverMailTitle(),
                emailContent.getReceiverMailSubTitle(),
                emailContent.getReceiverMailSubText(),

                matchingInfoResolver.getSenderEmail(matching),
                matchingInfoResolver.getSenderName(matching),
                matchingInfoResolver.getSenderLogoImagePath(matching),
                matchingInfoResolver.getSenderPositionOrTeamSizeText(matching),
                matchingInfoResolver.getSenderPositionOrTeamSize(matching),
                matchingInfoResolver.getSenderRegionDetail(matching),

                matchingInfoResolver.getReceiverEmail(matching),
                matchingInfoResolver.getReceiverName(matching),
                matchingInfoResolver.getReceiverLogoImagePath(matching),
                matchingInfoResolver.getReceiverPositionOrTeamSizeText(matching),
                matchingInfoResolver.getReceiverPositionOrTeamSize(matching),
                matchingInfoResolver.getReceiverRegionOrAnnouncementSkillText(matching),
                matchingInfoResolver.getReceiverRegionOrAnnouncementSkill(matching)
            );

            NotificationDetails acceptedStateReceiverNotificationDetails = notificationHandler.generateAcceptedStateReceiverNotificationDetails(matching);
            notificationHandler.alertNewAcceptedNotificationToReceiver(matching, acceptedStateReceiverNotificationDetails);

            headerNotificationService.publishNotificationCount(matchingInfoResolver.getReceiverMemberId(matching));

            // 매칭 성사된 경우, 발신자에게 알림 발송
            NotificationDetails acceptedStateSenderNotificationDetails =
                notificationHandler.generateAcceptedStateSenderNotificationDetails(matching);
            notificationHandler.alertNewAcceptedNotificationToSender(matching, acceptedStateSenderNotificationDetails);

            headerNotificationService.publishNotificationCount(matchingInfoResolver.getSenderMemberId(matching));
        } else {
            NotificationDetails rejectedStateReceiverNotificationDetails = notificationHandler.generatedRejectedStateSenderNotificationDetails(matching);
            notificationHandler.alertNewRejectedNotificationToSender(matching, rejectedStateReceiverNotificationDetails);
            headerNotificationService.publishNotificationCount(matchingInfoResolver.getSenderMemberId(matching));
        }

        return matchingMapper.toUpdateMatchingStatusTypeResponse(matching, req.getMatchingStatusType());
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
}
