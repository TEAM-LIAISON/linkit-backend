package liaison.linkit.team.domain.repository;

import liaison.linkit.team.domain.TeamProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TeamProfileRepository extends JpaRepository<TeamProfile, Long> {

    @Query("""
           SELECT teamProfile
           FROM TeamProfile teamProfile
           WHERE teamProfile.member.id = :memberId
           """)
    Optional<TeamProfile> findByMemberId(@Param("memberId") final Long memberId);

    boolean existsByMemberId(final Long memberId);
}
