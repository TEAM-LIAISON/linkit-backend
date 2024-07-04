package liaison.linkit.profile.domain.repository.jobRole;

import liaison.linkit.profile.domain.role.ProfileJobRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ProfileJobRoleRepository extends JpaRepository<ProfileJobRole, Long> {

    boolean existsByProfileId(final Long profileId);

    @Modifying
    @Transactional
    @Query("DELETE FROM ProfileJobRole profileJobRole WHERE profileJobRole.profile.id = :profileId")
    void deleteAllByProfileId(@Param("profileId") final Long profileId);

    @Query("""
           SELECT pjr
           FROM ProfileJobRole pjr
           WHERE pjr.profile.id = :profileId
           """)
    List<ProfileJobRole> findAllByProfileId(@Param("profileId") final Long profileId);
}
