package liaison.linkit.chat.domain;

import static lombok.AccessLevel.PROTECTED;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Builder;

@Document(collection = "chat_messages")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
public class ChatMessage {

    @Id
    private String id;

    // 메시지가 속한 채팅방 ID
    private Long chatRoomId;

    // 실제 메시지를 보낸 회원의 ID
    private Long senderMemberId;

    // 메시지 내용
    private String content;

    // 메시지 전송 시간
    private LocalDateTime timestamp;

    // 메시지 읽음 여부
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
            final Long senderMemberId,
            final String content
    ) {
        this.chatRoomId = chatRoomId;
        this.senderMemberId = senderMemberId;
        this.content = content;
        this.timestamp = LocalDateTime.now();
        this.isRead = false;
    }
}
