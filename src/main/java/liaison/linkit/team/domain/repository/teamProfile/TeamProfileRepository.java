package liaison.linkit.team.domain.repository.teamProfile;

import liaison.linkit.team.domain.TeamProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamProfileRepository extends JpaRepository<TeamProfile, Long>, TeamProfileRepositoryCustom {

}
