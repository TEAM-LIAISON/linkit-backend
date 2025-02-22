package liaison.linkit.member.implement;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.member.domain.MemberBasicInform;
import liaison.linkit.member.domain.repository.memberBasicInform.MemberBasicInformRepository;
import liaison.linkit.member.exception.memberBasicInform.MemberBasicInformBadRequestException;
import liaison.linkit.member.presentation.dto.MemberBasicInformRequestDTO;
import liaison.linkit.member.presentation.dto.MemberBasicInformRequestDTO.UpdateConsentMarketingRequest;
import liaison.linkit.member.presentation.dto.MemberBasicInformRequestDTO.UpdateMemberContactRequest;
import liaison.linkit.member.presentation.dto.MemberBasicInformRequestDTO.UpdateMemberNameRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Adapter
@RequiredArgsConstructor
@Slf4j
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
            final MemberBasicInformRequestDTO.UpdateMemberBasicInformRequest request) {
        log.info("memberId = {}의 회원 기본 정보 수정을 진행합니다.", memberId);
        return memberBasicInformRepository
                .updateMemberBasicInform(memberId, request)
                .orElseThrow(() -> MemberBasicInformBadRequestException.BAD_REQUEST_EXCEPTION);
    }

    public MemberBasicInform updateConsentServiceUse(
            final Long memberId,
            final MemberBasicInformRequestDTO.UpdateConsentServiceUseRequest request) {
        return memberBasicInformRepository
                .updateConsentServiceUse(memberId, request)
                .orElseThrow(() -> MemberBasicInformBadRequestException.BAD_REQUEST_EXCEPTION);
    }

    public MemberBasicInform updateMemberName(
            final Long memberId, final UpdateMemberNameRequest request) {
        return memberBasicInformRepository
                .updateMemberName(memberId, request)
                .orElseThrow(() -> MemberBasicInformBadRequestException.BAD_REQUEST_EXCEPTION);
    }

    public MemberBasicInform updateMemberContact(
            final Long memberId, final UpdateMemberContactRequest request) {
        return memberBasicInformRepository
                .updateMemberContact(memberId, request)
                .orElseThrow(() -> MemberBasicInformBadRequestException.BAD_REQUEST_EXCEPTION);
    }

    public MemberBasicInform updateConsentMarketing(
            final Long memberId,
            final UpdateConsentMarketingRequest updateConsentServiceUseRequest) {
        return memberBasicInformRepository
                .updateConsentMarketing(memberId, updateConsentServiceUseRequest)
                .orElseThrow(() -> MemberBasicInformBadRequestException.BAD_REQUEST_EXCEPTION);
    }
}
