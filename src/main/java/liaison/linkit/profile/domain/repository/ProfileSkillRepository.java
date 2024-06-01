package liaison.linkit.profile.domain.repository;

import liaison.linkit.profile.domain.skill.ProfileSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ProfileSkillRepository extends JpaRepository<ProfileSkill, Long> {
    boolean existsByProfileId(final Long profileId);

    @Query("""
           SELECT ps
           FROM ProfileSkill ps
           WHERE ps.profile.id = :profileId
           """)
    List<ProfileSkill> findAllByProfileId(@Param("profileId") final Long profileId);

    ProfileSkill findByProfileId(@Param("profileId") Long profileId);

    @Modifying
    @Transactional  // 메서드가 트랜잭션 내에서 실행되어야 함을 나타낸다.
    @Query("DELETE FROM ProfileSkill profileSkill WHERE profileSkill.profile.id = :profileId")
    void deleteAllByProfileId(@Param("profileId") final Long profileId);
}
