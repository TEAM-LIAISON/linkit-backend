package liaison.linkit.profile.domain.repository.teambuilding;

import liaison.linkit.profile.domain.teambuilding.ProfileTeamBuildingField;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ProfileTeamBuildingFieldRepository extends JpaRepository<ProfileTeamBuildingField, Long>, ProfileTeamBuildingFieldRepositoryCustom {

}
