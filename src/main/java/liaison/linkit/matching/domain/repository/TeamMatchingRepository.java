package liaison.linkit.matching.domain.repository;

import liaison.linkit.matching.domain.TeamMatching;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TeamMatchingRepository extends JpaRepository<TeamMatching, Long> {

    @Query("SELECT tm FROM TeamMatching tm WHERE tm.teamProfile.id = :teamProfileId")
    List<TeamMatching> findByTeamProfileId(@Param("teamProfileId") final Long teamProfileId);

    @Query("SELECT tm FROM TeamMatching tm WHERE tm.member.id = :memberId")
    List<TeamMatching> findByMemberId(@Param("memberId") final Long memberId);

    @Query("SELECT tm FROM TeamMatching tm WHERE tm.matchingStatus = 'SUCCESSFUL' AND tm.teamProfile.id = :teamProfileId")
    List<TeamMatching> findSuccessReceivedMatching(@Param("teamProfileId") final Long teamProfileId);

    @Query("SELECT tm FROM TeamMatching tm WHERE tm.matchingStatus = 'SUCCESSFUL' AND tm.member.id = :memberId")
    List<TeamMatching> findSuccessRequestMatching(@Param("memberId")final Long memberId);
}
