package liaison.linkit.profile.domain.repository;

import liaison.linkit.profile.domain.Awards;
import liaison.linkit.profile.dto.response.AwardsResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AwardsRepository extends JpaRepository<Awards, Long> {

    boolean existsByMemberId(Long memberId);

    @Query("SELECT awards FROM Awards awards WHERE awards.member.id = :memberId")
    List<Awards> findAllByMemberId(@Param("memberId") final Long memberId);

    Awards findByMemberId(@Param("memberId") final Long memberId);

}
