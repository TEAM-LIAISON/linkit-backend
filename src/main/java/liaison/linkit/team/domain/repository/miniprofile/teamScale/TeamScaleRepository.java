package liaison.linkit.team.domain.repository.miniprofile.teamScale;

import liaison.linkit.team.domain.miniprofile.TeamScale;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamScaleRepository extends JpaRepository<TeamScale, Long>, TeamScaleRepositoryCustom {

}
