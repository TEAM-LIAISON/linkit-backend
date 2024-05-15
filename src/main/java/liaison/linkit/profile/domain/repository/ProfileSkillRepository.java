package liaison.linkit.profile.domain.repository;

import liaison.linkit.profile.domain.skill.ProfileSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProfileSkillRepository extends JpaRepository<ProfileSkill, Long> {
    boolean existsByProfileId(final Long profileId);

    @Query("SELECT profileSkill FROM ProfileSkill profileSkill WHERE profileSkill.profile.id = :profileId")
    List<ProfileSkill> findAllByProfileId(@Param("profileId") final Long profileId);

    ProfileSkill findByProfileId(@Param("profileId") Long profileId);
}
