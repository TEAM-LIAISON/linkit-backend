package liaison.linkit.profile.dto.request.miniProfile;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class MiniProfileCreateRequest {

    private final String profileTitle;
    private final LocalDate uploadPeriod;
    private final boolean uploadDeadline;
    private final String miniProfileImg;
    private final String myValue;
    private final String skillSets;


}
