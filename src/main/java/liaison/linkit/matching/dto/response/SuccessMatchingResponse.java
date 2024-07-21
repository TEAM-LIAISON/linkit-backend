package liaison.linkit.matching.dto.response;

import liaison.linkit.matching.domain.type.MatchingType;
import liaison.linkit.matching.dto.response.requestPrivateMatching.MyPrivateMatchingResponse;
import liaison.linkit.matching.dto.response.requestTeamMatching.MyTeamMatchingResponse;
import liaison.linkit.matching.dto.response.toPrivateMatching.ToPrivateMatchingResponse;
import liaison.linkit.matching.dto.response.toTeamMatching.ToTeamMatchingResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Getter
@AllArgsConstructor
public class SuccessMatchingResponse {

    // private / team matching pk id
    private final Long matchingId;
    // 성사된 사람의 개인 이름, 또는 팀명
    private final String successMatchingMemberName;
    // 매칭 요청 메시지
    private final String requestMessage;
    // 매칭 요청 발생 날짜
    private final LocalDate requestOccurTime;
    // 매칭 요청 타입
    private final MatchingType matchingType;

    public static List<SuccessMatchingResponse> successMatchingResponseList(
            final List<ToPrivateMatchingResponse> toPrivateMatchingResponseList,
            final List<ToTeamMatchingResponse> toTeamMatchingResponseList,
            final List<MyPrivateMatchingResponse> myPrivateMatchingResponseList,
            final List<MyTeamMatchingResponse> myTeamMatchingResponseList
    ) {
        // 네 리스트의 스트림을 하나로 결합
        Stream<SuccessMatchingResponse> combinedStream = Stream.concat(
                Stream.concat(
                        toPrivateMatchingResponseList.stream().map(pmr -> new SuccessMatchingResponse(
                                pmr.getPrivateMatchingId(),
                                pmr.getSenderName(),
                                pmr.getRequestMessage(),
                                pmr.getRequestOccurTime(),
                                pmr.getMatchingType())),
                        toTeamMatchingResponseList.stream().map(tmr -> new SuccessMatchingResponse(
                                tmr.getTeamMatchingId(),
                                tmr.getSenderName(),
                                tmr.getRequestMessage(),
                                tmr.getRequestOccurTime(),
                                tmr.getMatchingType()))
                ),
                Stream.concat(
                        myPrivateMatchingResponseList.stream().map(mpm -> new SuccessMatchingResponse(
                                mpm.getPrivateMatchingId(),
                                mpm.getReceiverName(),
                                mpm.getRequestMessage(),
                                mpm.getRequestOccurTime(),
                                mpm.getMatchingType())),
                        myTeamMatchingResponseList.stream().map(mtm -> new SuccessMatchingResponse(
                                mtm.getTeamMatchingId(),
                                mtm.getReceiverName(),
                                mtm.getRequestMessage(),
                                mtm.getRequestOccurTime(),
                                mtm.getMatchingType()))
                )
        );

        return combinedStream.collect(Collectors.toList());
    }
}
