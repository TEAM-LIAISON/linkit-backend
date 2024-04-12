package liaison.linkit.profile.domain.repository;

import liaison.linkit.profile.domain.Antecedents;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface AntecedentsRepository extends JpaRepository<Antecedents, Long> {
    boolean existsByMemberId(Long memberId);

    Antecedents findByMemberId(@Param("memberId") final Long memberId);

    @Query("SELECT antecedents FROM Antecedents antecedents WHERE antecedents.member.id = :memberId")
    List<Antecedents> findAllByMemberId(Long memberId);
}
