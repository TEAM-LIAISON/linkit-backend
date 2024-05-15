package liaison.linkit.profile.dto.request.miniProfile;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MiniProfileUpdateRequest {
    private final String oneLineIntroduction;
    private final String interests;
    private final String firstFreeText;
    private final String secondFreeText;
}
