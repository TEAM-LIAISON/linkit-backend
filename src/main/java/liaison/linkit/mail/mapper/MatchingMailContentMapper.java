package liaison.linkit.mail.mapper;

import liaison.linkit.common.annotation.Mapper;
import liaison.linkit.matching.domain.Matching;
import liaison.linkit.matching.domain.MatchingEmailContent;
import liaison.linkit.matching.domain.type.ReceiverType;
import liaison.linkit.matching.domain.type.SenderType;

@Mapper
public class MatchingMailContentMapper {

    public MatchingEmailContent generateMatchingRequestedEmailContent(final Matching matching) {
        String receiverMailTitle;
        String receiverMailSubTitle;
        String receiverMailSubText;

        if (matching.getReceiverType().equals(ReceiverType.ANNOUNCEMENT)) {
            receiverMailTitle = "공고 지원";
            receiverMailSubTitle = "님의 공고 지원";
            receiverMailSubText = "새로운 지원이 왔어요";
        } else if (matching.getSenderType().equals(SenderType.TEAM)) {
            receiverMailTitle = "매칭 요청";
            receiverMailSubTitle = "팀의 매칭 요청";
            receiverMailSubText = "새로운 매칭 요청이 왔어요";
        } else {
            receiverMailTitle = "매칭 요청";
            receiverMailSubTitle = "님의 매칭 요청";
            receiverMailSubText = "새로운 매칭 요청이 왔어요";
        }

        return new MatchingEmailContent(
            receiverMailTitle, receiverMailSubTitle, receiverMailSubText
        );
    }

    // --- 매칭 성사에 대한 email_content ---

    /**
     * 이메일 제목, 소제목, 세부 내용을 생성하는 메서드
     */
    public MatchingEmailContent generateMatchingCompletedEmailContent(
        final Matching matching
    ) {
        String senderMailTitle;
        String senderMailSubTitle;
        String senderMailSubText;
        String receiverMailTitle;
        String receiverMailSubTitle;
        String receiverMailSubText;

        if (matching.getReceiverType().equals(ReceiverType.ANNOUNCEMENT)) {
            senderMailTitle = "지원 수락";
            senderMailSubTitle = "팀의 지원 수락!";
            senderMailSubText = "지원이 수락되었어요";

            receiverMailTitle = "지원 수락";
            receiverMailSubTitle = "님의 지원 수락!";
            receiverMailSubText = "지원을 수락했어요";
        } else {
            senderMailTitle = "매칭 성사";
            senderMailSubTitle = generateSubTitle(matching.getSenderType(), matching.getReceiverType(), true);
            senderMailSubText = "매칭이 성사되었어요";

            receiverMailTitle = "매칭 성사";
            receiverMailSubTitle = generateSubTitle(matching.getSenderType(), matching.getReceiverType(), false);
            receiverMailSubText = "매칭이 성사되었어요";
        }

        return new MatchingEmailContent(
            senderMailTitle, senderMailSubTitle, senderMailSubText,
            receiverMailTitle, receiverMailSubTitle, receiverMailSubText
        );
    }

    /**
     * 이메일 소제목 생성
     */
    private String generateSubTitle(
        final SenderType senderType,
        final ReceiverType receiverType,
        final boolean isSender
    ) {
        if (senderType.equals(SenderType.PROFILE) && receiverType.equals(ReceiverType.PROFILE)) {
            return "님과 매칭 성사";
        } else if (senderType.equals(SenderType.PROFILE) && receiverType.equals(ReceiverType.TEAM)) {
            return isSender ? "팀과 매칭 성사" : "님과 매칭 성사";
        } else if (senderType.equals(SenderType.TEAM) && receiverType.equals(ReceiverType.PROFILE)) {
            return isSender ? "님과 매칭 성사" : "팀과 매칭 성사";
        } else if (senderType.equals(SenderType.TEAM) && receiverType.equals(ReceiverType.TEAM)) {
            return "팀과 매칭 성사";
        }
        return "매칭 성사";
    }
}
