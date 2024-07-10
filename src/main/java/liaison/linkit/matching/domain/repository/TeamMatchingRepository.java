package liaison.linkit.matching.domain.repository;

import liaison.linkit.matching.domain.TeamMatching;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TeamMatchingRepository extends JpaRepository<TeamMatching, Long> {

    @Query("SELECT tm FROM TeamMatching tm WHERE tm.teamProfile.id = :teamProfileId")
    List<TeamMatching> findByTeamProfileId(final Long teamProfileId);
}
