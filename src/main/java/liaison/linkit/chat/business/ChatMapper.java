package liaison.linkit.chat.business;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;
import liaison.linkit.chat.domain.ChatMessage;
import liaison.linkit.chat.domain.ChatRoom;
import liaison.linkit.chat.domain.type.ParticipantType;
import liaison.linkit.chat.presentation.dto.ChatResponseDTO;
import liaison.linkit.chat.presentation.dto.ChatResponseDTO.ChatLeftMenu;
import liaison.linkit.chat.presentation.dto.ChatResponseDTO.ChatMessageHistoryResponse;
import liaison.linkit.chat.presentation.dto.ChatResponseDTO.ChatPartnerInformation;
import liaison.linkit.chat.presentation.dto.ChatResponseDTO.ChatRoomLeaveResponse;
import liaison.linkit.chat.presentation.dto.ChatResponseDTO.ChatRoomSummary;
import liaison.linkit.chat.presentation.dto.ChatResponseDTO.CreateChatRoomResponse;
import liaison.linkit.chat.presentation.dto.ChatResponseDTO.ReadChatMessageResponse;
import liaison.linkit.common.annotation.Mapper;
import liaison.linkit.matching.domain.type.SenderType;
import org.springframework.data.domain.Page;

@Mapper
public class ChatMapper {

    public ReadChatMessageResponse toReadChatMessageResponse(final Long chatRoomId, final Long updatedCount) {
        return ReadChatMessageResponse.builder()
            .chatRoomId(chatRoomId)
            .readMessagesCount(updatedCount)
            .build();
    }

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

    // ChatMapper.java (예시)
    public ChatMessageHistoryResponse toChatMessageHistoryResponse(
        final ChatRoom chatRoom,
        final Page<ChatMessage> messagePage,
        final Long memberId,
        final ChatPartnerInformation partnerInfo,
        final boolean isPartnerOnline
    ) {
        return ChatMessageHistoryResponse.builder()
            .totalElements(messagePage.getTotalElements())
            .totalPages(messagePage.getTotalPages())
            .hasNext(messagePage.hasNext())
            .isChatPartnerOnline(isPartnerOnline)
            .chatPartnerInformation(partnerInfo)
            .messages(
                messagePage.getContent().stream()
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
        ParticipantType myType = determineMyParticipantType(chatRoom, message, memberId);
        ParticipantType messageSenderType = message.getMessageSenderParticipantType();

        // String 타입으로 비교
        boolean isMyMessage = myType.equals(messageSenderType);

        return ChatResponseDTO.ChatMessageResponse.builder()
            .messageId(message.getId())
            .chatRoomId(message.getChatRoomId())
            .myParticipantType(myType.name())
            .messageSenderParticipantType(message.getMessageSenderParticipantType())
            .isMyMessage(isMyMessage)
            .messageSenderLogoImagePath(message.getMessageSenderLogoImagePath())
            .content(message.getContent())
            .timestamp(message.getTimestamp())
            .isRead(message.isRead())
            .build();
    }

    public ChatMessage toChatMessage(
        final Long chatRoomId,
        final String chatMessageContent,
        final ParticipantType participantType,
        final String senderKeyId,
        final Long senderMemberId,
        final String senderName,
        final String senderLogoImagePath,
        final SenderType senderType,
        final Long receiverMemberId
    ) {
        return ChatMessage.builder()
            .chatRoomId(chatRoomId)
            .messageSenderParticipantType(participantType)
            .messageSenderKeyId(senderKeyId)
            .messageSenderMemberId(senderMemberId)
            .messageSenderName(senderName)
            .messageSenderLogoImagePath(senderLogoImagePath)
            .messageSenderType(senderType)
            .messageReceiverMemberId(receiverMemberId)
            .content(chatMessageContent)
            .timestamp(LocalDateTime.now(ZoneId.of("Asia/Seoul")))
            .isRead(false)
            .build();
    }

    public ChatLeftMenu toChatLeftMenu(final List<ChatRoomSummary> chatRoomSummaries) {
        return ChatLeftMenu.builder()
            .chatRoomSummaries(chatRoomSummaries)
            .build();
    }

    private ParticipantType determineMyParticipantType(ChatRoom chatRoom, ChatMessage message, Long memberId) {
        if (chatRoom.getParticipantAMemberId().equals(memberId)) {
            return ParticipantType.A_TYPE;
        } else {
            return ParticipantType.B_TYPE;
        }
    }

    public ChatResponseDTO.ChatRoomLeaveResponse toLeaveChatRoom(final Long chatRoomId, final ParticipantType participantType) {
        return ChatRoomLeaveResponse.builder()
            .chatRoomId(chatRoomId)
            .chatRoomLeaveParticipantType(participantType)
            .build();
    }
}
