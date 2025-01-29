package liaison.linkit.profile.domain.repository.education;

import java.util.List;
import liaison.linkit.common.domain.University;
import liaison.linkit.profile.domain.education.ProfileEducation;
import liaison.linkit.profile.presentation.education.dto.ProfileEducationRequestDTO.UpdateProfileEducationRequest;

public interface ProfileEducationCustomRepository {
    List<ProfileEducation> getProfileEducations(final Long profileId);

    ProfileEducation updateProfileEducation(final Long profileEducationId, final University university, final UpdateProfileEducationRequest updateProfileEducationRequest);

    boolean existsByProfileId(final Long profileId);

    void removeProfileEducationsByProfileId(final Long profileId);
}
