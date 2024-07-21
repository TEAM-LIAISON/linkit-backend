package liaison.linkit.matching.dto.response.messageResponse;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class ReceivedPrivateMatchingMessageResponse {
    // 내 이력서 또는 팀 소개서에 매칭 요청 온 PK ID
    private final Long receivedMatchingId;
    // 발신자 이름
    private final String senderName;
    // 보낸 사람의 jobRoleName
    private final List<String> jobRoleNames;
    // 매칭 요청 메시지
    private final String requestMessage;
    // 어떤 이력/소개서에 매칭 요청이 왔는지
    private final boolean isReceivedTeamProfile;
}
