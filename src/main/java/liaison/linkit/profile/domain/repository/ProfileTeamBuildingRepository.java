package liaison.linkit.profile.domain.repository;

import liaison.linkit.profile.domain.teambuilding.ProfileTeamBuildingField;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface ProfileTeamBuildingRepository extends JpaRepository<ProfileTeamBuildingField, Long> {

    boolean existsByProfileId(final Long profileId);

     ProfileTeamBuildingField findByProfileId(@Param("profileId") final Long profileId);

     @Query("SELECT ProfileTeamBuilding FROM ProfileTeamBuildingField profileTeamBuilding WHERE profileTeamBuilding.profile.id = :profileId")
    List<ProfileTeamBuildingField> findAllByProfileId(@Param("profileId") final Long profileId);
}
