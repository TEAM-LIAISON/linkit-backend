package liaison.linkit.profile.domain.repository.education;

import java.util.List;
import liaison.linkit.profile.domain.ProfileEducation;

public interface ProfileEducationCustomRepository {
    List<ProfileEducation> getProfileEducations(final Long memberId);
}
