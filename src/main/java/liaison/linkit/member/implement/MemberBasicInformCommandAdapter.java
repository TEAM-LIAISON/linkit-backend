package liaison.linkit.member.implement;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.member.domain.MemberBasicInform;
import liaison.linkit.member.domain.repository.memberBasicInform.MemberBasicInformRepository;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class MemberBasicInformCommandAdapter {
    private final MemberBasicInformRepository memberBasicInformRepository;

    public MemberBasicInform save(final MemberBasicInform memberBasicInform) {
        return memberBasicInformRepository.save(memberBasicInform);
    }

    public void delete(final MemberBasicInform memberBasicInform) {
        memberBasicInformRepository.delete(memberBasicInform);
    }

    public void deleteByMemberId(final Long memberId) {
        memberBasicInformRepository.deleteByMemberId(memberId);
    }
}
