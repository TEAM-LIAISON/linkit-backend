package liaison.linkit.chat.business;

import java.util.List;
import java.util.stream.Collectors;
import liaison.linkit.chat.domain.ChatMessage;
import liaison.linkit.chat.domain.ChatRoom;
import liaison.linkit.chat.domain.type.ParticipantType;
import liaison.linkit.chat.presentation.dto.ChatRequestDTO.ChatMessageRequest;
import liaison.linkit.chat.presentation.dto.ChatResponseDTO;
import liaison.linkit.chat.presentation.dto.ChatResponseDTO.ChatLeftMenu;
import liaison.linkit.chat.presentation.dto.ChatResponseDTO.ChatRoomSummary;
import liaison.linkit.chat.presentation.dto.ChatResponseDTO.CreateChatRoomResponse;
import liaison.linkit.common.annotation.Mapper;
import liaison.linkit.matching.domain.type.SenderType;
import org.springframework.data.domain.Page;

@Mapper
public class ChatMapper {
    public ChatResponseDTO.CreateChatRoomResponse toCreateChatRoomResponse(
            final ChatRoom chatRoom
    ) {
        return CreateChatRoomResponse.builder()
                .chatRoomId(chatRoom.getId())
                .matchingId(chatRoom.getMatchingId())
                .participantAId(chatRoom.getParticipantAId())
                .participantAType(chatRoom.getParticipantAType())
                .participantAName(chatRoom.getParticipantAName())
                .participantBId(chatRoom.getParticipantBId())
                .participantBType(chatRoom.getParticipantBType())
                .participantBName(chatRoom.getParticipantBName())
                .lastMessage(null)
                .unreadCount(0L)
                .build();
    }

    public ChatResponseDTO.ChatMessageHistoryResponse toChatMessageHistoryResponse(
            final ChatRoom chatRoom,
            final Page<ChatMessage> messagePage,
            final Long memberId
    ) {
        return ChatResponseDTO.ChatMessageHistoryResponse.builder()
                .totalElements(messagePage.getTotalElements())
                .totalPages(messagePage.getTotalPages())
                .hasNext(messagePage.hasNext())
                .messages(
                        messagePage.getContent().stream()
                                // 여기서 message와 memberId를 함께 넘김
                                .map(message -> toChatMessageResponse(chatRoom, message, memberId))
                                .collect(Collectors.toList())
                )
                .build();
    }

    public ChatResponseDTO.ChatMessageResponse toChatMessageResponse(
            final ChatRoom chatRoom,
            final ChatMessage message,
            final Long memberId
    ) {
        return ChatResponseDTO.ChatMessageResponse.builder()
                .messageId(message.getId())                               // 메시지 ID
                .chatRoomId(message.getChatRoomId())                      // 채팅방 ID

                // '나의 참여 타입' (예: A_TYPE / B_TYPE) 판단 로직
                .myParticipantType(determineMyParticipantType(chatRoom, message, memberId))

                // 메시지 발신자 A/B 참여 타입
                .messageSenderParticipantType(message.getMessageSenderParticipantType())

                // 메시지 발신자의 로고 이미지 경로
                .messageSenderLogoImagePath(message.getMessageSenderLogoImagePath())

                // 발신자 타입(팀/개인 등)
                .messageSenderType(message.getMessageSenderType())

                // 메시지 내용
                .content(message.getContent())

                // 전송 시간
                .timestamp(message.getTimestamp())

                // 읽음 여부
                .isRead(message.isRead())
                .build();
    }

    public ChatMessage toChatMessage(
            final ChatMessageRequest chatMessageRequest,
            final ParticipantType participantType,
            final String senderKeyId,
            final Long senderMemberId,
            final String senderName,
            final String senderLogoImagePath,
            final SenderType senderType
    ) {
        return ChatMessage.builder()
                .chatRoomId(chatMessageRequest.getChatRoomId())
                .messageSenderParticipantType(participantType)
                .messageSenderKeyId(senderKeyId)
                .messageSenderMemberId(senderMemberId)
                .messageSenderName(senderName)
                .messageSenderLogoImagePath(senderLogoImagePath)
                .messageSenderType(senderType)
                .content(chatMessageRequest.getContent())
                .build();
    }

    public ChatLeftMenu toChatLeftMenu(final List<ChatRoomSummary> chatRoomSummaries) {
        return ChatLeftMenu.builder()
                .chatRoomSummaries(chatRoomSummaries)
                .build();
    }

    public ChatResponseDTO.ChatMessageResponse toChatMessageResponse(ChatMessage chatMessage, boolean isMyMessage) {
        return ChatResponseDTO.ChatMessageResponse.builder()
                .messageId(chatMessage.getId())
                .chatRoomId(chatMessage.getChatRoomId())
                .content(chatMessage.getContent())
                .timestamp(chatMessage.getTimestamp())
                .isRead(chatMessage.isRead())
                .build();
    }

    private String determineMyParticipantType(ChatRoom chatRoom, ChatMessage message, Long memberId) {
        if (chatRoom.getParticipantAMemberId().equals(memberId)) {
            return "A_TYPE";
        } else {
            return "B_TYPE";
        }
    }
}
