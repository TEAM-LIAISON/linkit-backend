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
    // 팀 소개서에 온 매칭 요청 PK ID
    private final Long teamMatchingId;
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

    public static List<ToTeamMatchingResponse> toTeamMatchingResponse(final List<TeamMatching> teamMatchingList) {
        return teamMatchingList.stream()
                .map(teamMatching -> new ToTeamMatchingResponse(
                        teamMatching.getId(),
                        teamMatching.getMember().getMemberBasicInform().getMemberName(),
                        teamMatching.getRequestMessage(),
                        LocalDate.from(teamMatching.getCreatedAt()),
                        teamMatching.getMatchingType(),
                        true
                )).collect(Collectors.toList());
    }
}
