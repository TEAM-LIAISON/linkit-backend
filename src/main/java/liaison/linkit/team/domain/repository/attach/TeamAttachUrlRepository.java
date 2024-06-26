package liaison.linkit.team.domain.repository.attach;

import liaison.linkit.team.domain.attach.TeamAttachUrl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface TeamAttachUrlRepository extends JpaRepository<TeamAttachUrl, Long> {
    boolean existsByTeamProfileId(final Long teamProfileId);

    @Modifying
    @Transactional
    @Query("DELETE FROM TeamAttachUrl teamAttachUrl WHERE teamAttachUrl.teamProfile.id = :teamProfileId")
    void deleteAllByTeamProfileId(@Param("teamProfileId") final Long teamProfileId);

    @Query("SELECT teamAttachUrl FROM TeamAttachUrl teamAttachUrl WHERE teamAttachUrl.teamProfile.id = :teamProfileId")
    List<TeamAttachUrl> findAllByTeamProfileId(@Param("teamProfileId") final Long teamProfileId);

}
