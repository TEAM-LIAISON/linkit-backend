package liaison.linkit.chat.domain;

import static lombok.AccessLevel.PROTECTED;

import java.time.LocalDateTime;
import liaison.linkit.chat.domain.ChatRoom.ParticipantType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import lombok.Builder;

@Document(collection = "chat_messages")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
public class ChatMessage {

    @Id
    private String id;

    // 메시지가 속한 채팅방 ID (인덱싱)
    @Indexed
    @Field("chat_room_id")
    private Long chatRoomId;

    @Field("message_sender_type")
    private ParticipantType messageSenderType;

    @Field("message_sender_id")
    private String messageSenderId;

    // 메시지 내용
    private String content;

    // 메시지 전송 시간 (인덱싱)
    @Indexed
    private LocalDateTime timestamp;

    // 메시지 읽음 여부 (인덱싱)
    @Indexed
    @Field("is_read")
    private boolean isRead;

    public void markAsRead() {
        this.isRead = true;
    }

    /**
     * 채팅 메시지 생성을 위한 빌더 패턴 생성자
     */
    @Builder
    public ChatMessage(
            final Long chatRoomId,
            final ParticipantType messageSenderType,
            final String messageSenderId,
            final String content
    ) {
        this.chatRoomId = chatRoomId;

        this.content = content;
        this.messageSenderType = messageSenderType;
        this.messageSenderId = messageSenderId;
        this.timestamp = LocalDateTime.now();
        this.isRead = false;
    }
}
