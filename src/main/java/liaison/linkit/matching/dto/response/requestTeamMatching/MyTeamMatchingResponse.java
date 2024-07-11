package liaison.linkit.matching.dto.response.requestTeamMatching;

import liaison.linkit.matching.domain.TeamMatching;
import liaison.linkit.matching.domain.type.MatchingType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public class MyTeamMatchingResponse {
    // 수신자 이름
    private final String receiverName;
    // 매칭 요청 메시지
    private final String requestMessage;
    // 매칭 요청 발생 날짜
    private final LocalDate requestOccurTime;
    // 매칭 요청 타입
    private final MatchingType matchingType;

    public static List<MyTeamMatchingResponse> myTeamMatchingResponses(final List<TeamMatching> teamMatchingList) {
        return teamMatchingList.stream()
                .map(teamMatching -> new MyTeamMatchingResponse(
                        teamMatching.getTeamProfile().getMember().getMemberBasicInform().getMemberName(),
                        teamMatching.getRequestMessage(),
                        LocalDate.from(teamMatching.getCreatedAt()),
                        teamMatching.getMatchingType()
                )).collect(Collectors.toList());
    }
}
