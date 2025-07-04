package liaison.linkit.member.domain.repository.memberBasicInform;

import java.util.Optional;

import liaison.linkit.member.domain.MemberBasicInform;
import liaison.linkit.member.presentation.dto.MemberBasicInformRequestDTO;
import liaison.linkit.member.presentation.dto.MemberBasicInformRequestDTO.UpdateConsentMarketingRequest;

public interface MemberBasicInformRepositoryCustom {
    boolean existsByMemberId(final Long memberId);

    Optional<MemberBasicInform> findByMemberId(final Long memberId);

    Optional<MemberBasicInform> updateMemberBasicInform(
            final Long memberId,
            MemberBasicInformRequestDTO.UpdateMemberBasicInformRequest
                    updateMemberBasicInformRequest);

    Optional<MemberBasicInform> updateConsentServiceUse(
            final Long memberId,
            MemberBasicInformRequestDTO.UpdateConsentServiceUseRequest
                    updateConsentServiceUseRequest);

    Optional<MemberBasicInform> updateMemberName(
            final Long memberId,
            MemberBasicInformRequestDTO.UpdateMemberNameRequest updateMemberNameRequest);

    Optional<MemberBasicInform> updateMemberContact(
            final Long memberId,
            MemberBasicInformRequestDTO.UpdateMemberContactRequest updateMemberContactRequest);

    Optional<MemberBasicInform> updateConsentMarketing(
            final Long memberId, UpdateConsentMarketingRequest updateConsentMarketingRequest);
}
