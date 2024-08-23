package liaison.linkit.profile.domain.repository.profile;

import liaison.linkit.profile.domain.Profile;

import java.util.Optional;

public interface ProfileRepositoryCustom {

    Optional<Profile> findByMemberId(final Long memberId);

    boolean existsByMemberId(final Long memberId);

    void deleteByMemberId(final Long memberId);
}
