package liaison.linkit.matching.dto.response.toTeamMatching;

import liaison.linkit.matching.domain.TeamMatching;
import liaison.linkit.matching.domain.type.MatchingType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public class ToTeamMatchingResponse {
    // 발신자 이름
    private final String senderName;
    // 매칭 요청 메시지
    private final String requestMessage;
    // 매칭 요청 발생 날짜
    private final LocalDate requestOccurTime;
    // 매칭 요청 타입
    private final MatchingType matchingType;

    public static List<ToTeamMatchingResponse> toTeamMatchingResponse(final List<TeamMatching> teamMatchingList) {
        return teamMatchingList.stream()
                .map(teamMatching -> new ToTeamMatchingResponse(
                        teamMatching.getMember().getMemberBasicInform().getMemberName(),
                        teamMatching.getRequestMessage(),
                        LocalDate.from(teamMatching.getCreatedAt()),
                        teamMatching.getMatchingType()
                )).collect(Collectors.toList());
    }
}
