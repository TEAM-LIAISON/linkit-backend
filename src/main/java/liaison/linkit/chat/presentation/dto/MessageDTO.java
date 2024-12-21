package liaison.linkit.chat.presentation.dto;

import liaison.linkit.chat.domain.type.MessageType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MessageDTO {
    private MessageType type;
    private Long roomId;
    private String detailMessage;
    private Long senderId;
}
