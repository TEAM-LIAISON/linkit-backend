package liaison.linkit.team.domain.repository.miniprofile;

import liaison.linkit.team.domain.miniprofile.TeamMiniProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface TeamMiniProfileRepository extends JpaRepository<TeamMiniProfile, Long> {

    boolean existsByTeamProfileId(final Long teamProfileId);

    Optional<TeamMiniProfile> findByTeamProfileId(@Param("teamProfileId") final Long teamProfileId);

    void deleteByTeamProfileId(@Param("teamProfileId") final Long teamProfileId);
}
