package liaison.linkit.member.domain.repository;

import liaison.linkit.member.domain.Member;
import liaison.linkit.member.domain.MemberBasicInform;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberBasicInformRepository extends JpaRepository<MemberBasicInform, Long> {
    Optional<MemberBasicInform> findByMember(Member member);

    boolean existsByMember(Member member);
}
