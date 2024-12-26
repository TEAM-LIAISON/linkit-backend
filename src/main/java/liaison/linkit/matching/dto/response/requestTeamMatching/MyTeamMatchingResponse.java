package liaison.linkit.matching.dto.response.requestTeamMatching;

import java.time.LocalDate;
import liaison.linkit.matching.domain.type.ReceiverType;
import liaison.linkit.matching.domain.type.SenderType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MyTeamMatchingResponse {

    // 팀 소개서에 보낸 매칭 요청 PK ID
    private final Long teamMatchingId;
    // 미니 프로필 이미지
    private final String miniProfileImg;
    // 수신자 이름
    private final String receiverName;
    // 매칭 요청 메시지
    private final String requestMessage;
    // 매칭 요청 발생 날짜
    private final LocalDate requestOccurTime;
    // 발신자가 누구인지
    private final SenderType senderType;
    // 매칭 요청 타입
    private final ReceiverType receiverType;
    // 어떤 이력/소개서에 매칭 요청을 보냈는지
    private final boolean isRequestTeamProfile;
}
