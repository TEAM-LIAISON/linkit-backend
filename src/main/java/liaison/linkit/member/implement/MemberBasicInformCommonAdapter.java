package liaison.linkit.member.implement;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.member.domain.repository.memberBasicInform.MemberBasicInformRepository;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class MemberBasicInformCommonAdapter {
    private final MemberBasicInformRepository memberBasicInformRepository;

    public void deleteByMemberId(final Long memberId) {
        memberBasicInformRepository.deleteByMemberId(memberId);
    }
}
