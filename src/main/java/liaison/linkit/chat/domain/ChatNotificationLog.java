package liaison.linkit.chat.domain;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "chat_notification_logs")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatNotificationLog {

    @Id private String id;

    @Field("chat_message_id")
    private String chatMessageId;

    @Field("chat_room_id")
    private Long chatRoomId;

    @Field("receiver_member_id")
    private Long receiverMemberId;

    @Field("sent_at")
    private LocalDateTime sentAt;
}
