package liaison.linkit.matching.dto.response;

import liaison.linkit.matching.dto.response.requestPrivateMatching.MyPrivateMatchingResponse;
import liaison.linkit.matching.dto.response.requestTeamMatching.MyTeamMatchingResponse;
import liaison.linkit.matching.dto.response.toPrivateMatching.ToPrivateMatchingResponse;
import liaison.linkit.matching.dto.response.toTeamMatching.ToTeamMatchingResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class SuccessMatchingResponse {
    private final List<ToPrivateMatchingResponse> toPrivateMatchingResponseList;
    private final List<ToTeamMatchingResponse> toTeamMatchingResponseList;
    private final List<MyPrivateMatchingResponse> myPrivateMatchingResponseList;
    private final List<MyTeamMatchingResponse> myTeamMatchingResponseList;
}
