package liaison.linkit.profile.domain.repository;

import liaison.linkit.profile.domain.antecedents.Antecedents;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


public interface AntecedentsRepository extends JpaRepository<Antecedents, Long> {

    boolean existsByProfileId(final Long profileId);

    Optional<Antecedents> findByProfileId(@Param("profileId") final Long profileId);

    @Query("SELECT antecedents FROM Antecedents antecedents WHERE antecedents.profile.id = :profileId")
    List<Antecedents> findAllByProfileId(final Long profileId);

    @Modifying
    @Transactional
    // 메서드가 트랜잭션 내에서 실행되어야 함을 나타낸다.
    @Query("DELETE FROM Antecedents antecedents WHERE antecedents.profile.id = :profileId")
    void deleteAllByProfileId(@Param("profileId") final Long profileId);
}
