package liaison.linkit.matching.dto.response.messageResponse;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RequestTeamMatchingMessageResponse {
    // 팀 소개서에 보낸 매칭 요청 PK ID
    private final Long teamMatchingId;
    // 수신자 이름
    private final String receiverName;

    // 어떤 직무에 요청을 보냈는지
    private final String jobRoleName;
    // 매칭 요청 메시지
    private final String requestMessage;

    // 어떤 이력/소개서에 매칭 요청을 보냈는지
    private final boolean isRequestTeamProfile;
}
