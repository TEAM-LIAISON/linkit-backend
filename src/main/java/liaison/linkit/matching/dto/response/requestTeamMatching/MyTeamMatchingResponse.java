package liaison.linkit.matching.dto.response.requestTeamMatching;

import liaison.linkit.matching.domain.TeamMatching;
import liaison.linkit.matching.domain.type.MatchingType;
import liaison.linkit.matching.domain.type.SenderType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

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
    private final MatchingType matchingType;
    // 어떤 이력/소개서에 매칭 요청을 보냈는지
    private final boolean isRequestTeamProfile;

    public static List<MyTeamMatchingResponse> myTeamMatchingResponses(final List<TeamMatching> teamMatchingList) {
        return teamMatchingList.stream()
                .map(teamMatching -> new MyTeamMatchingResponse(
                        teamMatching.getId(),
                        teamMatching.getTeamMemberAnnouncement().getTeamProfile().getTeamMiniProfile().getTeamLogoImageUrl(),
                        teamMatching.getTeamMemberAnnouncement().getTeamProfile().getTeamMiniProfile().getTeamName(),
                        teamMatching.getRequestMessage(),
                        LocalDate.from(teamMatching.getCreatedAt()),
                        teamMatching.getSenderType(),
                        teamMatching.getMatchingType(),
                        true
                )).collect(Collectors.toList());
    }
}
