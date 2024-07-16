package liaison.linkit.team.domain.repository.teambuilding;

import liaison.linkit.team.domain.teambuilding.TeamProfileTeamBuildingField;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface TeamProfileTeamBuildingFieldRepository extends JpaRepository<TeamProfileTeamBuildingField, Long> {

    boolean existsByTeamProfileId(final Long teamProfileId);

    @Query("SELECT teamProfileTeamBuildingField FROM TeamProfileTeamBuildingField teamProfileTeamBuildingField WHERE teamProfileTeamBuildingField.teamProfile.id = :teamProfileId")
    List<TeamProfileTeamBuildingField> findAllByTeamProfileId(final Long teamProfileId);

    @Modifying
    @Transactional  // 메서드가 트랜잭션 내에서 실행되어야 함을 나타낸다.
    @Query("DELETE FROM TeamProfileTeamBuildingField teamProfileTeamBuildingField WHERE teamProfileTeamBuildingField.teamProfile.id = :teamProfileId")
    void deleteAllByTeamProfileId(@Param("teamProfileId") final Long teamProfileId);
}
