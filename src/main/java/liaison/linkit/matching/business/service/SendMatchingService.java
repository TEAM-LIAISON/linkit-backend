package liaison.linkit.matching.business.service;

import jakarta.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import liaison.linkit.chat.implement.ChatRoomQueryAdapter;
import liaison.linkit.mail.mapper.MatchingMailContentMapper;
import liaison.linkit.mail.service.AsyncMatchingEmailService;
import liaison.linkit.matching.business.assembler.SendMatchingModalAssembler;
import liaison.linkit.matching.business.mapper.MatchingMapper;
import liaison.linkit.matching.business.resolver.MatchingInfoResolver;
import liaison.linkit.matching.business.validator.MatchingValidator;
import liaison.linkit.matching.domain.Matching;
import liaison.linkit.matching.domain.MatchingEmailContent;
import liaison.linkit.matching.domain.type.ReceiverType;
import liaison.linkit.matching.domain.type.SenderDeleteStatus;
import liaison.linkit.matching.domain.type.SenderType;
import liaison.linkit.matching.implement.MatchingCommandAdapter;
import liaison.linkit.matching.implement.MatchingQueryAdapter;
import liaison.linkit.matching.presentation.dto.MatchingRequestDTO;
import liaison.linkit.matching.presentation.dto.MatchingRequestDTO.DeleteRequestedMatchingRequest;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.DeleteRequestedMatchingItem;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.DeleteRequestedMatchingItems;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.ReceiverAnnouncementInformation;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.ReceiverProfileInformation;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.ReceiverTeamInformation;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.RequestedMatchingMenu;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.SelectMatchingRequestToProfileMenu;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.SelectMatchingRequestToTeamMenu;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.SenderProfileInformation;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.SenderTeamInformation;
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
public class SendMatchingService {

    private final SendMatchingModalAssembler sendMatchingModalAssembler;
    private final MatchingQueryAdapter matchingQueryAdapter;
    private final MatchingCommandAdapter matchingCommandAdapter;
    private final MatchingMapper matchingMapper;
    private final MatchingValidator matchingValidator;
    private final MatchingInfoResolver matchingInfoResolver;
    private final MatchingMailContentMapper matchingMailContentMapper;
    private final AsyncMatchingEmailService asyncMatchingEmailService;
    private final ProfileQueryAdapter profileQueryAdapter;
    private final ProfilePositionQueryAdapter profilePositionQueryAdapter;
    private final ProfilePositionMapper profilePositionMapper;
    private final TeamQueryAdapter teamQueryAdapter;
    private final TeamScaleQueryAdapter teamScaleQueryAdapter;
    private final TeamScaleMapper teamScaleMapper;
    private final TeamMemberAnnouncementQueryAdapter teamMemberAnnouncementQueryAdapter;
    private final AnnouncementPositionQueryAdapter announcementPositionQueryAdapter;
    private final TeamMemberAnnouncementMapper teamMemberAnnouncementMapper;
    private final AnnouncementSkillQueryAdapter announcementSkillQueryAdapter;
    private final AnnouncementSkillMapper announcementSkillMapper;
    private final NotificationHandler notificationHandler;
    private final HeaderNotificationService headerNotificationService;
    private final MemberQueryAdapter memberQueryAdapter;
    private final TeamMemberQueryAdapter teamMemberQueryAdapter;
    private final ChatRoomQueryAdapter chatRoomQueryAdapter;

    // 개인에게 매칭 요청 보낼 때 모달 정보 조회
    @Transactional(readOnly = true)
    public SelectMatchingRequestToProfileMenu selectMatchingRequestToProfileMenu(final Long senderMemberId, final String receiverEmailId) {
        return sendMatchingModalAssembler.assembleSelectMatchingRequestToProfileMenu(senderMemberId, receiverEmailId);
    }

    // 팀에게 매칭 요청 보낼 때 모달 정보 조회
    @Transactional(readOnly = true)
    public SelectMatchingRequestToTeamMenu selectMatchingRequestToTeamMenu(final Long senderMemberId, final String receiverTeamCode) {
        return sendMatchingModalAssembler.assembleSelectMatchingRequestToTeamMenu(senderMemberId, receiverTeamCode);
    }

    // 매칭 발신함에서 매칭 데이터 삭제 처리
    public DeleteRequestedMatchingItems deleteRequestedMatchingItems(
        final DeleteRequestedMatchingRequest req
    ) {
        List<Long> matchingIds = req.getMatchingIds();
        List<Matching> matches = matchingQueryAdapter.findAllByIds(matchingIds);

        matches.forEach(matching ->
            matching.setSenderDeleteStatus(SenderDeleteStatus.DELETED));

        matchingCommandAdapter.updateAll(matches);

        List<DeleteRequestedMatchingItem> deleteRequestedMatchingItems = matchingMapper.toDeleteRequestedMatchingItemList(matches);
        return matchingMapper.toDeleteRequestedMatchingItems(deleteRequestedMatchingItems);
    }

    // 매칭 요청 발신 메서드
    public MatchingResponseDTO.AddMatchingResponse addMatching(
        final Long memberId,
        final MatchingRequestDTO.AddMatchingRequest addMatchingRequest
    ) throws MessagingException, UnsupportedEncodingException {
        matchingValidator.validateAddMatching(memberId, addMatchingRequest);

        final Matching matching = matchingMapper.toMatching(addMatchingRequest);
        final Matching savedMatching = matchingCommandAdapter.addMatching(matching);

        MatchingEmailContent emailContent = matchingMailContentMapper.generateMatchingRequestedEmailContent(matching);

        asyncMatchingEmailService.sendMatchingRequestedEmail(
            emailContent.getReceiverMailTitle(),
            emailContent.getReceiverMailSubTitle(),
            emailContent.getReceiverMailSubText(),

            matchingInfoResolver.getReceiverEmail(matching),
            matchingInfoResolver.getSenderName(matching),
            matchingInfoResolver.getSenderLogoImagePath(matching),
            matchingInfoResolver.getSenderPositionOrTeamSizeText(matching),
            matchingInfoResolver.getSenderPositionOrTeamSize(matching),
            matchingInfoResolver.getSenderRegionDetail(matching)
        );

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
            final Team receiverTeam = teamQueryAdapter.findByTeamCode(addMatchingRequest.getReceiverTeamCode());
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

        NotificationDetails requestedStateReceiverNotificationDetails =
            notificationHandler.generateRequestedStateReceiverNotificationDetails(savedMatching);
        notificationHandler.alertNewRequestedNotificationToReceiver(savedMatching, requestedStateReceiverNotificationDetails);
        headerNotificationService.publishNotificationCount(matchingInfoResolver.getReceiverMemberId(matching));

        return matchingMapper.toAddMatchingResponse(matching, senderProfileInformation, senderTeamInformation, receiverProfileInformation, receiverTeamInformation, receiverAnnouncementInformation);
    }


    public Page<RequestedMatchingMenu> getRequestedMatchingMenuResponse(final Long memberId, final SenderType senderType, Pageable pageable) {
        List<Matching> combinedMatchingItems = new ArrayList<>();

        // senderType이 ANNOUNCEMENT인 경우 빈 Page 반환
        if (senderType != null && senderType.equals(SenderType.ANNOUNCEMENT)) {
            return Page.empty(pageable);
        }

        // Profile 케이스
        if (senderType == null || senderType.equals(SenderType.PROFILE)) {
            final String emailId = memberQueryAdapter
                .findEmailIdById(memberId);
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
}
