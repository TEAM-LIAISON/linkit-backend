package liaison.linkit.matching.domain;

import lombok.Getter;

@Getter
public class MatchingEmailContent {
    private final String senderMailTitle;
    private final String senderMailSubTitle;
    private final String senderMailSubText;
    private final String receiverMailTitle;
    private final String receiverMailSubTitle;
    private final String receiverMailSubText;

    // 완료 이메일용 생성자 (발신자+수신자 정보 모두 포함)
    public MatchingEmailContent(
            String senderMailTitle,
            String senderMailSubTitle,
            String senderMailSubText,
            String receiverMailTitle,
            String receiverMailSubTitle,
            String receiverMailSubText
    ) {
        this.senderMailTitle = senderMailTitle;
        this.senderMailSubTitle = senderMailSubTitle;
        this.senderMailSubText = senderMailSubText;
        this.receiverMailTitle = receiverMailTitle;
        this.receiverMailSubTitle = receiverMailSubTitle;
        this.receiverMailSubText = receiverMailSubText;
    }

    // 요청 이메일용 생성자 (수신자 정보만 포함)
    public MatchingEmailContent(
            String receiverMailTitle,
            String receiverMailSubTitle,
            String receiverMailSubText
    ) {
        this(
                "",  // senderMailTitle (미사용)
                "",  // senderMailSubTitle (미사용)
                "",  // senderMailSubText (미사용)
                receiverMailTitle,
                receiverMailSubTitle,
                receiverMailSubText
        );
    }
}
