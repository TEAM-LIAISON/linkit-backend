package liaison.linkit.matching.business.service;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jakarta.mail.MessagingException;

import liaison.linkit.chat.business.ChatMapper;
import liaison.linkit.chat.business.validator.ChatRoomValidator;
import liaison.linkit.chat.domain.ChatMessage;
import liaison.linkit.chat.domain.ChatRoom;
import liaison.linkit.chat.domain.repository.chatMessage.ChatMessageRepository;
import liaison.linkit.chat.domain.type.ParticipantType;
import liaison.linkit.chat.implement.ChatRoomCommandAdapter;
import liaison.linkit.chat.implement.ChatRoomQueryAdapter;
import liaison.linkit.global.type.StatusType;
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
import liaison.linkit.member.domain.Member;
import liaison.linkit.member.implement.MemberQueryAdapter;
import liaison.linkit.notification.business.handler.NotificationHandler;
import liaison.linkit.notification.presentation.dto.NotificationResponseDTO.NotificationDetails;
import liaison.linkit.notification.service.HeaderNotificationService;
import liaison.linkit.profile.domain.profile.Profile;
import liaison.linkit.profile.implement.profile.ProfileQueryAdapter;
import liaison.linkit.team.domain.announcement.TeamMemberAnnouncement;
import liaison.linkit.team.domain.team.Team;
import liaison.linkit.team.implement.announcement.TeamMemberAnnouncementQueryAdapter;
import liaison.linkit.team.implement.team.TeamQueryAdapter;
import liaison.linkit.team.implement.teamMember.TeamMemberQueryAdapter;
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

    // Adapters
    private final MemberQueryAdapter memberQueryAdapter;
    private final TeamMemberQueryAdapter teamMemberQueryAdapter;
    private final TeamMemberAnnouncementQueryAdapter teamMemberAnnouncementQueryAdapter;
    private final MatchingQueryAdapter matchingQueryAdapter;
    private final ChatRoomQueryAdapter chatRoomQueryAdapter;
    private final MatchingCommandAdapter matchingCommandAdapter;

    // Mappers
    private final MatchingMapper matchingMapper;
    private final MatchingMailContentMapper matchingMailContentMapper;

    // Validator
    private final MatchingValidator matchingValidator;

    // Resolver
    private final MatchingInfoResolver matchingInfoResolver;

    // Handler
    private final NotificationHandler notificationHandler;

    // Mail & Notification
    private final AsyncMatchingEmailService asyncMatchingEmailService;
    private final HeaderNotificationService headerNotificationService;
    private final ChatRoomValidator chatRoomValidator;
    private final ProfileQueryAdapter profileQueryAdapter;
    private final TeamQueryAdapter teamQueryAdapter;
    private final ChatRoomCommandAdapter chatRoomCommandAdapter;
    private final ChatMapper chatMapper;
    private final ChatMessageRepository chatMessageRepository;

    // ─── 매칭 수신함에서 매칭 데이터 삭제 처리 ───────────────────────────────────────────────

    public DeleteReceivedMatchingItems deleteReceivedMatchingItems(
            final DeleteReceivedMatchingRequest req) {
        List<Matching> matches = matchingQueryAdapter.findAllByIds(req.getMatchingIds());
        matches.forEach(matching -> matching.setReceiverDeleteStatus(ReceiverDeleteStatus.DELETED));
        matchingCommandAdapter.updateAll(matches);
        List<DeleteReceivedMatchingItem> items =
                matchingMapper.toDeleteReceivedMatchingItemList(matches);
        return matchingMapper.toDeleteReceivedMatchingItems(items);
    }

    // ─── 매칭 수신함에서 읽음 처리 업데이트 ──────────────────────────────────

    public UpdateReceivedMatchingCompletedStateReadItems updateReceivedMatchingStateToRead(
            final UpdateReceivedMatchingReadRequest req) {
        List<Matching> matches = matchingQueryAdapter.findAllByIds(req.getMatchingIds());
        matches.forEach(this::updateMatchingReadStatus);
        matchingCommandAdapter.updateAll(matches);

        List<UpdateReceivedMatchingCompletedStateReadItem> readItems =
                matchingMapper.toUpdateReceivedMatchingCompletedStateReadItems(matches);
        return matchingMapper.toUpdateMatchingCompletedToReadItems(readItems);
    }

    // 매칭 상태에 따라 읽음 상태 업데이트 (REQUESTED, COMPLETED 처리)
    private void updateMatchingReadStatus(final Matching matching) {
        MatchingStatusType statusType = matching.getMatchingStatusType();

        Map<MatchingStatusType, ReceiverReadStatus> statusMapping =
                Map.of(
                        MatchingStatusType.REQUESTED, ReceiverReadStatus.READ_REQUESTED_MATCHING,
                        MatchingStatusType.COMPLETED, ReceiverReadStatus.READ_COMPLETED_MATCHING);

        if (statusMapping.containsKey(statusType)) {
            ReceiverReadStatus newReadStatus = statusMapping.get(statusType);
            if (matching.getReceiverReadStatus() != newReadStatus) {
                matching.setReceiverReadStatus(newReadStatus);
            }
        }
    }

    // ─── 매칭 수신함에서 메뉴 조회 ──────────────────────────────────────

    public Page<ReceivedMatchingMenu> getReceivedMatchingMenuResponse(
            final Long memberId, final ReceiverType receiverType, Pageable pageable) {
        if (receiverType != null) {
            // 특정 타입이 지정된 경우 해당 타입의 Matching 페이지를 조회하고 매핑해서 반환
            return getMatchingPageByReceiverType(memberId, receiverType, pageable)
                    .map(this::toMatchingReceivedMenu);
        } else {
            // receiverType null 경우 모든 타입(Profile, Team, Announcement)을 병합
            List<Matching> combined = new ArrayList<>();
            combined.addAll(
                    getMatchingPageByReceiverType(memberId, ReceiverType.PROFILE, pageable)
                            .getContent());
            combined.addAll(
                    getMatchingPageByReceiverType(memberId, ReceiverType.TEAM, pageable)
                            .getContent());
            combined.addAll(
                    getMatchingPageByReceiverType(memberId, ReceiverType.ANNOUNCEMENT, pageable)
                            .getContent());
            List<ReceivedMatchingMenu> menus =
                    combined.stream().map(this::toMatchingReceivedMenu).toList();
            return new PageImpl<>(menus, pageable, combined.size());
        }
    }

    private Page<Matching> getMatchingPageByReceiverType(
            Long memberId, ReceiverType receiverType, Pageable pageable) {
        switch (receiverType) {
            case PROFILE:
                final String emailId = memberQueryAdapter.findEmailIdById(memberId);
                return matchingQueryAdapter.findReceivedToProfile(emailId, pageable);
            case TEAM:
                final List<Team> teams =
                        teamMemberQueryAdapter.getAllTeamsInOwnerStateByMemberId(memberId);
                return matchingQueryAdapter.findReceivedToTeam(teams, pageable);
            case ANNOUNCEMENT:
                final List<Team> teamsForAnnouncement =
                        teamMemberQueryAdapter.getAllTeamsInOwnerStateByMemberId(memberId);
                List<Long> teamIds = teamsForAnnouncement.stream().map(Team::getId).toList();
                final List<TeamMemberAnnouncement> teamMemberAnnouncements =
                        teamMemberAnnouncementQueryAdapter.getAllByTeamIds(teamIds);
                List<Long> announcementIds =
                        teamMemberAnnouncements.stream()
                                .map(TeamMemberAnnouncement::getId)
                                .toList();
                return matchingQueryAdapter.findReceivedToAnnouncement(announcementIds, pageable);
            default:
                return Page.empty(pageable);
        }
    }

    // 수신자가 매칭 요청 상태를 업데이트한다.
    public UpdateMatchingStatusTypeResponse updateMatchingStatusType(
            final Long memberId, final Long matchingId, final UpdateMatchingStatusTypeRequest req)
            throws MessagingException, UnsupportedEncodingException {

        matchingValidator.validateUpdateStatusRequest(req);

        final Matching matching = matchingQueryAdapter.findByMatchingId(matchingId);

        matchingValidator.ensureReceiverAuthorized(
                memberId, matchingInfoResolver.getReceiverMemberId(matching));
        matchingCommandAdapter.updateMatchingStatusType(matching, req.getMatchingStatusType());

        if (req.getMatchingStatusType() == MatchingStatusType.COMPLETED) {
            // 메일 전송 및 알림 발송
            handleMailAndNotificationInCompletedState(matching);

            // 채팅방 생성 및 기본 매칭 요청 메시지 설정 메서드
            handleChatRoomSetting(matching, memberId);
        } else {
            handleNotificationInRejectedState(matching);
        }

        return matchingMapper.toUpdateMatchingStatusTypeResponse(
                matching, req.getMatchingStatusType());
    }

    // ─── 수신 매칭 메뉴 변환 ─────────────────────────────────────────

    private ReceivedMatchingMenu toMatchingReceivedMenu(final Matching matching) {
        // 채팅방 존재 여부 확인
        final Long chatRoomId =
                chatRoomQueryAdapter.existsChatRoomByMatchingId(matching.getId())
                        ? chatRoomQueryAdapter.getChatRoomIdByMatchingId(matching.getId())
                        : null;

        Object senderInfo = matchingInfoResolver.resolveSenderInfo(matching);
        Object receiverInfo = matchingInfoResolver.resolveReceiverInfo(matching);

        return matchingMapper.toMatchingReceivedMenu(
                matching,
                chatRoomId,
                (senderInfo instanceof SenderProfileInformation
                        ? (SenderProfileInformation) senderInfo
                        : null),
                (senderInfo instanceof SenderTeamInformation
                        ? (SenderTeamInformation) senderInfo
                        : null),
                (receiverInfo instanceof ReceiverProfileInformation
                        ? (ReceiverProfileInformation) receiverInfo
                        : null),
                (receiverInfo instanceof ReceiverTeamInformation
                        ? (ReceiverTeamInformation) receiverInfo
                        : null),
                (receiverInfo instanceof ReceiverAnnouncementInformation
                        ? (ReceiverAnnouncementInformation) receiverInfo
                        : null));
    }

    // ─── 매칭 요청 성사용 이메일 전송 ───────────────────────────────────────────

    private void sendMatchingCompleteEmail(final Matching matching)
            throws MessagingException, UnsupportedEncodingException {
        MatchingEmailContent emailContent =
                matchingMailContentMapper.generateMatchingCompletedEmailContent(matching);

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
                matchingInfoResolver.getReceiverRegionOrAnnouncementSkill(matching));
    }

    /** COMPLETED 상태로 변경된 경우의 후속 처리: - 매칭 완료 이메일 전송 - 수신자 및 발신자에게 알림 전송 및 카운트 업데이트 */
    private void handleMailAndNotificationInCompletedState(final Matching matching)
            throws MessagingException, UnsupportedEncodingException {

        sendMatchingCompleteEmail(matching);

        NotificationDetails acceptedReceiverDetails =
                notificationHandler.generateAcceptedStateReceiverNotificationDetails(matching);
        notificationHandler.alertNewAcceptedNotificationToReceiver(
                matching, acceptedReceiverDetails);
        headerNotificationService.publishNotificationCount(
                matchingInfoResolver.getReceiverMemberId(matching));

        NotificationDetails acceptedSenderDetails =
                notificationHandler.generateAcceptedStateSenderNotificationDetails(matching);
        notificationHandler.alertNewAcceptedNotificationToSender(matching, acceptedSenderDetails);
        headerNotificationService.publishNotificationCount(
                matchingInfoResolver.getSenderMemberId(matching));
    }

    /** COMPLETED 상태가 아닌 경우(거절 등)의 후속 처리: - 발신자에게 거절 알림 전송 및 카운트 업데이트 */
    private void handleNotificationInRejectedState(final Matching matching) {

        NotificationDetails rejectedDetails =
                notificationHandler.generatedRejectedStateSenderNotificationDetails(matching);
        notificationHandler.alertNewRejectedNotificationToSender(matching, rejectedDetails);
        headerNotificationService.publishNotificationCount(
                matchingInfoResolver.getSenderMemberId(matching));
    }

    // ※ 예제에서는 handleChatRoomSetting() 메서드에서 buildAndSaveChatRoomAsReceiver()를 호출하는 형태로 사용합니다.
    // 예시:
    public void handleChatRoomSetting(final Matching matching, final Long receiverMemberId) {
        buildAndSaveChatRoomAsReceiver(matching, receiverMemberId);
    }

    /**
     * 채팅방 생성 및 매칭 상태 업데이트 처리
     *
     * @param matching 생성할 채팅방과 연관된 매칭
     * @param receiverMemberId 현재 사용자의 memberId (수신자)
     */
    private void buildAndSaveChatRoomAsReceiver(
            final Matching matching, final Long receiverMemberId) {
        // 1. 현재 사용자 프로필 조회
        final Profile receiverProfile = profileQueryAdapter.findByMemberId(receiverMemberId);

        // 2. 채팅방 관련 검증 (예: 수신자 로직 검증)
        chatRoomValidator.validateReceiverLogic(matching, receiverMemberId, receiverProfile);

        // 3. 발신자와 수신자 정보를 분리하여 ParticipantInfo 생성
        ParticipantInfo participantA = buildSenderParticipant(matching);
        ParticipantInfo participantB =
                buildReceiverParticipant(matching, receiverMemberId, receiverProfile);

        // 4. ChatRoom 생성
        ChatRoom chatRoom =
                ChatRoom.builder()
                        .matchingId(matching.getId())
                        .participantAId(participantA.id())
                        .participantAMemberId(participantA.memberId())
                        .participantAName(participantA.name())
                        .participantAType(participantA.type())
                        .participantAStatus(StatusType.USABLE)
                        .participantBId(participantB.id())
                        .participantBMemberId(participantB.memberId())
                        .participantBName(participantB.name())
                        .participantBType(participantB.type())
                        .participantBStatus(StatusType.USABLE)
                        .lastMessage(matching.getRequestMessage())
                        .lastMessageTime(LocalDateTime.now())
                        .build();

        chatRoomCommandAdapter.createChatRoom(chatRoom);

        // 참여자 프로필 이미지 로드
        String participantALogoImagePath =
                getParticipantLogoImagePath(
                        chatRoom.getParticipantAType(), chatRoom.getParticipantAId());

        ChatMessage chatMessage =
                createChatMessage(
                        matching.getRequestMessage(), chatRoom, participantALogoImagePath);
        chatMessageRepository.save(chatMessage);

        // 5. 매칭 상태 업데이트: 채팅방 생성 상태로 변경
        matchingCommandAdapter.updateMatchingToCreatedRoomState(matching);
    }

    /**
     * 발신자(ParticipantA) 정보 생성
     *
     * @param matching 매칭 객체 (발신자 정보 포함)
     * @return ParticipantInfo containing 발신자 정보
     */
    private ParticipantInfo buildSenderParticipant(final Matching matching) {
        if (matching.getSenderType() == SenderType.PROFILE) {
            String id = matching.getSenderEmailId();
            Member member = memberQueryAdapter.findByEmailId(id);
            return new ParticipantInfo(
                    id,
                    member.getId(),
                    member.getMemberBasicInform().getMemberName(),
                    SenderType.PROFILE);
        } else { // SenderType.TEAM
            String id = matching.getSenderTeamCode();
            Team team = teamQueryAdapter.findByTeamCode(id);
            Long ownerId = teamMemberQueryAdapter.getTeamOwnerMemberId(team);
            return new ParticipantInfo(id, ownerId, team.getTeamName(), SenderType.TEAM);
        }
    }

    /**
     * 수신자(ParticipantB) 정보 생성
     *
     * @param matching 매칭 객체 (수신자 정보 포함)
     * @param receiverMemberId 현재 사용자 memberId (수신자)
     * @param receiverProfile 현재 사용자 프로필
     * @return ParticipantInfo containing 수신자 정보
     */
    private ParticipantInfo buildReceiverParticipant(
            final Matching matching, final Long receiverMemberId, final Profile receiverProfile) {
        String id;
        String name;
        SenderType type;

        if (matching.getReceiverType() == ReceiverType.PROFILE) {
            id = receiverProfile.getMember().getEmailId();
            name = receiverProfile.getMember().getMemberBasicInform().getMemberName();
            type = SenderType.PROFILE;
        } else if (matching.getReceiverType() == ReceiverType.TEAM) {
            id = matching.getReceiverTeamCode();
            Team team = teamQueryAdapter.findByTeamCode(id);
            name = team.getTeamName();
            type = SenderType.TEAM;
        } else { // ReceiverType.ANNOUNCEMENT
            TeamMemberAnnouncement announcement =
                    teamMemberAnnouncementQueryAdapter.findById(
                            matching.getReceiverAnnouncementId());
            id = announcement.getTeam().getTeamCode();
            name = announcement.getTeam().getTeamName();
            type = SenderType.TEAM;
        }
        return new ParticipantInfo(id, receiverMemberId, name, type);
    }

    private ChatMessage createChatMessage(
            final String matchingRequestMessage,
            final ChatRoom chatRoom,
            final String participantALogoImagePath) {
        // 수신자 측에서만 생성이 가능하다. (수신자는 ParticipantB로 확정)
        return chatMapper.toChatMessage(
                chatRoom.getId(),
                matchingRequestMessage,
                ParticipantType.A_TYPE,
                chatRoom.getParticipantAId(),
                chatRoom.getParticipantAMemberId(),
                chatRoom.getParticipantAName(),
                participantALogoImagePath,
                chatRoom.getParticipantAType(),
                chatRoom.getParticipantBMemberId());
    }

    private String getParticipantLogoImagePath(SenderType type, String id) {
        if (type.equals(SenderType.PROFILE)) {
            return profileQueryAdapter.findByEmailId(id).getProfileImagePath();
        } else if (type.equals(SenderType.TEAM)) {
            return teamQueryAdapter.findByTeamCode(id).getTeamLogoImagePath();
        }
        return null;
    }

    /**
     * @param id Profile의 경우 emailId, Team의 경우 teamCode
     * @param memberId 해당 참여자의 memberId
     * @param name 사용자 이름 또는 팀 이름
     * @param type PROFILE 또는 TEAM
     */
    // 내부 DTO: 채팅방 참여자 정보
    private record ParticipantInfo(String id, Long memberId, String name, SenderType type) {}
}
