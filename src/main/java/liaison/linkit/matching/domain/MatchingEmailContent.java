package liaison.linkit.matching.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MatchingEmailContent {
    private final String senderMailTitle;
    private final String senderMailSubTitle;
    private final String senderMailSubText;
    private final String receiverMailTitle;
    private final String receiverMailSubTitle;
    private final String receiverMailSubText;

}
