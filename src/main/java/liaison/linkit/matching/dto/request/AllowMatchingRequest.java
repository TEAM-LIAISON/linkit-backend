package liaison.linkit.matching.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AllowMatchingRequest {
    private Boolean isAllowMatching;

    public AllowMatchingRequest(
            final Boolean isAllowMatching
    ) {
        this.isAllowMatching = isAllowMatching;
    }
}
