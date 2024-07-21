package liaison.linkit.matching.dto.response;

import liaison.linkit.matching.domain.type.MatchingType;
import liaison.linkit.matching.domain.type.SenderType;
import liaison.linkit.matching.dto.response.requestPrivateMatching.MyPrivateMatchingResponse;
import liaison.linkit.matching.dto.response.requestTeamMatching.MyTeamMatchingResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public class RequestMatchingResponse {

    // 내 이력서 또는 팀 소개서에 매칭 요청 온 PK ID
    private final Long requestMatchingId;
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
    private final MatchingType matchingType;
    // 어떤 이력/소개서에 매칭 요청을 보냈는지
    private final boolean isRequestTeamProfile;

    public static List<RequestMatchingResponse> requestMatchingResponseList(
            final List<MyPrivateMatchingResponse> myPrivateMatchingResponseList,
            final List<MyTeamMatchingResponse> myTeamMatchingResponseList
    ) {
        // 스트림 API를 사용하여 두 리스트의 요소를 하나의 스트림으로 결합
        return Stream.concat(
                myPrivateMatchingResponseList.stream().map(mpm -> new RequestMatchingResponse(
                        mpm.getPrivateMatchingId(),
                        mpm.getMiniProfileImg(),
                        mpm.getReceiverName(),
                        mpm.getRequestMessage(),
                        mpm.getRequestOccurTime(),
                        mpm.getSenderType(),
                        mpm.getMatchingType(),
                        mpm.isRequestTeamProfile())),
                myTeamMatchingResponseList.stream().map(mtm -> new RequestMatchingResponse(
                        mtm.getTeamMatchingId(),
                        mtm.getMiniProfileImg(),
                        mtm.getReceiverName(),
                        mtm.getRequestMessage(),
                        mtm.getRequestOccurTime(),
                        mtm.getSenderType(),
                        mtm.getMatchingType(),
                        mtm.isRequestTeamProfile()))
        ).collect(Collectors.toList());
    }
}
