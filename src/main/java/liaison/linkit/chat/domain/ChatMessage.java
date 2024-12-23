package liaison.linkit.chat.domain;

import static lombok.AccessLevel.PROTECTED;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

// MongoDB에 저장 (Collection -> chat_messages)
@Document(collection = "chat_messages")
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
public class ChatMessage {

    @Id
    private String id;
    private String chatRoomId;
    private String senderMemberId;
    private String senderMemberName;
    private String messageContent;
    private LocalDateTime timeStamp;
    
}
