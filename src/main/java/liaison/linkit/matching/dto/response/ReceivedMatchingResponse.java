package liaison.linkit.matching.dto.response;

import liaison.linkit.matching.domain.PrivateMatching;
import liaison.linkit.matching.dto.response.toPrivateMatching.PrivateMatchingResponse;
import liaison.linkit.matching.dto.response.toTeamMatching.TeamMatchingResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ReceivedMatchingResponse {
    // 내 이력서에 온 매칭 요청
    private final List<PrivateMatchingResponse> privateMatchingResponseList;
    // 팀 소개서에 온 매칭 요청
    private final List<TeamMatchingResponse> teamMatchingResponseList;


    public ReceivedMatchingResponse() {
        this.privateMatchingResponseList = null;
        this.teamMatchingResponseList = null;
    }


}
