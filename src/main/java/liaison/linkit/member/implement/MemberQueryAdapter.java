package liaison.linkit.member.implement;

import java.util.Optional;
import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.member.domain.Member;
import liaison.linkit.member.domain.repository.member.MemberRepository;
import liaison.linkit.member.exception.member.MemberNotFoundException;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class MemberQueryAdapter {
    // 데이터를 조회하는 기능을 담당
    private final MemberRepository memberRepository;

    public Member findByEmailId(final String emailId) {
        return memberRepository.findByEmailId(emailId).orElseThrow(() -> MemberNotFoundException.EXCEPTION);
    }

    public boolean existsByEmailId(final String emailId) {
        return memberRepository.existsByEmailId(emailId);
    }

    public Member findById(final Long memberId) {
        return memberRepository
                .findById(memberId)
                .orElseThrow(() -> MemberNotFoundException.EXCEPTION);
    }

    public String findEmailIdById(final Long memberId) {
        return memberRepository.findEmailIdById(memberId);
    }

    public Optional<Member> findBySocialLoginId(final String socialLoginId) {
        return memberRepository.findBySocialLoginId(socialLoginId);
    }

    public String findEmailById(final Long memberId) {
        return memberRepository.findEmailById(memberId);
    }

    public boolean existsByEmail(final String email) {
        return memberRepository.existsByEmail(email);
    }
}
