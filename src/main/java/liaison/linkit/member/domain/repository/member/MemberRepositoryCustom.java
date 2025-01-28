package liaison.linkit.member.domain.repository.member;

import liaison.linkit.member.domain.Member;

import java.util.Optional;

public interface MemberRepositoryCustom {
    Optional<Member> findBySocialLoginId(final String socialLoginId);

    Optional<Member> findByEmailId(final String emailId);

    Optional<Member> updateEmailId(final Long memberId, final String emailId);

    boolean existsByEmail(final String email);

    boolean existsByEmailId(final String emailId);

    void deleteByMemberId(final Long memberId);

    String findEmailById(final Long memberId);

    String findEmailIdById(final Long memberId);

    Optional<Member> findByEmail(final String email);
}
