package liaison.linkit.member.implement;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.member.domain.MemberBasicInform;
import liaison.linkit.member.domain.repository.memberBasicInform.MemberBasicInformRepository;
import liaison.linkit.member.exception.memberBasicInform.MemberBasicInformBadRequestException;
import liaison.linkit.member.presentation.dto.request.memberBasicInform.MemberBasicInformRequestDTO;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class MemberBasicInformCommandAdapter {
    private final MemberBasicInformRepository memberBasicInformRepository;

    public MemberBasicInform save(final MemberBasicInform memberBasicInform) {
        return memberBasicInformRepository.save(memberBasicInform);
    }

    public MemberBasicInform update(
            final Long memberId,
            final MemberBasicInformRequestDTO.memberBasicInformRequest request
    ) {
        return memberBasicInformRepository.update(memberId, request)
                .orElseThrow(() -> MemberBasicInformBadRequestException.BAD_REQUEST_EXCEPTION);
    }

    public void delete(final MemberBasicInform memberBasicInform) {
        memberBasicInformRepository.delete(memberBasicInform);
    }

    public void deleteByMemberId(final Long memberId) {
        memberBasicInformRepository.deleteByMemberId(memberId);
    }
}
