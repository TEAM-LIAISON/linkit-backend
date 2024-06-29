package liaison.linkit.profile.dto.request.onBoarding.personal;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class OnBoardingPersonalJobAndSkillCreateRequest {
    private final List<String> jobRoleNames;
    private final List<String> skillNames;
}
