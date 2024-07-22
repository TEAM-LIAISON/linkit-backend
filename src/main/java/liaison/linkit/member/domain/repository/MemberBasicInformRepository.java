package liaison.linkit.member.domain.repository;

import liaison.linkit.member.domain.Member;
import liaison.linkit.member.domain.MemberBasicInform;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface MemberBasicInformRepository extends JpaRepository<MemberBasicInform, Long> {

    Optional<MemberBasicInform> findByMember(Member member);

    boolean existsByMember(Member member);

    boolean existsByMemberId(@Param("memberId") final Long memberId);

    @Query("SELECT memberBasicInform FROM MemberBasicInform memberBasicInform WHERE memberBasicInform.member.id = :memberId")
    Optional<MemberBasicInform> findByMemberId(@Param("memberId") final Long memberId);

    @Modifying
    @Transactional
    @Query("""
           UPDATE MemberBasicInform mbi
           SET mbi.status = 'DELETED'
           WHERE mbi.member.id = :memberId
           """)
    void deleteByMemberId(@Param("memberId") final Long memberId);
}
