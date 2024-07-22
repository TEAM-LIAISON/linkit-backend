package liaison.linkit.profile.domain.repository;

import liaison.linkit.profile.domain.awards.Awards;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface AwardsRepository extends JpaRepository<Awards, Long> {

    boolean existsByProfileId(@Param("profileId") final Long profileId);

    @Query("SELECT awards FROM Awards awards WHERE awards.profile.id = :profileId")
    List<Awards> findAllByProfileId(@Param("profileId") final Long profileId);

    Awards findByProfileId(@Param("profileId") final Long profileId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Awards awards WHERE awards.profile.id = :profileId")
    void deleteAllByProfileId(@Param("profileId") final Long profileId);

}
