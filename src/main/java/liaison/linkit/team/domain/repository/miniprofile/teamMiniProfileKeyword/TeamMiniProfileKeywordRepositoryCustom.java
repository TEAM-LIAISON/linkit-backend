package liaison.linkit.team.domain.repository.miniprofile.teamMiniProfileKeyword;

import liaison.linkit.team.domain.miniprofile.TeamMiniProfileKeyword;

import java.util.List;

public interface TeamMiniProfileKeywordRepositoryCustom {
    List<TeamMiniProfileKeyword> findAllByTeamMiniProfileId(final Long teamMiniProfileId);
    void deleteAllByTeamMiniProfileId(final Long teamMiniProfileId);
}
