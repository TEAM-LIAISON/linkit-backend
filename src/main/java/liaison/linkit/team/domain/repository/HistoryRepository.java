package liaison.linkit.team.domain.repository;

import liaison.linkit.team.domain.History;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface HistoryRepository extends JpaRepository<History, Long> {

    boolean existsByTeamProfileId(final Long teamProfileId);

    @Query("SELECT history FROM History history WHERE history.teamProfile.id = :teamProfileId")
    Optional<History> findByTeamProfileId(@Param("teamProfileId") final Long teamProfileId);

    @Query("SELECT history FROM History history WHERE history.teamProfile.id = :teamProfileId")
    List<History> findAllByTeamProfileId(final Long teamProfileId);

    @Modifying
    @Transactional
    @Query("DELETE FROM History history WHERE history.teamProfile.id = :teamProfileId")
    void deleteAllByTeamProfileId(@Param("teamProfileId") final Long teamProfileId);
}
