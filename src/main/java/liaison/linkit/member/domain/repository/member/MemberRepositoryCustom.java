package liaison.linkit.member.domain.repository.member;

import liaison.linkit.member.domain.Member;

import java.util.Optional;

public interface MemberRepositoryCustom {
    Optional<Member> findBySocialLoginId(final String socialLoginId);

    boolean existsByEmail(final String email);

    void deleteByMemberId(final Long memberId);
}
