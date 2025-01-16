package liaison.linkit.chat.domain;

import static lombok.AccessLevel.PROTECTED;

import java.time.LocalDateTime;
import liaison.linkit.chat.domain.type.ParticipantType;
import liaison.linkit.matching.domain.type.SenderType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

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

    @Field("message_sender_participant_type")
    private ParticipantType messageSenderParticipantType;

    // 채팅방의 참여자 ID (Profile의 경우 emailId, Team의 경우 teamCode)
    @Field("message_sender_key_id")
    private String messageSenderKeyId;

    @Field("message_sender_member_id")
    private Long messageSenderMemberId;

    @Field("message_sender_name")
    private String messageSenderName;

    @Field("message_sender_logo_image_path")
    private String messageSenderLogoImagePath;

    @Field("message_sender_type")
    private SenderType messageSenderType;

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
}
