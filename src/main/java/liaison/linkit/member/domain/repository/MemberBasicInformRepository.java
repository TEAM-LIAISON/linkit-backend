package liaison.linkit.member.domain.repository;

import liaison.linkit.member.domain.Member;
import liaison.linkit.member.domain.MemberBasicInform;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberBasicInformRepository extends JpaRepository<MemberBasicInform, Long> {
    Optional<MemberBasicInform> findByMember(Member member);

    boolean existsByMember(Member member);

    boolean existsByMemberId(Long memberId);

    @Query("SELECT memberBasicInform FROM MemberBasicInform memberBasicInform WHERE memberBasicInform.member.id = :memberId")
    Optional<MemberBasicInform> findByMemberId(@Param("memberId") final Long memberId);

    @Query("""
           UPDATE MemberBasicInform memberBasicInform
           SET memberBasicInform.status = 'DELETED'
           WHERE memberBasicInform.member.id = :memberId
           """)
    void deleteByMemberId(@Param("memberId") final Long memberId);
}
