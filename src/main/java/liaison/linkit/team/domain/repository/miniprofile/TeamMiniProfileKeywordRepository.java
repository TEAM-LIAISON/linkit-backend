package liaison.linkit.team.domain.repository.miniprofile;

import liaison.linkit.team.domain.miniprofile.TeamMiniProfileKeyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface TeamMiniProfileKeywordRepository extends JpaRepository<TeamMiniProfileKeyword, Long> {

    @Query("SELECT teamMiniProfileKeyword FROM TeamMiniProfileKeyword teamMiniProfileKeyword WHERE teamMiniProfileKeyword.teamMiniProfile.id = :teamMiniProfileId")
    List<TeamMiniProfileKeyword> findAllByTeamMiniProfileId(@Param("teamMiniProfileId") final Long teamMiniProfileId);

    @Modifying
    @Transactional // 메서드가 트랜잭션 내에서 실행되어야 함을 나타낸다.
    @Query("DELETE FROM TeamMiniProfileKeyword tmk WHERE tmk.teamMiniProfile.id = :teamMiniProfileId")
    void deleteAllByTeamMiniProfileId(@Param("teamMiniProfileId") final Long teamMiniProfileId);
}
