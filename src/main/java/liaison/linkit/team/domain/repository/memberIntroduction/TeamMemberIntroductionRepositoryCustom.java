package liaison.linkit.team.domain.repository.memberIntroduction;

import liaison.linkit.team.domain.memberIntroduction.TeamMemberIntroduction;

import java.util.List;

public interface TeamMemberIntroductionRepositoryCustom {
    boolean existsByTeamProfileId(final Long teamProfileId);
    void deleteAllByTeamProfileId(final Long teamProfileId);
    List<TeamMemberIntroduction> findAllByTeamProfileId(final Long teamProfileId);
}
