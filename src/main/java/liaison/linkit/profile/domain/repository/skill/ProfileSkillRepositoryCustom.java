package liaison.linkit.profile.domain.repository.skill;

import java.util.List;
import java.util.Optional;

public interface ProfileSkillRepositoryCustom {
    boolean existsByProfileId(final Long profileId);

    List<ProfileSkill> findAllByProfileId(final Long profileId);

    Optional<ProfileSkill> findByProfileId(final Long profileId);

    void deleteAllByProfileId(final Long profileId);
}
