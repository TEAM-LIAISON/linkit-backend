package liaison.linkit.member.domain.repository.memberBasicInform;

import liaison.linkit.member.domain.MemberBasicInform;

import java.util.Optional;

public interface MemberBasicInformRepositoryCustom {
    boolean existsByMemberId(final Long memberId);
    Optional<MemberBasicInform> findByMemberId(final Long memberId);
    void deleteByMemberId(final Long memberId);
}
