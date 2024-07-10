package liaison.linkit.matching.domain.repository;

import liaison.linkit.matching.domain.PrivateMatching;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PrivateMatchingRepository extends JpaRepository<PrivateMatching, Long> {

    @Query("SELECT pm FROM PrivateMatching pm WHERE pm.profile.id = :profileId")
    List<PrivateMatching> findByProfileId(final Long profileId);

}
