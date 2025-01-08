package liaison.linkit.chat.business;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import liaison.linkit.chat.domain.ChatMessage;
import liaison.linkit.chat.domain.ChatRoom;
import liaison.linkit.chat.domain.ChatRoom.ParticipantType;
import liaison.linkit.chat.presentation.dto.ChatRequestDTO.ChatMessageRequest;
import liaison.linkit.chat.presentation.dto.ChatResponseDTO;
import liaison.linkit.chat.presentation.dto.ChatResponseDTO.ChatLeftMenu;
import liaison.linkit.chat.presentation.dto.ChatResponseDTO.ChatRoomSummary;
import liaison.linkit.chat.presentation.dto.ChatResponseDTO.CreateChatRoomResponse;
import liaison.linkit.common.annotation.Mapper;
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
            final Page<ChatMessage> messagePage
    ) {
        return ChatResponseDTO.ChatMessageHistoryResponse.builder()
                .totalElements(messagePage.getTotalElements())
                .totalPages(messagePage.getTotalPages())
                .hasNext(messagePage.hasNext())
                .messages(messagePage.getContent().stream()
                        .map(this::toChatMessageResponse)
                        .collect(Collectors.toList()))
                .build();
    }

    public ChatResponseDTO.ChatMessageResponse toChatMessageResponse(
            final ChatMessage message
    ) {
        return ChatResponseDTO.ChatMessageResponse.builder()
                .messageId(message.getId())
                .chatRoomId(message.getChatRoomId())
                .messageSenderType(message.getMessageSenderType())
                .messageSenderId(message.getMessageSenderId())
                .content(message.getContent())
                .timestamp(message.getTimestamp())
                .isRead(message.isRead())
                .build();
    }

    public ChatMessage toChatMessage(
            final ChatMessageRequest chatMessageRequest,
            final String participantId,
            final ParticipantType participantType
    ) {
        return ChatMessage.builder()
                .chatRoomId(chatMessageRequest.getChatRoomId())
                .messageSenderId(participantId)
                .messageSenderType(participantType)
                .content(chatMessageRequest.getContent())
                .timestamp(LocalDateTime.now())
                .isRead(false)
                .build();
    }

    public ChatLeftMenu toChatLeftMenu(final List<ChatRoomSummary> chatRoomSummaries) {
        return ChatLeftMenu.builder()
                .chatRoomSummaries(chatRoomSummaries)
                .build();
    }
}
