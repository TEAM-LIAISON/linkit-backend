package liaison.linkit.matching.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MatchingCreateRequest {

    @NotNull(message = "매칭 요청과 함께 보낼 메시지를 작성해주세요")
    private String requestMessage;

    public MatchingCreateRequest(
            final String requestMessage
    ) {
        this.requestMessage = requestMessage;
    }
}
