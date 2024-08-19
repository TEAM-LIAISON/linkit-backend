package liaison.linkit.team.domain.repository.teamProfile;

import liaison.linkit.team.domain.TeamProfile;

import java.util.Optional;

public interface TeamProfileRepositoryCustom {
    Optional<TeamProfile> findByMemberId(final Long memberId);
    boolean existsByMemberId(final Long memberId);
    void deleteByMemberId(final Long memberId);
}
