package liaison.linkit.profile.domain.repository.jobRole;

import liaison.linkit.profile.domain.role.ProfileJobRole;

import java.util.List;

public interface ProfileJobRoleRepositoryCustom {
    boolean existsByProfileId(final Long profileId);
    void deleteAllByProfileId(final Long profileId);
    List<ProfileJobRole> findAllByProfileId(final Long profileId);
}
