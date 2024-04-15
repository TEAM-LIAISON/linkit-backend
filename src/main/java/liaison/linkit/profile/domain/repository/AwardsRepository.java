package liaison.linkit.profile.domain.repository;

import liaison.linkit.profile.domain.Awards;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AwardsRepository extends JpaRepository<Awards, Long> {

    boolean existsByProfileId(Long profileId);

    @Query("SELECT awards FROM Awards awards WHERE awards.profile.id = :profileId")
    List<Awards> findAllByProfileId(@Param("profileId") final Long profileId);

    Awards findByProfileId(@Param("profileId") final Long profileId);

}
