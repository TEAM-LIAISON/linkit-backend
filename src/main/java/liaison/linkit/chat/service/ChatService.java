package liaison.linkit.chat.service;

import java.util.ArrayList;
import java.util.List;
import liaison.linkit.chat.business.ChatMapper;
import liaison.linkit.chat.domain.ChatMessage;
import liaison.linkit.chat.domain.ChatRoom;
import liaison.linkit.chat.domain.repository.chatMessage.ChatMessageRepository;
import liaison.linkit.chat.domain.type.CreateChatLocation;
import liaison.linkit.chat.domain.type.ParticipantType;
import liaison.linkit.chat.exception.CreateChatReceiverBadRequestException;
import liaison.linkit.chat.exception.CreateChatRoomBadRequestException;
import liaison.linkit.chat.exception.CreateChatSenderBadRequestException;
import liaison.linkit.chat.exception.MatchingStateChatBadRequestException;
import liaison.linkit.chat.exception.SendChatMessageBadRequestException;
import liaison.linkit.chat.implement.ChatRoomCommandAdapter;
import liaison.linkit.chat.implement.ChatRoomQueryAdapter;
import liaison.linkit.chat.presentation.dto.ChatRequestDTO.ChatMessageRequest;
import liaison.linkit.chat.presentation.dto.ChatRequestDTO.CreateChatRoomRequest;
import liaison.linkit.chat.presentation.dto.ChatResponseDTO.ChatLeftMenu;
import liaison.linkit.chat.presentation.dto.ChatResponseDTO.ChatMessageHistoryResponse;
import liaison.linkit.chat.presentation.dto.ChatResponseDTO.ChatMessageResponse;
import liaison.linkit.chat.presentation.dto.ChatResponseDTO.ChatPartnerInformation;
import liaison.linkit.chat.presentation.dto.ChatResponseDTO.ChatRoomSummary;
import liaison.linkit.chat.presentation.dto.ChatResponseDTO.CreateChatRoomResponse;
import liaison.linkit.chat.presentation.dto.ChatResponseDTO.PartnerProfileDetailInformation;
import liaison.linkit.chat.presentation.dto.ChatResponseDTO.PartnerTeamDetailInformation;
import liaison.linkit.common.business.RegionMapper;
import liaison.linkit.common.implement.RegionQueryAdapter;
import liaison.linkit.common.presentation.RegionResponseDTO.RegionDetail;
import liaison.linkit.global.type.StatusType;
import liaison.linkit.global.util.SessionRegistry;
import liaison.linkit.matching.domain.Matching;
import liaison.linkit.matching.domain.type.ReceiverType;
import liaison.linkit.matching.domain.type.SenderType;
import liaison.linkit.matching.implement.MatchingCommandAdapter;
import liaison.linkit.matching.implement.MatchingQueryAdapter;
import liaison.linkit.member.domain.Member;
import liaison.linkit.member.implement.MemberQueryAdapter;
import liaison.linkit.profile.business.mapper.ProfilePositionMapper;
import liaison.linkit.profile.domain.position.ProfilePosition;
import liaison.linkit.profile.domain.profile.Profile;
import liaison.linkit.profile.domain.region.ProfileRegion;
import liaison.linkit.profile.implement.position.ProfilePositionQueryAdapter;
import liaison.linkit.profile.implement.profile.ProfileQueryAdapter;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO.ProfilePositionDetail;
import liaison.linkit.team.business.mapper.scale.TeamScaleMapper;
import liaison.linkit.team.domain.announcement.TeamMemberAnnouncement;
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
    private final MatchingQueryAdapter matchingQueryAdapter;

    private final ChatRoomCommandAdapter chatRoomCommandAdapter;
    private final SessionRegistry sessionRegistry;
    private final ProfilePositionQueryAdapter profilePositionQueryAdapter;
    private final ProfilePositionMapper profilePositionMapper;
    private final RegionQueryAdapter regionQueryAdapter;
    private final RegionMapper regionMapper;
    private final TeamScaleQueryAdapter teamScaleQueryAdapter;
    private final TeamScaleMapper teamScaleMapper;
    private final MatchingCommandAdapter matchingCommandAdapter;

    /**
     * 새로운 채팅방 생성
     *
     * @param createChatRoomRequest 채팅방 생성 요청 정보
     * @param memberId              채팅방 생성을 요청한 회원의 ID
     * @return 생성된 채팅방 정보
     */
    public CreateChatRoomResponse createChatRoom(final CreateChatRoomRequest createChatRoomRequest, final Long memberId) {

        // 채팅방을 열게하는 매칭의 매칭 ID
        final Long matchingId = createChatRoomRequest.getMatchingId();

        // 채팅방 생성을 요청한 회원의 프로필
        final Profile profile = profileQueryAdapter.findByMemberId(memberId);

        // 매칭 성사된 매칭에 대해서만 채팅방을 열어주도록 설정
        if (!matchingQueryAdapter.isCompletedMatching(matchingId)) {
            throw MatchingStateChatBadRequestException.EXCEPTION;
        }

        if (chatRoomQueryAdapter.existsChatRoomByMatchingId(createChatRoomRequest.getMatchingId())) {
            throw CreateChatRoomBadRequestException.EXCEPTION;
        }

        // 2) 수신함(RECEIVED) or 발신함(SENT) 분기
        {
            if (createChatRoomRequest.getCreateChatLocation().equals(CreateChatLocation.RECEIVED)) {
                validateReceiverLogic(createChatRoomRequest, memberId, profile);
                return buildAndSaveChatRoomAsReceiver(createChatRoomRequest, memberId, profile);
            } else {
                validateSenderLogic(createChatRoomRequest, memberId, profile);
                return buildAndSaveChatRoomAsSender(createChatRoomRequest, memberId, profile);
            }
        }
    }

    private void validateReceiverLogic(CreateChatRoomRequest request, Long memberId, Profile profile) {
        // (1) ReceiverType 검사
        if (request.getReceiverType().equals(ReceiverType.PROFILE)) {
            // 수신자가 PROFILE이므로 receiverEmailId == 현재사용자(emailId)
            if (!request.getReceiverEmailId().equals(profile.getMember().getEmailId())) {
                throw CreateChatReceiverBadRequestException.EXCEPTION;
            }
        } else if (request.getReceiverType().equals(ReceiverType.TEAM)) {
            // 현재 user가 오너로 등록된 팀 목록
            List<Team> teams = teamMemberQueryAdapter.getAllTeamsInOwnerStateByMemberId(memberId);
            // receiverTeamCode가 이 중 하나여야 함
            boolean hasTeam = teams.stream()
                    .anyMatch(team -> team.getTeamCode().equals(request.getReceiverTeamCode()));
            if (!hasTeam) {
                throw CreateChatReceiverBadRequestException.EXCEPTION;
            }
        } else if (request.getReceiverType().equals(ReceiverType.ANNOUNCEMENT)) {
            // Announcement -> 실질적으로 팀 오너인지 확인
            List<Team> teams = teamMemberQueryAdapter.getAllTeamsInOwnerStateByMemberId(memberId);
            List<Long> teamIds = teams.stream().map(Team::getId).toList();

            List<TeamMemberAnnouncement> announcements =
                    teamMemberAnnouncementQueryAdapter.getAllByTeamIds(teamIds);

            boolean hasAnnouncement = announcements.stream()
                    .anyMatch(ann -> ann.getId().equals(request.getReceiverAnnouncementId()));

            if (!hasAnnouncement) {
                throw CreateChatReceiverBadRequestException.EXCEPTION;
            }
        }
    }

    // (수신함에서) 채팅방 생성
    private CreateChatRoomResponse buildAndSaveChatRoomAsReceiver(
            CreateChatRoomRequest request,
            Long memberId,
            Profile profile
    ) {
        // (2) 수신자가 현재 사용자 -> participantB
        // (3) 발신자 -> participantA

        // ---- participantB : 수신자 = 현재 사용자 ----
        String participantBId;
        Long participantBMemberId = memberId;
        String participantBName;
        ParticipantType participantBType;

        if (request.getReceiverType().equals(ReceiverType.PROFILE)) {
            participantBId = profile.getMember().getEmailId();
            participantBName = profile.getMember().getMemberBasicInform().getMemberName();
            participantBType = ParticipantType.PROFILE;
        } else if (request.getReceiverType().equals(ReceiverType.TEAM)) {
            participantBId = request.getReceiverTeamCode();
            final Team team = teamQueryAdapter.findByTeamCode(participantBId);
            participantBName = team.getTeamName();
            participantBType = ParticipantType.TEAM;
        } else {
            final TeamMemberAnnouncement teamMemberAnnouncement = teamMemberAnnouncementQueryAdapter.findById(request.getReceiverAnnouncementId());
            participantBId = teamMemberAnnouncement.getTeam().getTeamCode();
            participantBName = teamMemberAnnouncement.getTeam().getTeamName();
            participantBType = ParticipantType.TEAM;
        }

        // ---- participantA : 발신자 ----
        // request 안의 senderXxx 값을 이용
        String participantAId;
        Long participantAMemberId;
        String participantAName;
        ParticipantType participantAType;

        if (request.getSenderType().equals(SenderType.PROFILE)) {
            participantAId = request.getSenderEmailId();
            final Member member = memberQueryAdapter.findByEmailId(participantAId);
            participantAMemberId = member.getId();
            participantAName = member.getMemberBasicInform().getMemberName();
            participantAType = ParticipantType.PROFILE;
        } else { // TEAM
            participantAId = request.getSenderTeamCode();
            final Team team = teamQueryAdapter.findByTeamCode(participantAId);
            participantAMemberId = teamMemberQueryAdapter.getTeamOwnerMemberId(team);
            participantAName = team.getTeamName();
            participantAType = ParticipantType.TEAM;
        }

        ChatRoom chatRoom = ChatRoom.builder()
                .matchingId(request.getMatchingId())
                .participantAId(participantAId)
                .participantAMemberId(participantAMemberId)
                .participantAName(participantAName)
                .participantAType(participantAType)
                .participantAStatus(StatusType.USABLE)

                .participantBId(participantBId)
                .participantBMemberId(participantBMemberId)
                .participantBName(participantBName)
                .participantBType(participantBType)
                .participantBStatus(StatusType.USABLE)
                
                .build();

        ChatRoom saved = chatRoomCommandAdapter.createChatRoom(chatRoom);

        final Matching matching = matchingQueryAdapter.findByMatchingId(request.getMatchingId());
        matchingCommandAdapter.updateMatchingToCreatedRoomState(matching);

        return chatMapper.toCreateChatRoomResponse(saved);
    }

    private void validateSenderLogic(CreateChatRoomRequest request, Long memberId, Profile profile) {
        // (1) SenderType 검사
        if (request.getSenderType().equals(SenderType.PROFILE)) {
            // 발신자가 PROFILE인 경우 senderEmailId == 현재사용자(emailId)
            if (!request.getSenderEmailId().equals(profile.getMember().getEmailId())) {
                throw CreateChatSenderBadRequestException.EXCEPTION;
            }
        } else if (request.getSenderType().equals(SenderType.TEAM)) {
            List<Team> teams = teamMemberQueryAdapter.getAllTeamsInOwnerStateByMemberId(memberId);
            boolean hasTeam = teams.stream()
                    .anyMatch(team -> team.getTeamCode().equals(request.getSenderTeamCode()));
            if (!hasTeam) {
                throw CreateChatSenderBadRequestException.EXCEPTION;
            }
        }
        // ANNOUNCEMENT 등 추가 가능
    }

    // 발신함에서 채팅방 생성
    private CreateChatRoomResponse buildAndSaveChatRoomAsSender(
            CreateChatRoomRequest request,
            Long memberId,
            Profile profile
    ) {
        // (2) 발신자가 현재 사용자 -> participantA
        // (3) 수신자가 participantB

        // ---- participantA : 발신자 = 현재 사용자 ----
        String participantAId;
        Long participantAMemberId = memberId;
        String participantAName;
        ParticipantType participantAType;

        if (request.getSenderType().equals(SenderType.PROFILE)) {
            participantAId = profile.getMember().getEmailId();
            final Member member = memberQueryAdapter.findById(memberId);
            participantAName = member.getMemberBasicInform().getMemberName();
            participantAType = ParticipantType.PROFILE;
        } else { // TEAM
            participantAId = request.getSenderTeamCode();
            final Team team = teamQueryAdapter.findByTeamCode(participantAId);
            participantAName = team.getTeamName();
            participantAType = ParticipantType.TEAM;
        }

        // ---- participantB : 수신자 ----
        // request.getReceiverXxx 값 사용
        String participantBId;
        Long participantBMemberId;
        String participantBName;
        ParticipantType participantBType;

        if (request.getReceiverType().equals(ReceiverType.PROFILE)) {
            participantBId = request.getReceiverEmailId();
            final Member member = memberQueryAdapter.findByEmailId(participantBId);
            participantBMemberId = member.getId();
            participantBName = member.getMemberBasicInform().getMemberName();
            participantBType = ParticipantType.PROFILE;
        } else if (request.getReceiverType().equals(ReceiverType.TEAM)) {
            participantBId = request.getReceiverTeamCode();
            final Team team = teamQueryAdapter.findByTeamCode(participantBId);
            participantBMemberId = teamMemberQueryAdapter.getTeamOwnerMemberId(team);
            participantBName = team.getTeamName();
            participantBType = ParticipantType.TEAM;
        } else {

            final TeamMemberAnnouncement teamMemberAnnouncement = teamMemberAnnouncementQueryAdapter.findById(request.getReceiverAnnouncementId());
            participantBId = teamMemberAnnouncement.getTeam().getTeamCode();
            participantBMemberId = teamMemberQueryAdapter.getTeamOwnerMemberId(teamMemberAnnouncement.getTeam());
            participantBName = teamMemberAnnouncement.getTeam().getTeamName();
            participantBType = ParticipantType.TEAM;
        }

        ChatRoom chatRoom = ChatRoom.builder()
                .matchingId(request.getMatchingId())
                .participantAId(participantAId)
                .participantAMemberId(participantAMemberId)
                .participantAName(participantAName)
                .participantAType(participantAType)
                .participantBId(participantBId)
                .participantBName(participantBName)
                .participantBMemberId(participantBMemberId)
                .participantBType(participantBType)
                .build();

        ChatRoom saved = chatRoomCommandAdapter.createChatRoom(chatRoom);

        return chatMapper.toCreateChatRoomResponse(saved);
    }

    // 메시지 처리
    public void handleChatMessage(final ChatMessageRequest chatMessageRequest, final Long memberId) {
        log.info("handleChatMessage 호출: chatRoomId={}, content={}", chatMessageRequest.getChatRoomId(), chatMessageRequest.getContent());

        // 1. 채팅방 존재 및 접근 권한 확인
        final ChatRoom chatRoom = chatRoomQueryAdapter.findById(chatMessageRequest.getChatRoomId());

        // 2. 메시지 생성 및 저장
        ChatMessage chatMessage;
        
        if (chatRoom.getParticipantAMemberId().equals(memberId)) {
            // A가 B에게 보내는 메시지
            chatMessage = chatMapper.toChatMessage(
                    chatMessageRequest,
                    chatRoom.getParticipantAId(),
                    chatRoom.getParticipantAType(),
                    chatRoom.getParticipantBId(),
                    chatRoom.getParticipantBType(),
                    chatRoom.getParticipantBMemberId()
            );
        } else if (chatRoom.getParticipantBMemberId().equals(memberId)) {
            // B가 A에게 보내는 메시지
            chatMessage = chatMapper.toChatMessage(
                    chatMessageRequest,
                    chatRoom.getParticipantBId(),
                    chatRoom.getParticipantBType(),
                    chatRoom.getParticipantAId(),
                    chatRoom.getParticipantAType(),
                    chatRoom.getParticipantAMemberId()
            );
        } else {
            throw SendChatMessageBadRequestException.EXCEPTION;
        }

        chatMessageRepository.save(chatMessage);

        // 3. 채팅방 마지막 메시지 업데이트
        chatRoom.updateLastMessage(chatMessage.getContent(), chatMessage.getTimestamp());
        chatRoomCommandAdapter.save(chatRoom);

        // 4. 발신자용 메시지 응답 생성 (isMyMessage = true)
        ChatMessageResponse senderResponse = chatMapper.toChatMessageResponse(chatMessage, true);
        simpMessagingTemplate.convertAndSendToUser(
            memberId.toString(),
            "/sub/chat/" + chatRoom.getId(), 
            senderResponse
        );

        // 5. 수신자용 메시지 응답 생성 (isMyMessage = false)
        ChatMessageResponse receiverResponse = chatMapper.toChatMessageResponse(chatMessage, false);
        simpMessagingTemplate.convertAndSendToUser(
            chatMessage.getMessageReceiverMemberId().toString(),
            "/sub/chat/" + chatRoom.getId(), 
            receiverResponse
        );
    }

    /**
     * 채팅방의 이전 메시지 내역 조회
     */
    @Transactional(readOnly = true)
    public ChatMessageHistoryResponse getChatMessages(
            final Long chatRoomId,
            final Long memberId,
            final Pageable pageable
    ) {
        // 2. 메시지 조회 및 읽음 처리
        Page<ChatMessage> messages = chatMessageRepository.findByChatRoomIdOrderByTimestampDesc(
                chatRoomId,
                pageable
        );

        // 3. 읽지 않은 메시지 읽음 처리
        updateUnreadMessages(chatRoomId, memberId);

        return chatMapper.toChatMessageHistoryResponse(messages);
    }

    @Transactional(readOnly = true)
    public ChatLeftMenu getChatLeftMenu(
            final Long memberId
    ) {
        final List<ChatRoom> chatRooms = chatRoomQueryAdapter.findAllChatRoomsByMemberId(memberId);

        final List<ChatRoomSummary> chatRoomSummaries = new ArrayList<>();
        for (ChatRoom chatRoom : chatRooms) {
            if (chatRoom.getParticipantAMemberId().equals(memberId)) {
                final Member chatPartnerMember = memberQueryAdapter.findById(chatRoom.getParticipantBMemberId());

                if (chatRoom.getParticipantBType().equals(ParticipantType.PROFILE)) {
                    final Profile chatPartnerProfile = chatPartnerMember.getProfile();

                    ProfilePositionDetail profilePositionDetail = new ProfilePositionDetail();
                    if (profilePositionQueryAdapter.existsProfilePositionByProfileId(chatPartnerProfile.getId())) {
                        final ProfilePosition profilePosition = profilePositionQueryAdapter.findProfilePositionByProfileId(chatPartnerProfile.getId());
                        profilePositionDetail = profilePositionMapper.toProfilePositionDetail(profilePosition);
                    }

                    RegionDetail regionDetail = new RegionDetail();
                    if (regionQueryAdapter.existsProfileRegionByProfileId((chatPartnerProfile.getId()))) {
                        final ProfileRegion profileRegion = regionQueryAdapter.findProfileRegionByProfileId(chatPartnerProfile.getId());
                        regionDetail = regionMapper.toRegionDetail(profileRegion.getRegion());
                    }

                    ChatRoomSummary chatRoomSummary = ChatRoomSummary.builder()
                            .chatRoomId(chatRoom.getId())
                            .chatPartnerInformation(
                                    ChatPartnerInformation.builder()
                                            .chatPartnerName(chatPartnerMember.getMemberBasicInform().getMemberName())
                                            .chatPartnerImageUrl(chatPartnerMember.getProfile().getProfileImagePath())
                                            .partnerProfileDetailInformation(
                                                    PartnerProfileDetailInformation.builder()
                                                            .profilePositionDetail(profilePositionDetail)
                                                            .regionDetail(regionDetail)
                                                            .build()
                                            )
                                            .build()
                            )
                            .build();

                    chatRoomSummaries.add(chatRoomSummary);
                } else if (chatRoom.getParticipantBType().equals(ParticipantType.TEAM)) {
                    final Team chatPartnerTeam = teamQueryAdapter.findByTeamCode(chatRoom.getParticipantBId());

                    TeamScaleItem teamScaleItem = new TeamScaleItem();
                    if (teamScaleQueryAdapter.existsTeamScaleByTeamId(chatPartnerTeam.getId())) {
                        final TeamScale teamScale = teamScaleQueryAdapter.findTeamScaleByTeamId(chatPartnerTeam.getId());
                        teamScaleItem = teamScaleMapper.toTeamScaleItem(teamScale);
                    }

                    RegionDetail regionDetail = new RegionDetail();
                    if (regionQueryAdapter.existsTeamRegionByTeamId((chatPartnerTeam.getId()))) {
                        final TeamRegion teamRegion = regionQueryAdapter.findTeamRegionByTeamId(chatPartnerTeam.getId());
                        regionDetail = regionMapper.toRegionDetail(teamRegion.getRegion());
                    }

                    ChatRoomSummary chatRoomSummary = ChatRoomSummary.builder()
                            .chatRoomId(chatRoom.getId())
                            .chatPartnerInformation(
                                    ChatPartnerInformation.builder()
                                            .chatPartnerName(chatPartnerMember.getMemberBasicInform().getMemberName())
                                            .chatPartnerImageUrl(chatPartnerMember.getProfile().getProfileImagePath())
                                            .partnerTeamDetailInformation(
                                                    PartnerTeamDetailInformation.builder()
                                                            .teamScaleItem(teamScaleItem)
                                                            .regionDetail(regionDetail)
                                                            .build()
                                            )
                                            .build()
                            )
                            .build();

                    chatRoomSummaries.add(chatRoomSummary);
                } else {
                    final Team chatPartnerTeam = teamQueryAdapter.findByTeamCode(
                            teamMemberAnnouncementQueryAdapter.getTeamMemberAnnouncement(Long.valueOf(chatRoom.getParticipantBId())).getTeam().getTeamCode());
                    TeamScaleItem teamScaleItem = new TeamScaleItem();
                    if (teamScaleQueryAdapter.existsTeamScaleByTeamId(chatPartnerTeam.getId())) {
                        final TeamScale teamScale = teamScaleQueryAdapter.findTeamScaleByTeamId(chatPartnerTeam.getId());
                        teamScaleItem = teamScaleMapper.toTeamScaleItem(teamScale);
                    }

                    RegionDetail regionDetail = new RegionDetail();
                    if (regionQueryAdapter.existsTeamRegionByTeamId((chatPartnerTeam.getId()))) {
                        final TeamRegion teamRegion = regionQueryAdapter.findTeamRegionByTeamId(chatPartnerTeam.getId());
                        regionDetail = regionMapper.toRegionDetail(teamRegion.getRegion());
                    }

                    ChatRoomSummary chatRoomSummary = ChatRoomSummary.builder()
                            .chatRoomId(chatRoom.getId())
                            .chatPartnerInformation(
                                    ChatPartnerInformation.builder()
                                            .chatPartnerName(chatPartnerMember.getMemberBasicInform().getMemberName())
                                            .chatPartnerImageUrl(chatPartnerMember.getProfile().getProfileImagePath())
                                            .partnerTeamDetailInformation(
                                                    PartnerTeamDetailInformation.builder()
                                                            .teamScaleItem(teamScaleItem)
                                                            .regionDetail(regionDetail)
                                                            .build()
                                            )
                                            .build()
                            )
                            .build();

                    chatRoomSummaries.add(chatRoomSummary);
                }


            } else {
                final Member chatPartnerMember = memberQueryAdapter.findById(chatRoom.getParticipantAMemberId());

                if (chatRoom.getParticipantAType().equals(ParticipantType.PROFILE)) {
                    final Profile chatPartnerProfile = chatPartnerMember.getProfile();

                    ProfilePositionDetail profilePositionDetail = new ProfilePositionDetail();
                    if (profilePositionQueryAdapter.existsProfilePositionByProfileId(chatPartnerProfile.getId())) {
                        final ProfilePosition profilePosition = profilePositionQueryAdapter.findProfilePositionByProfileId(chatPartnerProfile.getId());
                        profilePositionDetail = profilePositionMapper.toProfilePositionDetail(profilePosition);
                    }

                    RegionDetail regionDetail = new RegionDetail();
                    if (regionQueryAdapter.existsProfileRegionByProfileId((chatPartnerProfile.getId()))) {
                        final ProfileRegion profileRegion = regionQueryAdapter.findProfileRegionByProfileId(chatPartnerProfile.getId());
                        regionDetail = regionMapper.toRegionDetail(profileRegion.getRegion());
                    }

                    ChatRoomSummary chatRoomSummary = ChatRoomSummary.builder()
                            .chatRoomId(chatRoom.getId())
                            .chatPartnerInformation(
                                    ChatPartnerInformation.builder()
                                            .chatPartnerName(chatPartnerMember.getMemberBasicInform().getMemberName())
                                            .chatPartnerImageUrl(chatPartnerMember.getProfile().getProfileImagePath())
                                            .partnerProfileDetailInformation(
                                                    PartnerProfileDetailInformation.builder()
                                                            .profilePositionDetail(profilePositionDetail)
                                                            .regionDetail(regionDetail)
                                                            .build()
                                            )
                                            .build()
                            )
                            .build();

                    chatRoomSummaries.add(chatRoomSummary);
                } else if (chatRoom.getParticipantAType().equals(ParticipantType.TEAM)) {
                    final Team chatPartnerTeam = teamQueryAdapter.findByTeamCode(chatRoom.getParticipantAId());

                    TeamScaleItem teamScaleItem = new TeamScaleItem();
                    if (teamScaleQueryAdapter.existsTeamScaleByTeamId(chatPartnerTeam.getId())) {
                        final TeamScale teamScale = teamScaleQueryAdapter.findTeamScaleByTeamId(chatPartnerTeam.getId());
                        teamScaleItem = teamScaleMapper.toTeamScaleItem(teamScale);
                    }

                    RegionDetail regionDetail = new RegionDetail();
                    if (regionQueryAdapter.existsTeamRegionByTeamId((chatPartnerTeam.getId()))) {
                        final TeamRegion teamRegion = regionQueryAdapter.findTeamRegionByTeamId(chatPartnerTeam.getId());
                        regionDetail = regionMapper.toRegionDetail(teamRegion.getRegion());
                    }

                    ChatRoomSummary chatRoomSummary = ChatRoomSummary.builder()
                            .chatRoomId(chatRoom.getId())
                            .chatPartnerInformation(
                                    ChatPartnerInformation.builder()
                                            .chatPartnerName(chatPartnerMember.getMemberBasicInform().getMemberName())
                                            .chatPartnerImageUrl(chatPartnerMember.getProfile().getProfileImagePath())
                                            .partnerTeamDetailInformation(
                                                    PartnerTeamDetailInformation.builder()
                                                            .teamScaleItem(teamScaleItem)
                                                            .regionDetail(regionDetail)
                                                            .build()
                                            )
                                            .build()
                            )
                            .build();

                    chatRoomSummaries.add(chatRoomSummary);
                } else {
                    final Team chatPartnerTeam = teamQueryAdapter.findByTeamCode(
                            teamMemberAnnouncementQueryAdapter.getTeamMemberAnnouncement(Long.valueOf(chatRoom.getParticipantAId())).getTeam().getTeamCode());
                    TeamScaleItem teamScaleItem = new TeamScaleItem();
                    if (teamScaleQueryAdapter.existsTeamScaleByTeamId(chatPartnerTeam.getId())) {
                        final TeamScale teamScale = teamScaleQueryAdapter.findTeamScaleByTeamId(chatPartnerTeam.getId());
                        teamScaleItem = teamScaleMapper.toTeamScaleItem(teamScale);
                    }

                    RegionDetail regionDetail = new RegionDetail();
                    if (regionQueryAdapter.existsTeamRegionByTeamId((chatPartnerTeam.getId()))) {
                        final TeamRegion teamRegion = regionQueryAdapter.findTeamRegionByTeamId(chatPartnerTeam.getId());
                        regionDetail = regionMapper.toRegionDetail(teamRegion.getRegion());
                    }

                    ChatRoomSummary chatRoomSummary = ChatRoomSummary.builder()
                            .chatRoomId(chatRoom.getId())
                            .chatPartnerInformation(
                                    ChatPartnerInformation.builder()
                                            .chatPartnerName(chatPartnerMember.getMemberBasicInform().getMemberName())
                                            .chatPartnerImageUrl(chatPartnerMember.getProfile().getProfileImagePath())
                                            .partnerTeamDetailInformation(
                                                    PartnerTeamDetailInformation.builder()
                                                            .teamScaleItem(teamScaleItem)
                                                            .regionDetail(regionDetail)
                                                            .build()
                                            )
                                            .build()
                            )
                            .build();

                    chatRoomSummaries.add(chatRoomSummary);
                }
            }
        }

        return chatMapper.toChatLeftMenu(chatRoomSummaries);
    }

    /**
     * 읽지 않은 메시지 읽음 처리
     */
    private void updateUnreadMessages(final Long chatRoomId, final Long memberId) {
        List<ChatMessage> unreadMessages = chatMessageRepository.findByChatRoomIdAndIsReadFalseAndReceiverParticipantId(
                chatRoomId
        );

        unreadMessages.forEach(ChatMessage::markAsRead);
        chatMessageRepository.saveAll(unreadMessages);
    }
}
