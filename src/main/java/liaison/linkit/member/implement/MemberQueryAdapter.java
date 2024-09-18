package liaison.linkit.member.implement;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.member.domain.Member;
import liaison.linkit.member.domain.repository.member.MemberRepository;
import liaison.linkit.member.exception.MemberNotFoundException;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class MemberQueryAdapter {
    private final MemberRepository memberRepository;

    public Member findById(Long memberId) {
        return memberRepository
                .findById(memberId)
                .orElseThrow(() -> MemberNotFoundException.EXCEPTION);
    }
}
