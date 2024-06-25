package liaison.linkit.team.domain.repository.memberIntroduction;

import liaison.linkit.team.domain.memberIntroduction.TeamMemberIntroduction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface TeamMemberIntroductionRepository extends JpaRepository<TeamMemberIntroduction, Long> {

    boolean existsByTeamProfileId(final Long teamProfileId);

    @Modifying
    @Transactional
    @Query("DELETE FROM TeamMemberIntroduction teamMemberIntroduction WHERE teamMemberIntroduction.teamProfile.id = :teamProfileId")
    void deleteAllByTeamProfileId(@Param("teamProfileId") final Long teamProfileId);
}
