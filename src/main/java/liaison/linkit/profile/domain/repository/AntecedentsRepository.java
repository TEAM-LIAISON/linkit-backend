package liaison.linkit.profile.domain.repository;

import liaison.linkit.profile.domain.Antecedents;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface AntecedentsRepository extends JpaRepository<Antecedents, Long> {

    boolean existsByProfileId(final Long profileId);

    Antecedents findByProfileId(@Param("profileId") final Long profileId);

    @Query("SELECT antecedents FROM Antecedents antecedents WHERE antecedents.profile.id = :profileId")
    List<Antecedents> findAllByProfileId(final Long profileId);
}
