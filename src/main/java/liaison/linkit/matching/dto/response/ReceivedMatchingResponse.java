package liaison.linkit.matching.dto.response;

import liaison.linkit.matching.dto.response.toPrivateMatching.ToPrivateMatchingResponse;
import liaison.linkit.matching.dto.response.toTeamMatching.ToTeamMatchingResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ReceivedMatchingResponse {
    // 내 이력서에 온 매칭 요청
    private final List<ToPrivateMatchingResponse> toPrivateMatchingResponseList;
    // 팀 소개서에 온 매칭 요청
    private final List<ToTeamMatchingResponse> toTeamMatchingResponseList;


    public ReceivedMatchingResponse() {
        this.toPrivateMatchingResponseList = null;
        this.toTeamMatchingResponseList = null;
    }


}
