package liaison.linkit.profile.domain.repository.education;

import liaison.linkit.profile.domain.ProfileEducation;

import java.util.List;

public interface EducationRepositoryCustom {
    boolean existsByProfileId(final Long profileId);

    ProfileEducation findByProfileId(final Long profileId);

    List<ProfileEducation> findAllByProfileId(final Long profileId);

    void deleteAllByProfileId(final Long profileId);
}
