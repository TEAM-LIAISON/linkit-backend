package liaison.linkit.profile.implement.education;

import java.util.List;
import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.profile.domain.ProfileEducation;
import liaison.linkit.profile.domain.repository.education.ProfileEducationRepository;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class ProfileEducationQueryAdapter {

    private final ProfileEducationRepository profileEducationRepository;

    public List<ProfileEducation> getProfileEducations(final Long memberId) {
        return profileEducationRepository.getProfileEducations(memberId);
    }
}
