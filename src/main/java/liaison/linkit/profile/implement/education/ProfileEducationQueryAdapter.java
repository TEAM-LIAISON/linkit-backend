package liaison.linkit.profile.implement.education;

import java.util.List;
import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.profile.domain.education.ProfileEducation;
import liaison.linkit.profile.domain.repository.education.ProfileEducationRepository;
import liaison.linkit.profile.exception.education.ProfileEducationNotFoundException;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class ProfileEducationQueryAdapter {

    final ProfileEducationRepository profileEducationRepository;

    public List<ProfileEducation> getProfileEducations(final Long profileId) {
        return profileEducationRepository.getProfileEducations(profileId);
    }

    public ProfileEducation getProfileEducation(final Long profileEducationId) {
        return profileEducationRepository.findById(profileEducationId)
                .orElseThrow(() -> ProfileEducationNotFoundException.EXCEPTION);
    }

    public boolean existsByProfileId(final Long profileId) {
        return profileEducationRepository.existsByProfileId(profileId);
    }
}
