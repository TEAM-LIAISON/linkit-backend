//package liaison.linkit.chat.service;
//
//import liaison.linkit.chat.business.ChatMapper;
//import liaison.linkit.chat.domain.ChatMessage;
//import liaison.linkit.chat.domain.repository.chatMessage.ChatMessageRepository;
//import liaison.linkit.chat.domain.ChatRoom;
//import liaison.linkit.chat.exception.MatchingStateChatBadRequestException;
//import liaison.linkit.chat.implement.ChatRoomCommandAdapter;
//import liaison.linkit.chat.implement.ChatRoomQueryAdapter;
//import liaison.linkit.chat.presentation.dto.ChatRequestDTO.CreateChatRoomRequest;
//import liaison.linkit.chat.presentation.dto.ChatResponseDTO;
//import liaison.linkit.matching.implement.MatchingQueryAdapter;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
//
//@Service
//@RequiredArgsConstructor
//@Transactional
//@Slf4j
//public class ChatService {
//
//    private final ChatRoomQueryAdapter chatRoomQueryAdapter;
//    private final ChatRoomCommandAdapter chatRoomCommandAdapter;
//    private final ChatMessageRepository chatMessageRepository;
//    private final SimpMessagingTemplate messagingTemplate;
//    private final ChatMapper chatMapper;
//    private final MatchingQueryAdapter matchingQueryAdapter;
//
//    /**
//     * 채팅 메시지 전송 처리
//     *
//     * @param request  채팅 메시지 요청 정보
//     * @param senderId 실제 메시지를 보내는 회원의 ID
//     */
//    public void sendMessage(ChatMessageRequest request, Long senderId) {
//        // 채팅방 존재 여부 확인
//        ChatRoom chatRoom = chatRoomRepository.findById(request.getChatRoomId())
//                .orElseThrow(() -> new ChatRoomNotFoundException());
//
//        // 발신자의 채팅방 접근 권한 확인
//        validateChatAccess(chatRoom, senderId, request.getSenderTeamId());
//
//        // 채팅 메시지 생성
//        ChatMessage message = ChatMessage.builder()
//                .chatRoomId(chatRoom.getId())
//                .senderId(senderId)
//                .senderType(request.getSenderType())
//                .senderEntityId(request.getSenderEntityId())
//                .content(request.getContent())
//                .build();
//
//        // 메시지 저장
//        ChatMessage savedMessage = chatMessageRepository.save(message);
//
//        // WebSocket을 통해 구독자들에게 실시간 메시지 전송
//        messagingTemplate.convertAndSend(
//                "/topic/chat/room/" + chatRoom.getId(),
//                savedMessage
//        );
//    }
//
//    /**
//     * 새로운 채팅방 생성
//     *
//     * @param createChatRoomRequest 채팅방 생성 요청 정보
//     * @param memberId              채팅방 생성을 요청한 회원의 ID
//     * @return 생성된 채팅방 정보
//     */
//    public ChatResponseDTO.CreateChatRoomResponse createChatRoom(final CreateChatRoomRequest createChatRoomRequest, final Long memberId) {
//
//        final Long matchingId = createChatRoomRequest.getMatchingId();
//
//        if (!matchingQueryAdapter.isCompletedMatching(matchingId)) {
//            throw MatchingStateChatBadRequestException.EXCEPTION;
//        }
//
//        // 채팅방 생성
//        final ChatRoom chatRoom = ChatRoom.builder()
//                .participantAId(request.getParticipantAId())
//                .participantAType(request.getParticipantAType())
//                .participantBId(request.getParticipantBId())
//                .participantBType(request.getParticipantBType())
//                .build();
//
//        final ChatRoom savedChatRoom = chatRoomCommandAdapter.createChatRoom(chatRoom);
//
//        return chatMapper.toCreateChatRoomResponse(savedChatRoom);
//    }
//
//    /**
//     * 채팅방 접근 권한 검증
//     */
//    private void validateChatAccess(ChatRoom chatRoom, Long memberId, Long teamId) {
//        if (!chatRoom.canAccessChat(memberId, teamId)) {
//            throw new UnauthorizedChatAccessException();
//        }
//    }
//}
