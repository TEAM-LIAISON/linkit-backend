package liaison.linkit.profile.domain.repository.antecedents;

import liaison.linkit.profile.domain.antecedents.Antecedents;

import java.util.List;
import java.util.Optional;

public interface AntecedentsRepositoryCustom {
    boolean existsByProfileId(final Long profileId);
    Optional<Antecedents> findByProfileId(final Long profileId);
    List<Antecedents> findAllByProfileId(final Long profileId);
    void deleteAllByProfileId(final Long profileId);
}
