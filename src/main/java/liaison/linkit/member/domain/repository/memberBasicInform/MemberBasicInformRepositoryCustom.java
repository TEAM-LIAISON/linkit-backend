package liaison.linkit.member.domain.repository.memberBasicInform;

import java.util.Optional;
import liaison.linkit.member.domain.MemberBasicInform;
import liaison.linkit.member.presentation.dto.request.memberBasicInform.MemberBasicInformRequestDTO;

public interface MemberBasicInformRepositoryCustom {
    boolean existsByMemberId(final Long memberId);

    Optional<MemberBasicInform> findByMemberId(final Long memberId);

    Optional<MemberBasicInform> updateMemberBasicInform(final Long memberId, MemberBasicInformRequestDTO.UpdateMemberBasicInformRequest updateMemberBasicInformRequest);

    Optional<MemberBasicInform> updateConsentServiceUse(final Long memberId, MemberBasicInformRequestDTO.UpdateConsentServiceUseRequest updateConsentServiceUseRequest);
}
