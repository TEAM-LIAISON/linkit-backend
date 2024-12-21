package liaison.linkit.chat.domain;

import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Getter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
public class Chatting {

    @Id
    private String id;
    private Integer chatRoomNo;
    private Integer senderNo;
    private String senderName;
    private String contentType;
    private String content;
    private LocalDateTime sendDate;
    private long readCount;
}
