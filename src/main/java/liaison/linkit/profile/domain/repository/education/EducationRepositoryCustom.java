package liaison.linkit.profile.domain.repository.education;

import liaison.linkit.profile.domain.education.Education;

import java.util.List;

public interface EducationRepositoryCustom {
    boolean existsByProfileId(final Long profileId);
    Education findByProfileId(final Long profileId);
    List<Education> findAllByProfileId(final Long profileId);
    void deleteAllByProfileId(final Long profileId);
}
