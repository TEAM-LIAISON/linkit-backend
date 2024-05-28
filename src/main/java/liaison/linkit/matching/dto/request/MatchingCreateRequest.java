package liaison.linkit.matching.dto.request;

import lombok.Getter;

@Getter
public class MatchingCreateRequest {
    private Long receiveMatchingId;
    private String requestMessage;
}
