package liaison.linkit.team.domain.repository.attach.teamAttachUrl;

import liaison.linkit.team.domain.attach.TeamAttachUrl;

import java.util.List;

public interface TeamAttachUrlRepositoryCustom {
    boolean existsByTeamProfileId(final Long teamProfileId);
    void deleteAllByTeamProfileId(final Long teamProfileId);
    List<TeamAttachUrl> findAllByTeamProfileId(final Long teamProfileId);
}
