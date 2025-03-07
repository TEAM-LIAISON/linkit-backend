package liaison.linkit.chat.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import liaison.linkit.chat.business.ChatMapper;
import liaison.linkit.chat.domain.ChatMessage;
import liaison.linkit.chat.domain.ChatRoom;
import liaison.linkit.chat.domain.repository.chatMessage.ChatMessageRepository;
import liaison.linkit.chat.domain.type.ParticipantType;
import liaison.linkit.chat.exception.ChatRoomLeaveBadRequestException;
import liaison.linkit.chat.exception.SendChatMessageBadRequestException;
import liaison.linkit.chat.implement.ChatQueryAdapter;
import liaison.linkit.chat.implement.ChatRoomCommandAdapter;
import liaison.linkit.chat.implement.ChatRoomQueryAdapter;
import liaison.linkit.chat.presentation.dto.ChatRequestDTO.ChatMessageRequest;
import liaison.linkit.chat.presentation.dto.ChatResponseDTO.ChatLeftMenu;
import liaison.linkit.chat.presentation.dto.ChatResponseDTO.ChatMessageHistoryResponse;
import liaison.linkit.chat.presentation.dto.ChatResponseDTO.ChatMessageResponse;
import liaison.linkit.chat.presentation.dto.ChatResponseDTO.ChatPartnerInformation;
import liaison.linkit.chat.presentation.dto.ChatResponseDTO.ChatRoomLeaveResponse;
import liaison.linkit.chat.presentation.dto.ChatResponseDTO.ChatRoomSummary;
import liaison.linkit.chat.presentation.dto.ChatResponseDTO.PartnerProfileDetailInformation;
import liaison.linkit.chat.presentation.dto.ChatResponseDTO.PartnerTeamDetailInformation;
import liaison.linkit.chat.presentation.dto.ChatResponseDTO.ReadChatMessageResponse;
import liaison.linkit.common.business.RegionMapper;
import liaison.linkit.common.implement.RegionQueryAdapter;
import liaison.linkit.common.presentation.RegionResponseDTO.RegionDetail;
import liaison.linkit.global.type.StatusType;
import liaison.linkit.global.util.SessionRegistry;
import liaison.linkit.matching.domain.type.SenderType;
import liaison.linkit.member.domain.Member;
import liaison.linkit.member.domain.type.MemberState;
import liaison.linkit.member.implement.MemberQueryAdapter;
import liaison.linkit.notification.service.HeaderNotificationService;
import liaison.linkit.profile.business.mapper.ProfilePositionMapper;
import liaison.linkit.profile.domain.position.ProfilePosition;
import liaison.linkit.profile.domain.profile.Profile;
import liaison.linkit.profile.domain.region.ProfileRegion;
import liaison.linkit.profile.implement.position.ProfilePositionQueryAdapter;
import liaison.linkit.profile.implement.profile.ProfileQueryAdapter;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO.ProfilePositionDetail;
import liaison.linkit.report.certification.dto.chat.ChatMessageReportDto;
import liaison.linkit.report.certification.service.DiscordChatReportService;
import liaison.linkit.team.business.mapper.scale.TeamScaleMapper;
import liaison.linkit.team.domain.region.TeamRegion;
import liaison.linkit.team.domain.scale.TeamScale;
import liaison.linkit.team.domain.team.Team;
import liaison.linkit.team.implement.announcement.TeamMemberAnnouncementQueryAdapter;
import liaison.linkit.team.implement.scale.TeamScaleQueryAdapter;
import liaison.linkit.team.implement.team.TeamQueryAdapter;
import liaison.linkit.team.implement.teamMember.TeamMemberQueryAdapter;
import liaison.linkit.team.presentation.team.dto.TeamResponseDTO.TeamScaleItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ChatService {

    private final SimpMessagingTemplate simpMessagingTemplate;

    private final ChatMapper chatMapper;

    private final ChatMessageRepository chatMessageRepository;

    private final MemberQueryAdapter memberQueryAdapter;
    private final ChatRoomQueryAdapter chatRoomQueryAdapter;
    private final ProfileQueryAdapter profileQueryAdapter;
    private final TeamQueryAdapter teamQueryAdapter;
    private final TeamMemberQueryAdapter teamMemberQueryAdapter;
    private final TeamMemberAnnouncementQueryAdapter teamMemberAnnouncementQueryAdapter;

    private final ChatRoomCommandAdapter chatRoomCommandAdapter;
    private final ProfilePositionQueryAdapter profilePositionQueryAdapter;
    private final ProfilePositionMapper profilePositionMapper;
    private final RegionQueryAdapter regionQueryAdapter;
    private final RegionMapper regionMapper;
    private final TeamScaleQueryAdapter teamScaleQueryAdapter;
    private final TeamScaleMapper teamScaleMapper;
    private final SessionRegistry sessionRegistry;
    private final ChatQueryAdapter chatQueryAdapter;
    private final HeaderNotificationService headerNotificationService;
    private final DiscordChatReportService discordChatReportService;

    public ReadChatMessageResponse handleReadChatMessage(
            final Long memberId, final Long chatRoomId) {

        // 1. 채팅방 존재 확인
        final ChatRoom chatRoom = chatRoomQueryAdapter.findById(chatRoomId);

        // 3. 읽지 않은 메시지 조회 (수신자 == memberId)
        List<ChatMessage> unreadMessages =
                chatMessageRepository.findByChatRoomIdAndIsReadFalseAndReceiverParticipantId(
                        chatRoomId);

        unreadMessages =
                unreadMessages.stream()
                        .filter(msg -> msg.getMessageReceiverMemberId().equals(memberId))
                        .collect(Collectors.toList());

        unreadMessages.forEach(ChatMessage::markAsRead);
        chatMessageRepository.saveAll(unreadMessages);

        headerNotificationService.publishNotificationCount(memberId);
        // 5. 응답 DTO 생성
        long updatedCount = unreadMessages.size();

        return chatMapper.toReadChatMessageResponse(chatRoomId, updatedCount);
    }

    public void handleChatMessage(
            final ChatMessageRequest chatMessageRequest,
            final String sessionId,
            final Long memberId,
            final Long chatRoomId) {

        // 1. 채팅방 존재 및 접근 권한 확인
        final ChatRoom chatRoom = chatRoomQueryAdapter.findById(chatRoomId);

        // 참여자 프로필 이미지 로드
        String participantALogoImagePath =
                getParticipantLogoImagePath(
                        chatRoom.getParticipantAType(), chatRoom.getParticipantAId());
        String participantBLogoImagePath =
                getParticipantLogoImagePath(
                        chatRoom.getParticipantBType(), chatRoom.getParticipantBId());

        // 2. 메시지 생성 및 저장
        ChatMessage chatMessage =
                createChatMessage(
                        chatMessageRequest,
                        chatRoom,
                        memberId,
                        participantALogoImagePath,
                        participantBLogoImagePath);
        final ChatMessage savedChatMessage = chatMessageRepository.save(chatMessage);

        // 3. 채팅방 마지막 메시지 업데이트
        chatRoom.updateLastMessage(chatMessage.getContent(), chatMessage.getTimestamp());
        final ChatRoom savedChatRoom = chatRoomCommandAdapter.save(chatRoom);

        // 4. 메시지 전송
        sendChatMessages(savedChatRoom, savedChatMessage, memberId, sessionId);

        ChatMessageReportDto chatMessageReportDto =
                ChatMessageReportDto.builder()
                        .chatMessageId(savedChatMessage.getId())
                        .content(savedChatMessage.getContent())
                        .timestamp(savedChatMessage.getTimestamp())
                        .chatMessageSenderEmail(
                                memberQueryAdapter.findEmailById(
                                        savedChatMessage.getMessageSenderMemberId()))
                        .chatMessageReceiverEmail(
                                memberQueryAdapter.findEmailById(
                                        savedChatMessage.getMessageReceiverMemberId()))
                        .build();

        discordChatReportService.sendChatMessageReport(chatMessageReportDto);

        // 5. 헤더 알림 전송
        headerNotificationService.publishNotificationCount(
                chatMessage.getMessageReceiverMemberId());
    }

    private String getParticipantLogoImagePath(SenderType type, String id) {
        if (type.equals(SenderType.PROFILE)) {
            return profileQueryAdapter.findByEmailId(id).getProfileImagePath();
        } else if (type.equals(SenderType.TEAM)) {
            return teamQueryAdapter.findByTeamCode(id).getTeamLogoImagePath();
        }
        return null;
    }

    private ChatMessage createChatMessage(
            ChatMessageRequest chatMessageRequest,
            ChatRoom chatRoom,
            Long memberId,
            String participantALogoImagePath,
            String participantBLogoImagePath) {
        if (chatRoom.getParticipantAMemberId().equals(memberId)) {
            // A가 B에게 보내는 메시지
            return chatMapper.toChatMessage(
                    chatRoom.getId(),
                    chatMessageRequest.getContent(),
                    ParticipantType.A_TYPE,
                    chatRoom.getParticipantAId(),
                    chatRoom.getParticipantAMemberId(),
                    chatRoom.getParticipantAName(),
                    participantALogoImagePath,
                    chatRoom.getParticipantAType(),
                    chatRoom.getParticipantBMemberId());
        } else if (chatRoom.getParticipantBMemberId().equals(memberId)) {
            // B가 A에게 보내는 메시지
            return chatMapper.toChatMessage(
                    chatRoom.getId(),
                    chatMessageRequest.getContent(),
                    ParticipantType.B_TYPE,
                    chatRoom.getParticipantBId(),
                    chatRoom.getParticipantBMemberId(),
                    chatRoom.getParticipantBName(),
                    participantBLogoImagePath,
                    chatRoom.getParticipantBType(),
                    chatRoom.getParticipantAMemberId());
        } else {
            throw SendChatMessageBadRequestException.EXCEPTION;
        }
    }

    private void sendChatMessages(
            ChatRoom chatRoom, ChatMessage chatMessage, Long senderMemberId, String sessionId) {
        // 채팅방의 양쪽 참여자에게 메시지 전송
        ChatMessageResponse senderResponse =
                chatMapper.toChatMessageResponse(chatRoom, chatMessage, senderMemberId);
        ChatMessageResponse receiverResponse =
                chatMapper.toChatMessageResponse(
                        chatRoom, chatMessage, chatMessage.getMessageReceiverMemberId());

        // 발신자에게 메시지 전송
        sendMessageToAllSessions(senderMemberId, "/sub/chat/" + chatRoom.getId(), senderResponse);

        // 수신자에게 메시지 전송
        Long receiverMemberId =
                chatRoom.getParticipantAMemberId().equals(senderMemberId)
                        ? chatRoom.getParticipantBMemberId()
                        : chatRoom.getParticipantAMemberId();

        sendMessageToAllSessions(
                receiverMemberId, "/sub/chat/" + chatRoom.getId(), receiverResponse);
    }

    /** 채팅방의 이전 메시지 내역 조회 */
    @Transactional(readOnly = true)
    public ChatMessageHistoryResponse getChatMessages(
            final Long chatRoomId, final Long memberId, final Pageable pageable) {

        final ChatRoom chatRoom = chatRoomQueryAdapter.findById(chatRoomId);
        if (chatRoom.getParticipantAMemberId().equals(memberId)) {
            if (chatRoom.getParticipantAStatus().equals(StatusType.DELETED)) {
                chatRoom.setParticipantBStatus(StatusType.USABLE);
            }
        } else if (chatRoom.getParticipantBMemberId().equals(memberId)) {
            if (chatRoom.getParticipantBStatus().equals(StatusType.DELETED)) {
                chatRoom.setParticipantAStatus(StatusType.USABLE);
            }
        }
        // 2. 메시지 조회 및 읽음 처리
        Page<ChatMessage> messages =
                chatMessageRepository.findByChatRoomIdOrderByTimestampDesc(chatRoomId, pageable);

        ChatPartnerInformation chatPartnerInformation = new ChatPartnerInformation();
        boolean isPartnerOnline = false;

        if (chatRoom.getParticipantAMemberId().equals(memberId)) {
            final Member chatPartnerMember =
                    memberQueryAdapter.findById(chatRoom.getParticipantBMemberId());
            if (chatRoom.getParticipantBType().equals(SenderType.PROFILE)) {
                final Profile chatPartnerProfile = chatPartnerMember.getProfile();

                ProfilePositionDetail profilePositionDetail = new ProfilePositionDetail();
                if (profilePositionQueryAdapter.existsProfilePositionByProfileId(
                        chatPartnerProfile.getId())) {
                    final ProfilePosition profilePosition =
                            profilePositionQueryAdapter.findProfilePositionByProfileId(
                                    chatPartnerProfile.getId());
                    profilePositionDetail =
                            profilePositionMapper.toProfilePositionDetail(profilePosition);
                }

                RegionDetail regionDetail = new RegionDetail();
                if (regionQueryAdapter.existsProfileRegionByProfileId(
                        (chatPartnerProfile.getId()))) {
                    final ProfileRegion profileRegion =
                            regionQueryAdapter.findProfileRegionByProfileId(
                                    chatPartnerProfile.getId());
                    regionDetail = regionMapper.toRegionDetail(profileRegion.getRegion());
                }

                isPartnerOnline = sessionRegistry.isOnline(chatPartnerMember.getId());
                long unreadCount =
                        chatQueryAdapter.countUnreadMessagesInRoomForMember(
                                chatRoom.getId(), memberId);

                chatPartnerInformation =
                        ChatPartnerInformation.builder()
                                .chatPartnerName(
                                        chatPartnerMember.getMemberBasicInform().getMemberName())
                                .chatPartnerImageUrl(
                                        chatPartnerMember.getProfile().getProfileImagePath())
                                .partnerProfileDetailInformation(
                                        PartnerProfileDetailInformation.builder()
                                                .profilePositionDetail(profilePositionDetail)
                                                .regionDetail(regionDetail)
                                                .emailId(
                                                        chatPartnerProfile.getMember().getEmailId())
                                                .build())
                                .lastMessage(chatRoom.getLastMessage())
                                .lastMessageTime(chatRoom.getLastMessageTime())
                                .build();

            } else if (chatRoom.getParticipantBType().equals(SenderType.TEAM)) {
                final Team chatPartnerTeam =
                        teamQueryAdapter.findByTeamCode(chatRoom.getParticipantBId());
                final Long ownerMemberId =
                        teamMemberQueryAdapter.getTeamOwnerMemberId(chatPartnerTeam);

                TeamScaleItem teamScaleItem = new TeamScaleItem();
                if (teamScaleQueryAdapter.existsTeamScaleByTeamId(chatPartnerTeam.getId())) {
                    final TeamScale teamScale =
                            teamScaleQueryAdapter.findTeamScaleByTeamId(chatPartnerTeam.getId());
                    teamScaleItem = teamScaleMapper.toTeamScaleItem(teamScale);
                }

                RegionDetail regionDetail = new RegionDetail();
                if (regionQueryAdapter.existsTeamRegionByTeamId((chatPartnerTeam.getId()))) {
                    final TeamRegion teamRegion =
                            regionQueryAdapter.findTeamRegionByTeamId(chatPartnerTeam.getId());
                    regionDetail = regionMapper.toRegionDetail(teamRegion.getRegion());
                }

                isPartnerOnline = sessionRegistry.isOnline(ownerMemberId);
                long unreadCount =
                        chatQueryAdapter.countUnreadMessagesInRoomForMember(
                                chatRoom.getId(), memberId);

                chatPartnerInformation =
                        ChatPartnerInformation.builder()
                                .chatPartnerName(chatPartnerTeam.getTeamName())
                                .chatPartnerImageUrl(chatPartnerTeam.getTeamLogoImagePath())
                                .partnerTeamDetailInformation(
                                        PartnerTeamDetailInformation.builder()
                                                .teamScaleItem(teamScaleItem)
                                                .regionDetail(regionDetail)
                                                .teamCode(chatPartnerTeam.getTeamCode())
                                                .build())
                                .lastMessage(chatRoom.getLastMessage())
                                .lastMessageTime(chatRoom.getLastMessageTime())
                                .build();
            } else {
                final Team chatPartnerTeam =
                        teamQueryAdapter.findByTeamCode(
                                teamMemberAnnouncementQueryAdapter
                                        .getTeamMemberAnnouncement(
                                                Long.valueOf(chatRoom.getParticipantBId()))
                                        .getTeam()
                                        .getTeamCode());
                final Long ownerMemberId =
                        teamMemberQueryAdapter.getTeamOwnerMemberId(chatPartnerTeam);

                TeamScaleItem teamScaleItem = new TeamScaleItem();
                if (teamScaleQueryAdapter.existsTeamScaleByTeamId(chatPartnerTeam.getId())) {
                    final TeamScale teamScale =
                            teamScaleQueryAdapter.findTeamScaleByTeamId(chatPartnerTeam.getId());
                    teamScaleItem = teamScaleMapper.toTeamScaleItem(teamScale);
                }

                RegionDetail regionDetail = new RegionDetail();
                if (regionQueryAdapter.existsTeamRegionByTeamId((chatPartnerTeam.getId()))) {
                    final TeamRegion teamRegion =
                            regionQueryAdapter.findTeamRegionByTeamId(chatPartnerTeam.getId());
                    regionDetail = regionMapper.toRegionDetail(teamRegion.getRegion());
                }

                isPartnerOnline = sessionRegistry.isOnline(ownerMemberId);
                long unreadCount =
                        chatQueryAdapter.countUnreadMessagesInRoomForMember(
                                chatRoom.getId(), memberId);

                chatPartnerInformation =
                        ChatPartnerInformation.builder()
                                .chatPartnerName(chatPartnerTeam.getTeamName())
                                .chatPartnerImageUrl(chatPartnerTeam.getTeamLogoImagePath())
                                .partnerTeamDetailInformation(
                                        PartnerTeamDetailInformation.builder()
                                                .teamScaleItem(teamScaleItem)
                                                .regionDetail(regionDetail)
                                                .teamCode(chatPartnerTeam.getTeamCode())
                                                .build())
                                .lastMessage(chatRoom.getLastMessage())
                                .lastMessageTime(chatRoom.getLastMessageTime())
                                .build();
            }
        } else {
            final Member chatPartnerMember =
                    memberQueryAdapter.findById(chatRoom.getParticipantAMemberId());
            if (chatRoom.getParticipantAType().equals(SenderType.PROFILE)) {
                final Profile chatPartnerProfile = chatPartnerMember.getProfile();

                ProfilePositionDetail profilePositionDetail = new ProfilePositionDetail();
                if (profilePositionQueryAdapter.existsProfilePositionByProfileId(
                        chatPartnerProfile.getId())) {
                    final ProfilePosition profilePosition =
                            profilePositionQueryAdapter.findProfilePositionByProfileId(
                                    chatPartnerProfile.getId());
                    profilePositionDetail =
                            profilePositionMapper.toProfilePositionDetail(profilePosition);
                }

                RegionDetail regionDetail = new RegionDetail();
                if (regionQueryAdapter.existsProfileRegionByProfileId(
                        (chatPartnerProfile.getId()))) {
                    final ProfileRegion profileRegion =
                            regionQueryAdapter.findProfileRegionByProfileId(
                                    chatPartnerProfile.getId());
                    regionDetail = regionMapper.toRegionDetail(profileRegion.getRegion());
                }

                isPartnerOnline = sessionRegistry.isOnline(chatPartnerMember.getId());
                long unreadCount =
                        chatQueryAdapter.countUnreadMessagesInRoomForMember(
                                chatRoom.getId(), memberId);

                chatPartnerInformation =
                        ChatPartnerInformation.builder()
                                .chatPartnerName(
                                        chatPartnerMember.getMemberBasicInform().getMemberName())
                                .chatPartnerImageUrl(
                                        chatPartnerMember.getProfile().getProfileImagePath())
                                .partnerProfileDetailInformation(
                                        PartnerProfileDetailInformation.builder()
                                                .profilePositionDetail(profilePositionDetail)
                                                .regionDetail(regionDetail)
                                                .emailId(
                                                        chatPartnerProfile.getMember().getEmailId())
                                                .build())
                                .lastMessage(chatRoom.getLastMessage())
                                .lastMessageTime(chatRoom.getLastMessageTime())
                                .build();

            } else if (chatRoom.getParticipantAType().equals(SenderType.TEAM)) {
                final Team chatPartnerTeam =
                        teamQueryAdapter.findByTeamCode(chatRoom.getParticipantAId());
                final Long ownerMemberId =
                        teamMemberQueryAdapter.getTeamOwnerMemberId(chatPartnerTeam);

                TeamScaleItem teamScaleItem = new TeamScaleItem();
                if (teamScaleQueryAdapter.existsTeamScaleByTeamId(chatPartnerTeam.getId())) {
                    final TeamScale teamScale =
                            teamScaleQueryAdapter.findTeamScaleByTeamId(chatPartnerTeam.getId());
                    teamScaleItem = teamScaleMapper.toTeamScaleItem(teamScale);
                }

                RegionDetail regionDetail = new RegionDetail();
                if (regionQueryAdapter.existsTeamRegionByTeamId((chatPartnerTeam.getId()))) {
                    final TeamRegion teamRegion =
                            regionQueryAdapter.findTeamRegionByTeamId(chatPartnerTeam.getId());
                    regionDetail = regionMapper.toRegionDetail(teamRegion.getRegion());
                }

                isPartnerOnline = sessionRegistry.isOnline(ownerMemberId);
                long unreadCount =
                        chatQueryAdapter.countUnreadMessagesInRoomForMember(
                                chatRoom.getId(), memberId);

                chatPartnerInformation =
                        ChatPartnerInformation.builder()
                                .chatPartnerName(chatPartnerTeam.getTeamName())
                                .chatPartnerImageUrl(chatPartnerTeam.getTeamLogoImagePath())
                                .partnerTeamDetailInformation(
                                        PartnerTeamDetailInformation.builder()
                                                .teamScaleItem(teamScaleItem)
                                                .regionDetail(regionDetail)
                                                .teamCode(chatPartnerTeam.getTeamCode())
                                                .build())
                                .lastMessage(chatRoom.getLastMessage())
                                .lastMessageTime(chatRoom.getLastMessageTime())
                                .build();
            } else {
                final Team chatPartnerTeam =
                        teamQueryAdapter.findByTeamCode(
                                teamMemberAnnouncementQueryAdapter
                                        .getTeamMemberAnnouncement(
                                                Long.valueOf(chatRoom.getParticipantAId()))
                                        .getTeam()
                                        .getTeamCode());
                final Long ownerMemberId =
                        teamMemberQueryAdapter.getTeamOwnerMemberId(chatPartnerTeam);

                TeamScaleItem teamScaleItem = new TeamScaleItem();
                if (teamScaleQueryAdapter.existsTeamScaleByTeamId(chatPartnerTeam.getId())) {
                    final TeamScale teamScale =
                            teamScaleQueryAdapter.findTeamScaleByTeamId(chatPartnerTeam.getId());
                    teamScaleItem = teamScaleMapper.toTeamScaleItem(teamScale);
                }

                RegionDetail regionDetail = new RegionDetail();
                if (regionQueryAdapter.existsTeamRegionByTeamId((chatPartnerTeam.getId()))) {
                    final TeamRegion teamRegion =
                            regionQueryAdapter.findTeamRegionByTeamId(chatPartnerTeam.getId());
                    regionDetail = regionMapper.toRegionDetail(teamRegion.getRegion());
                }

                isPartnerOnline = sessionRegistry.isOnline(ownerMemberId);
                long unreadCount =
                        chatQueryAdapter.countUnreadMessagesInRoomForMember(
                                chatRoom.getId(), memberId);

                chatPartnerInformation =
                        ChatPartnerInformation.builder()
                                .chatPartnerName(chatPartnerTeam.getTeamName())
                                .chatPartnerImageUrl(chatPartnerTeam.getTeamLogoImagePath())
                                .partnerTeamDetailInformation(
                                        PartnerTeamDetailInformation.builder()
                                                .teamScaleItem(teamScaleItem)
                                                .regionDetail(regionDetail)
                                                .teamCode(chatPartnerTeam.getTeamCode())
                                                .build())
                                .lastMessage(chatRoom.getLastMessage())
                                .lastMessageTime(chatRoom.getLastMessageTime())
                                .build();
            }
        }

        updateUnreadMessages(chatRoomId, memberId);

        return chatMapper.toChatMessageHistoryResponse(
                chatRoom, messages, memberId, chatPartnerInformation, isPartnerOnline);
    }

    @Transactional(readOnly = true)
    public ChatLeftMenu getChatLeftMenu(final Long memberId) {
        // 1. 채팅방 목록 조회 (나가지 않은 채팅방 목록)
        final List<ChatRoom> chatRooms = chatRoomQueryAdapter.findAllChatRoomsByMemberId(memberId);
        final List<ChatRoomSummary> chatRoomSummaries = new ArrayList<>();

        for (ChatRoom chatRoom : chatRooms) {
            // 채팅방의 참여자 타입에 따라 분기
            // 예시에서는 participantA가 나인 경우에 대해 처리하는 코드를 먼저 작성합니다.
            if (chatRoom.getParticipantAMemberId().equals(memberId)) {

                if (chatRoom.getParticipantAType().equals(SenderType.TEAM)) {
                    if (!teamQueryAdapter.existsByTeamCode(chatRoom.getParticipantAId())) {
                        continue;
                    }
                }

                // participantB가 채팅 상대입니다.
                final Member partnerMember =
                        memberQueryAdapter.findById(chatRoom.getParticipantBMemberId());
                // 만약 채팅 상대가 탈퇴(삭제) 상태라면 해당 채팅방은 목록에 포함하지 않음.
                if (!isMemberDisplayable(partnerMember)) {
                    continue;
                }

                if (chatRoom.getParticipantBType().equals(SenderType.PROFILE)) {

                    final Profile partnerProfile = partnerMember.getProfile();
                    // 프로필 관련 상세 정보를 조회하여 DTO를 생성하는 기존 로직…
                    ProfilePositionDetail profilePositionDetail =
                            getProfilePositionDetail(partnerProfile);
                    RegionDetail regionDetail = getProfileRegionDetail(partnerProfile);
                    boolean isPartnerOnline = sessionRegistry.isOnline(partnerMember.getId());
                    long unreadCount =
                            chatQueryAdapter.countUnreadMessagesInRoomForMember(
                                    chatRoom.getId(), memberId);

                    ChatRoomSummary chatRoomSummary =
                            ChatRoomSummary.builder()
                                    .chatRoomId(chatRoom.getId())
                                    .chatPartnerInformation(
                                            ChatPartnerInformation.builder()
                                                    .chatPartnerName(
                                                            partnerMember
                                                                    .getMemberBasicInform()
                                                                    .getMemberName())
                                                    .chatPartnerImageUrl(
                                                            partnerProfile.getProfileImagePath())
                                                    .partnerProfileDetailInformation(
                                                            PartnerProfileDetailInformation
                                                                    .builder()
                                                                    .profilePositionDetail(
                                                                            profilePositionDetail)
                                                                    .regionDetail(regionDetail)
                                                                    .build())
                                                    .lastMessage(chatRoom.getLastMessage())
                                                    .lastMessageTime(chatRoom.getLastMessageTime())
                                                    .build())
                                    .isChatPartnerOnline(isPartnerOnline)
                                    .unreadChatMessageCount(unreadCount)
                                    .build();

                    chatRoomSummaries.add(chatRoomSummary);
                } else if (chatRoom.getParticipantBType().equals(SenderType.TEAM)) {
                    Team partnerTeam;
                    if (teamQueryAdapter.existsByTeamCode(chatRoom.getParticipantBId())) {
                        partnerTeam = teamQueryAdapter.findByTeamCode(chatRoom.getParticipantBId());
                    } else {
                        continue;
                    }

                    final Long ownerMemberId =
                            teamMemberQueryAdapter.getTeamOwnerMemberId(partnerTeam);

                    TeamScaleItem teamScaleItem = getTeamScaleItem(partnerTeam);
                    RegionDetail regionDetail = getTeamRegionDetail(partnerTeam);
                    boolean isPartnerOnline = sessionRegistry.isOnline(ownerMemberId);
                    long unreadCount =
                            chatQueryAdapter.countUnreadMessagesInRoomForMember(
                                    chatRoom.getId(), memberId);

                    ChatRoomSummary chatRoomSummary =
                            ChatRoomSummary.builder()
                                    .chatRoomId(chatRoom.getId())
                                    .chatPartnerInformation(
                                            ChatPartnerInformation.builder()
                                                    .chatPartnerName(partnerTeam.getTeamName())
                                                    .chatPartnerImageUrl(
                                                            partnerTeam.getTeamLogoImagePath())
                                                    .partnerTeamDetailInformation(
                                                            PartnerTeamDetailInformation.builder()
                                                                    .teamScaleItem(teamScaleItem)
                                                                    .regionDetail(regionDetail)
                                                                    .build())
                                                    .lastMessage(chatRoom.getLastMessage())
                                                    .lastMessageTime(chatRoom.getLastMessageTime())
                                                    .build())
                                    .isChatPartnerOnline(isPartnerOnline)
                                    .unreadChatMessageCount(unreadCount)
                                    .build();

                    chatRoomSummaries.add(chatRoomSummary);
                } else {
                    Team partnerTeam;
                    if (teamQueryAdapter.existsByTeamCode(
                            teamMemberAnnouncementQueryAdapter
                                    .getTeamMemberAnnouncement(
                                            Long.valueOf(chatRoom.getParticipantBId()))
                                    .getTeam()
                                    .getTeamCode())) {
                        partnerTeam =
                                teamQueryAdapter.findByTeamCode(
                                        teamMemberAnnouncementQueryAdapter
                                                .getTeamMemberAnnouncement(
                                                        Long.valueOf(chatRoom.getParticipantBId()))
                                                .getTeam()
                                                .getTeamCode());
                    } else {
                        continue;
                    }

                    final Long ownerMemberId =
                            teamMemberQueryAdapter.getTeamOwnerMemberId(partnerTeam);

                    TeamScaleItem teamScaleItem = getTeamScaleItem(partnerTeam);
                    RegionDetail regionDetail = getTeamRegionDetail(partnerTeam);
                    boolean isPartnerOnline = sessionRegistry.isOnline(ownerMemberId);
                    long unreadCount =
                            chatQueryAdapter.countUnreadMessagesInRoomForMember(
                                    chatRoom.getId(), memberId);

                    ChatRoomSummary chatRoomSummary =
                            ChatRoomSummary.builder()
                                    .chatRoomId(chatRoom.getId())
                                    .chatPartnerInformation(
                                            ChatPartnerInformation.builder()
                                                    .chatPartnerName(partnerTeam.getTeamName())
                                                    .chatPartnerImageUrl(
                                                            partnerTeam.getTeamLogoImagePath())
                                                    .partnerTeamDetailInformation(
                                                            PartnerTeamDetailInformation.builder()
                                                                    .teamScaleItem(teamScaleItem)
                                                                    .regionDetail(regionDetail)
                                                                    .build())
                                                    .lastMessage(chatRoom.getLastMessage())
                                                    .lastMessageTime(chatRoom.getLastMessageTime())
                                                    .build())
                                    .isChatPartnerOnline(isPartnerOnline)
                                    .unreadChatMessageCount(unreadCount)
                                    .build();

                    chatRoomSummaries.add(chatRoomSummary);
                }
            } else {
                if (chatRoom.getParticipantBType().equals(SenderType.TEAM)) {
                    if (!teamQueryAdapter.existsByTeamCode(chatRoom.getParticipantBId())) {
                        continue;
                    }
                }
                // participantA가 채팅 상대입니다.
                final Member partnerMember =
                        memberQueryAdapter.findById(chatRoom.getParticipantAMemberId());
                // 만약 채팅 상대가 탈퇴(삭제) 상태라면 해당 채팅방은 목록에 포함하지 않음.
                if (!isMemberDisplayable(partnerMember)) {
                    continue;
                }

                if (chatRoom.getParticipantAType().equals(SenderType.PROFILE)) {
                    final Profile partnerProfile = partnerMember.getProfile();

                    ProfilePositionDetail profilePositionDetail =
                            getProfilePositionDetail(partnerProfile);
                    RegionDetail regionDetail = getProfileRegionDetail(partnerProfile);
                    boolean isPartnerOnline = sessionRegistry.isOnline(partnerMember.getId());
                    long unreadCount =
                            chatQueryAdapter.countUnreadMessagesInRoomForMember(
                                    chatRoom.getId(), memberId);

                    ChatRoomSummary chatRoomSummary =
                            ChatRoomSummary.builder()
                                    .chatRoomId(chatRoom.getId())
                                    .chatPartnerInformation(
                                            ChatPartnerInformation.builder()
                                                    .chatPartnerName(
                                                            partnerMember
                                                                    .getMemberBasicInform()
                                                                    .getMemberName())
                                                    .chatPartnerImageUrl(
                                                            partnerProfile.getProfileImagePath())
                                                    .partnerProfileDetailInformation(
                                                            PartnerProfileDetailInformation
                                                                    .builder()
                                                                    .profilePositionDetail(
                                                                            profilePositionDetail)
                                                                    .regionDetail(regionDetail)
                                                                    .build())
                                                    .lastMessage(chatRoom.getLastMessage())
                                                    .lastMessageTime(chatRoom.getLastMessageTime())
                                                    .build())
                                    .isChatPartnerOnline(isPartnerOnline)
                                    .unreadChatMessageCount(unreadCount)
                                    .build();

                    chatRoomSummaries.add(chatRoomSummary);
                } else if (chatRoom.getParticipantAType().equals(SenderType.TEAM)) {

                    Team partnerTeam;
                    if (teamQueryAdapter.existsByTeamCode(chatRoom.getParticipantAId())) {
                        partnerTeam = teamQueryAdapter.findByTeamCode(chatRoom.getParticipantAId());
                    } else {
                        continue;
                    }

                    final Long ownerMemberId =
                            teamMemberQueryAdapter.getTeamOwnerMemberId(partnerTeam);

                    TeamScaleItem teamScaleItem = getTeamScaleItem(partnerTeam);
                    RegionDetail regionDetail = getTeamRegionDetail(partnerTeam);
                    boolean isPartnerOnline = sessionRegistry.isOnline(ownerMemberId);
                    long unreadCount =
                            chatQueryAdapter.countUnreadMessagesInRoomForMember(
                                    chatRoom.getId(), memberId);

                    ChatRoomSummary chatRoomSummary =
                            ChatRoomSummary.builder()
                                    .chatRoomId(chatRoom.getId())
                                    .chatPartnerInformation(
                                            ChatPartnerInformation.builder()
                                                    .chatPartnerName(partnerTeam.getTeamName())
                                                    .chatPartnerImageUrl(
                                                            partnerTeam.getTeamLogoImagePath())
                                                    .partnerTeamDetailInformation(
                                                            PartnerTeamDetailInformation.builder()
                                                                    .teamScaleItem(teamScaleItem)
                                                                    .regionDetail(regionDetail)
                                                                    .build())
                                                    .lastMessage(chatRoom.getLastMessage())
                                                    .lastMessageTime(chatRoom.getLastMessageTime())
                                                    .build())
                                    .isChatPartnerOnline(isPartnerOnline)
                                    .unreadChatMessageCount(unreadCount)
                                    .build();

                    chatRoomSummaries.add(chatRoomSummary);
                } else {
                    Team partnerTeam;
                    if (teamQueryAdapter.existsByTeamCode(
                            teamMemberAnnouncementQueryAdapter
                                    .getTeamMemberAnnouncement(
                                            Long.valueOf(chatRoom.getParticipantAId()))
                                    .getTeam()
                                    .getTeamCode())) {
                        partnerTeam =
                                teamQueryAdapter.findByTeamCode(
                                        teamMemberAnnouncementQueryAdapter
                                                .getTeamMemberAnnouncement(
                                                        Long.valueOf(chatRoom.getParticipantAId()))
                                                .getTeam()
                                                .getTeamCode());
                    } else {
                        continue;
                    }

                    final Long ownerMemberId =
                            teamMemberQueryAdapter.getTeamOwnerMemberId(partnerTeam);
                    TeamScaleItem teamScaleItem = getTeamScaleItem(partnerTeam);
                    RegionDetail regionDetail = getTeamRegionDetail(partnerTeam);
                    boolean isPartnerOnline = sessionRegistry.isOnline(ownerMemberId);
                    long unreadCount =
                            chatQueryAdapter.countUnreadMessagesInRoomForMember(
                                    chatRoom.getId(), memberId);

                    ChatRoomSummary chatRoomSummary =
                            ChatRoomSummary.builder()
                                    .chatRoomId(chatRoom.getId())
                                    .chatPartnerInformation(
                                            ChatPartnerInformation.builder()
                                                    .chatPartnerName(partnerTeam.getTeamName())
                                                    .chatPartnerImageUrl(
                                                            partnerTeam.getTeamLogoImagePath())
                                                    .partnerTeamDetailInformation(
                                                            PartnerTeamDetailInformation.builder()
                                                                    .teamScaleItem(teamScaleItem)
                                                                    .regionDetail(regionDetail)
                                                                    .build())
                                                    .lastMessage(chatRoom.getLastMessage())
                                                    .lastMessageTime(chatRoom.getLastMessageTime())
                                                    .build())
                                    .isChatPartnerOnline(isPartnerOnline)
                                    .unreadChatMessageCount(unreadCount)
                                    .build();

                    chatRoomSummaries.add(chatRoomSummary);
                }
            }
        }

        return chatMapper.toChatLeftMenu(chatRoomSummaries);
    }

    /** 읽지 않은 메시지 읽음 처리 */
    private void updateUnreadMessages(final Long chatRoomId, final Long memberId) {
        List<ChatMessage> unreadMessages =
                chatMessageRepository.findByChatRoomIdAndIsReadFalseAndReceiverParticipantId(
                        chatRoomId);

        unreadMessages.forEach(ChatMessage::markAsRead);
        chatMessageRepository.saveAll(unreadMessages);
    }

    public ChatRoomLeaveResponse leaveChatRoom(final Long memberId, final Long chatRoomId) {
        final ChatRoom chatRoom = chatRoomQueryAdapter.findById(chatRoomId);

        ParticipantType participantType;

        if (chatRoom.getParticipantAMemberId().equals(memberId)) {
            chatRoom.setParticipantAStatus(StatusType.DELETED);
            participantType = ParticipantType.A_TYPE;
        } else if (chatRoom.getParticipantBMemberId().equals(memberId)) {
            chatRoom.setParticipantBStatus(StatusType.DELETED);
            participantType = ParticipantType.B_TYPE;
        } else {
            throw ChatRoomLeaveBadRequestException.EXCEPTION;
        }

        return chatMapper.toLeaveChatRoom(chatRoomId, participantType);
    }

    /** 특정 userId가 가진 모든 sessionId에 메시지를 전송하는 메서드 */
    private void sendMessageToAllSessions(Long userId, String destination, Object payload) {
        // userId가 가진 모든 sessionId 조회
        Set<String> sessionIds = sessionRegistry.getMemberSessions(userId);
        if (sessionIds == null || sessionIds.isEmpty()) {
            return;
        }

        for (String sessionId : sessionIds) {
            simpMessagingTemplate.convertAndSendToUser(
                    sessionId, // Principal name(=userId, e.g. "5")
                    destination, // "/sub/chat/{chatRoomId}"
                    payload,
                    createHeaders(sessionId) // sessionID 명시해 특정 세션만 받도록
                    );
        }
    }

    /** 회원이 화면에 표시 가능한 상태인지 여부를 판단합니다. 예: MemberState가 DELETED이면 false 반환. */
    private boolean isMemberDisplayable(final Member member) {
        return !member.getMemberState().equals(MemberState.DELETED);
    }

    /** 팀이 화면에 표시 가능한 상태인지 여부를 판단합니다. 예: 팀이 삭제 상태라면 false 반환. */
    private boolean isTeamDisplayable(final Team team) {
        return !team.isDeleted(); // 팀 엔티티에 삭제 상태를 나타내는 메서드나 플래그가 있다고 가정
    }

    private ProfilePositionDetail getProfilePositionDetail(final Profile profile) {
        ProfilePositionDetail detail = new ProfilePositionDetail();
        if (profilePositionQueryAdapter.existsProfilePositionByProfileId(profile.getId())) {
            ProfilePosition position =
                    profilePositionQueryAdapter.findProfilePositionByProfileId(profile.getId());
            detail = profilePositionMapper.toProfilePositionDetail(position);
        }
        return detail;
    }

    private RegionDetail getProfileRegionDetail(final Profile profile) {
        RegionDetail regionDetail = new RegionDetail();
        if (regionQueryAdapter.existsProfileRegionByProfileId(profile.getId())) {
            ProfileRegion profileRegion =
                    regionQueryAdapter.findProfileRegionByProfileId(profile.getId());
            regionDetail = regionMapper.toRegionDetail(profileRegion.getRegion());
        }
        return regionDetail;
    }

    private TeamScaleItem getTeamScaleItem(final Team team) {
        TeamScaleItem scaleItem = new TeamScaleItem();
        if (teamScaleQueryAdapter.existsTeamScaleByTeamId(team.getId())) {
            TeamScale teamScale = teamScaleQueryAdapter.findTeamScaleByTeamId(team.getId());
            scaleItem = teamScaleMapper.toTeamScaleItem(teamScale);
        }
        return scaleItem;
    }

    private RegionDetail getTeamRegionDetail(final Team team) {
        RegionDetail regionDetail = new RegionDetail();
        if (regionQueryAdapter.existsTeamRegionByTeamId(team.getId())) {
            TeamRegion teamRegion = regionQueryAdapter.findTeamRegionByTeamId(team.getId());
            regionDetail = regionMapper.toRegionDetail(teamRegion.getRegion());
        }
        return regionDetail;
    }

    private MessageHeaders createHeaders(String sessionId) {
        SimpMessageHeaderAccessor headerAccessor =
                SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
        headerAccessor.setSessionId(sessionId);
        headerAccessor.setLeaveMutable(true);
        return headerAccessor.getMessageHeaders();
    }
}
