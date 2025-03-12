package liaison.linkit.chat.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UnreadMessageNotificationDTO {
    private String messageId;
    private Long chatRoomId;
    private String senderName;
    private String senderType;
    private String receiverEmail;
    private Long receiverMemberId;
    private String messagePreview;

    // UnreadMessageNotificationDTO 클래스에 다음 메서드 추가
    public void setMessagePreview(String messagePreview) {
        this.messagePreview = messagePreview;
    }
}
