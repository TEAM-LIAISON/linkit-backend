package liaison.linkit.profile.domain.repository;

import liaison.linkit.profile.domain.MiniProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface MiniProfileRepository extends JpaRepository<MiniProfile, Long> {
    boolean existsByMemberId(Long memberId);

    MiniProfile findByMemberId(@Param("memberId") final Long memberId);
}
