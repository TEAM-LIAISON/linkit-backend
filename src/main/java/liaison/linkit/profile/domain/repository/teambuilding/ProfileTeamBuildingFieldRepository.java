package liaison.linkit.profile.domain.repository.teambuilding;

import liaison.linkit.profile.domain.teambuilding.ProfileTeamBuildingField;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface ProfileTeamBuildingFieldRepository extends JpaRepository<ProfileTeamBuildingField, Long> {

    boolean existsByProfileId(final Long profileId);

     @Query("SELECT profileTeamBuilding FROM ProfileTeamBuildingField profileTeamBuilding WHERE profileTeamBuilding.profile.id = :profileId")
    List<ProfileTeamBuildingField> findAllByProfileId(@Param("profileId") final Long profileId);
}
