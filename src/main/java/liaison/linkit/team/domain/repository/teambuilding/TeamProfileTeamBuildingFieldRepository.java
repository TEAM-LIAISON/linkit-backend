package liaison.linkit.team.domain.repository.teambuilding;

import liaison.linkit.team.domain.teambuilding.TeamProfileTeamBuildingField;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TeamProfileTeamBuildingFieldRepository extends JpaRepository<TeamProfileTeamBuildingField, Long> {

    boolean existsByTeamProfileId(final Long teamProfileId);

    @Query("SELECT teamProfileTeamBuildingField FROM TeamProfileTeamBuildingField teamProfileTeamBuildingField WHERE teamProfileTeamBuildingField.teamProfile.id = :teamProfileId")
    List<TeamProfileTeamBuildingField> findAllByTeamProfileId(final Long teamProfileId);
}
