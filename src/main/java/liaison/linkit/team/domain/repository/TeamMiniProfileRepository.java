package liaison.linkit.team.domain.repository;

import liaison.linkit.team.domain.miniprofile.TeamMiniProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;


public interface TeamMiniProfileRepository extends JpaRepository<TeamMiniProfile, Long> {

    boolean existsByTeamProfileId(final Long teamProfileId);

    TeamMiniProfile findByTeamProfileId(@Param("teamProfileId") final Long teamProfileId);
}
