package liaison.linkit.profile.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MiniProfileCreateRequest {
    private final String oneLineIntroduction;
    private final String interests;
    private final String firstFreeText;
    private final String secondFreeText;
}
