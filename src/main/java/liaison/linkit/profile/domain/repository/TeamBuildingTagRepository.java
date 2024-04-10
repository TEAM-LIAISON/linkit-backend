package liaison.linkit.profile.domain.repository;

import liaison.linkit.profile.domain.TeamBuildingField;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamBuildingTagRepository extends JpaRepository<TeamBuildingField, Long> {

}
