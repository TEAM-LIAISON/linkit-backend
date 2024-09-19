package liaison.linkit.member.implement;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.member.domain.MemberBasicInform;
import liaison.linkit.member.domain.repository.memberBasicInform.MemberBasicInformRepository;
import liaison.linkit.member.exception.MemberBasicInformNotFoundException;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class MemberBasicInformQueryAdapter {
    private final MemberBasicInformRepository memberBasicInformRepository;

    public MemberBasicInform findById(final Long memberBasicInformId) {
        return memberBasicInformRepository
                .findById(memberBasicInformId)
                .orElseThrow(() -> MemberBasicInformNotFoundException.EXCEPTION);
    }
}
