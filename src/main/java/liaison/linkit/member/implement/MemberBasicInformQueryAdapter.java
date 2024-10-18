package liaison.linkit.member.implement;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.member.domain.MemberBasicInform;
import liaison.linkit.member.domain.repository.memberBasicInform.MemberBasicInformRepository;
import liaison.linkit.member.exception.memberBasicInform.MemberBasicInformNotFoundException;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class MemberBasicInformQueryAdapter {
    private final MemberBasicInformRepository memberBasicInformRepository;

    public MemberBasicInform findById(final Long memberBasicInformId) {
        return memberBasicInformRepository
                .findById(memberBasicInformId)
                .orElseThrow(() -> MemberBasicInformNotFoundException.NOT_FOUND_EXCEPTION);
    }

    public boolean existsByMemberId(final Long memberId) {
        return memberBasicInformRepository.existsByMemberId(memberId);
    }

    public MemberBasicInform findByMemberId(final Long memberId) {
        return memberBasicInformRepository.findByMemberId(memberId)
                .orElseThrow(() -> MemberBasicInformNotFoundException.NOT_FOUND_EXCEPTION);
    }
}
