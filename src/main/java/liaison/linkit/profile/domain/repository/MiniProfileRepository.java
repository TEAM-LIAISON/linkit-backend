package liaison.linkit.profile.domain.repository;

import liaison.linkit.profile.domain.miniProfile.MiniProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface MiniProfileRepository extends JpaRepository<MiniProfile, Long> {
    boolean existsByProfileId(final Long profileId);

    Optional<MiniProfile> findByProfileId(@Param("profileId") final Long profileId);

    @Modifying
    @Transactional // 메서드가 트랜잭션 내에서 실행되어야 함을 나타낸다.
    @Query("DELETE FROM MiniProfile miniProfile WHERE miniProfile.profile.id = :profileId")
    void deleteByProfileId(@Param("profileId") final Long profileId);
}
