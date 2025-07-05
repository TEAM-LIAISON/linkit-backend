package liaison.linkit.profile.implement.education;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.common.domain.University;
import liaison.linkit.profile.domain.education.ProfileEducation;
import liaison.linkit.profile.domain.repository.education.ProfileEducationRepository;
import liaison.linkit.profile.presentation.education.dto.ProfileEducationRequestDTO.UpdateProfileEducationRequest;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class ProfileEducationCommandAdapter {
    final ProfileEducationRepository profileEducationRepository;

    public ProfileEducation addProfileEducation(final ProfileEducation profileEducation) {
        return profileEducationRepository.save(profileEducation);
    }

    public void removeProfileEducation(final ProfileEducation profileEducation) {
        profileEducationRepository.delete(profileEducation);
    }

    public ProfileEducation updateProfileEducation(
            final Long profileEducationId,
            final University university,
            final UpdateProfileEducationRequest updateProfileEducationRequest) {
        return profileEducationRepository.updateProfileEducation(
                profileEducationId, university, updateProfileEducationRequest);
    }

    public void removeProfileEducationsByProfileId(final Long profileId) {
        profileEducationRepository.removeProfileEducationsByProfileId(profileId);
    }
}
