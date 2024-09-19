package liaison.linkit.member.implement;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.member.domain.Member;
import liaison.linkit.member.domain.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class MemberCommandAdapter {
    private final MemberRepository memberRepository;

    public Member create(final Member member) {
        return memberRepository.save(member);
    }

    public void deleteByMemberId(final Long memberId) {
        memberRepository.deleteByMemberId(memberId);
    }
}
