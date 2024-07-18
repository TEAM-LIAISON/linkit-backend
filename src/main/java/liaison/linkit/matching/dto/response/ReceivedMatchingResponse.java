package liaison.linkit.matching.dto.response;

import liaison.linkit.matching.domain.type.MatchingType;
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
public class ReceivedMatchingResponse {

    // 내 이력서 또는 팀 소개서에 매칭 요청 온 PK ID
    private final Long receivedMatchingId;
    // 발신자 이름
    private final String senderName;
    // 매칭 요청 메시지
    private final String requestMessage;
    // 매칭 요청 발생 날짜
    private final LocalDate requestOccurTime;
    // 매칭 요청 타입
    private final MatchingType matchingType;
    // 어떤 이력/소개서에 매칭 요청이 왔는지
    private final boolean isReceivedTeamProfile;

    public static List<ReceivedMatchingResponse> toReceivedMatchingResponse(
            final List<ToPrivateMatchingResponse> toPrivateMatchingResponseList,
            final List<ToTeamMatchingResponse> toTeamMatchingResponseList
    ) {
        // 스트림 API를 사용하여 두 리스트의 요소를 하나의 스트림으로 결합
        return Stream.concat(
                toPrivateMatchingResponseList.stream().map(pmr -> new ReceivedMatchingResponse(
                        pmr.getPrivateMatchingId(),
                        pmr.getSenderName(),
                        pmr.getRequestMessage(),
                        pmr.getRequestOccurTime(),
                        pmr.getMatchingType(),
                        pmr.isReceivedTeamProfile())),
                toTeamMatchingResponseList.stream().map(tmr -> new ReceivedMatchingResponse(
                        tmr.getTeamMatchingId(),
                        tmr.getSenderName(),
                        tmr.getRequestMessage(),
                        tmr.getRequestOccurTime(),
                        tmr.getMatchingType(),
                        tmr.isReceivedTeamProfile()))
        ).collect(Collectors.toList());
    }
}
