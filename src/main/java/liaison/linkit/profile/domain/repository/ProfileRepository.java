package liaison.linkit.profile.domain.repository;

import liaison.linkit.profile.domain.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Long> {

    @Query("""
           SELECT profile
           FROM Profile profile
           WHERE profile.member.id = :memberId
           """)
    Optional<Profile> findByMemberId(@Param("memberId") final Long memberId);

    boolean existsByMemberId(final Long memberId);
}
