package liaison.linkit.member.implement;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.member.domain.Member;
import liaison.linkit.member.domain.repository.member.MemberRepository;
import liaison.linkit.member.exception.member.DuplicateEmailIdException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Adapter
@RequiredArgsConstructor
@Slf4j
public class MemberCommandAdapter {
    private final MemberRepository memberRepository;

    public Member create(final Member member) {
        return memberRepository.save(member);
    }

    public Member updateEmailId(final Long memberId, final String emailId) {
        return memberRepository
                .updateEmailId(memberId, emailId)
                .orElseThrow(() -> DuplicateEmailIdException.EXCEPTION);
    }

    public void deleteByMemberId(final Long memberId) {
        memberRepository.deleteByMemberId(memberId);
    }
}
