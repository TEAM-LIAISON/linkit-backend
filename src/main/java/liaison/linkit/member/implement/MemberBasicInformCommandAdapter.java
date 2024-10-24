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

    public MemberBasicInform create(final MemberBasicInform memberBasicInform) {
        return memberBasicInformRepository.save(memberBasicInform);
    }

    public void delete(final MemberBasicInform memberBasicInform) {
        memberBasicInformRepository.delete(memberBasicInform);
    }

    public MemberBasicInform updateMemberBasicInform(
            final Long memberId,
            final MemberBasicInformRequestDTO.UpdateMemberBasicInformRequest request
    ) {
        return memberBasicInformRepository.updateMemberBasicInform(memberId, request)
                .orElseThrow(() -> MemberBasicInformBadRequestException.BAD_REQUEST_EXCEPTION);
    }

    public MemberBasicInform updateConsentServiceUse(
            final Long memberId,
            final MemberBasicInformRequestDTO.UpdateConsentServiceUseRequest request
    ) {
        return memberBasicInformRepository.updateConsentServiceUse(memberId, request)
                .orElseThrow(() -> MemberBasicInformBadRequestException.BAD_REQUEST_EXCEPTION);
    }
}
