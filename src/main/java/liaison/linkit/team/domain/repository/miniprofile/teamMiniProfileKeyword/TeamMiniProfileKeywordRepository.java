package liaison.linkit.team.domain.repository.miniprofile.teamMiniProfileKeyword;

import liaison.linkit.team.domain.miniprofile.TeamMiniProfileKeyword;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamMiniProfileKeywordRepository extends JpaRepository<TeamMiniProfileKeyword, Long>, TeamMiniProfileKeywordRepositoryCustom {

}
