package liaison.linkit.member.domain.repository.memberBasicInform;

import liaison.linkit.member.domain.MemberBasicInform;

import java.util.Optional;
import liaison.linkit.member.presentation.dto.request.memberBasicInform.MemberBasicInformRequestDTO;

public interface MemberBasicInformRepositoryCustom {
    boolean existsByMemberId(final Long memberId);

    Optional<MemberBasicInform> findByMemberId(final Long memberId);

    void deleteByMemberId(final Long memberId);

    Optional<MemberBasicInform> update(final Long memberId, final MemberBasicInformRequestDTO.memberBasicInformRequest request);
}
