package liaison.linkit.matching.dto.response;

import liaison.linkit.matching.dto.response.requestPrivateMatching.MyPrivateMatchingResponse;
import liaison.linkit.matching.dto.response.requestTeamMatching.MyTeamMatchingResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class RequestMatchingResponse {
    private final List<MyPrivateMatchingResponse> myPrivateMatchingResponseList;
    private final List<MyTeamMatchingResponse> myTeamMatchingResponseList;

    public RequestMatchingResponse() {
        this.myPrivateMatchingResponseList = null;
        this.myTeamMatchingResponseList = null;
    }
}
