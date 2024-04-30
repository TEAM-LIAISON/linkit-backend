package liaison.linkit.profile.domain.repository;

import liaison.linkit.profile.domain.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
    Profile findByMemberId(@Param("memberId") final Long memberId);

    boolean existsByMemberId(Long memberId);
}
