package liaison.linkit.profile.domain.repository.teambuilding;

import liaison.linkit.profile.domain.teambuilding.TeamBuildingField;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamBuildingFieldRepository extends JpaRepository<TeamBuildingField, Long>, TeamBuildingFieldRepositoryCustom {

}
