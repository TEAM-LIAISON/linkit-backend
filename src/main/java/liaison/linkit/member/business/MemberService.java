package liaison.linkit.member.business;

import liaison.linkit.member.domain.Member;
import liaison.linkit.member.domain.MemberBasicInform;
import liaison.linkit.member.implement.MemberBasicInformCommandAdapter;
import liaison.linkit.member.implement.MemberBasicInformQueryAdapter;
import liaison.linkit.member.implement.MemberQueryAdapter;
import liaison.linkit.member.presentation.dto.request.memberBasicInform.MemberBasicInformRequestDTO.UpdateConsentServiceUseRequest;
import liaison.linkit.member.presentation.dto.request.memberBasicInform.MemberBasicInformRequestDTO.UpdateMemberBasicInformRequest;
import liaison.linkit.member.presentation.dto.response.MemberBasicInformResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 소셜로그인 이후 기본 정보 기입 플로우부터
@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final MemberQueryAdapter memberQueryAdapter;
    private final MemberBasicInformQueryAdapter memberBasicInformQueryAdapter;
    private final MemberBasicInformCommandAdapter memberBasicInformCommandAdapter;
    private final MemberBasicInformMapper memberBasicInformMapper;

    // 회원 기본 정보 요청 (UPDATE)
    public MemberBasicInformResponseDTO.UpdateMemberBasicInformResponse updateMemberBasicInform(final Long memberId, final UpdateMemberBasicInformRequest request) {

        final MemberBasicInform updatedMemberBasicInform = memberBasicInformCommandAdapter.updateMemberBasicInform(memberId, request);

        final Member member = memberQueryAdapter.findById(memberId);
        member.setCreateMemberBasicInform(updatedMemberBasicInform.isMemberBasicInform());

        final String email = memberQueryAdapter.findEmailById(memberId);

        return memberBasicInformMapper.toMemberBasicInformResponse(updatedMemberBasicInform, email);
    }

    // 서비스 이용 동의 요청 (UPDATE)
    public MemberBasicInformResponseDTO.UpdateConsentServiceUseResponse updateConsentServiceUse(final Long memberId, final UpdateConsentServiceUseRequest request) {
        final MemberBasicInform updatedMemberBasicInform = memberBasicInformCommandAdapter.updateConsentServiceUse(memberId, request);
        return memberBasicInformMapper.toUpdateConsentServiceUseResponse(updatedMemberBasicInform);
    }

    // 회원 기본 정보 조회 (READ)
    public MemberBasicInformResponseDTO.MemberBasicInformDetail getMemberBasicInform(final Long memberId) {

        final MemberBasicInform memberBasicInform = memberBasicInformQueryAdapter.findByMemberId(memberId);
        final String email = memberQueryAdapter.findEmailById(memberId);

        return memberBasicInformMapper.toMemberBasicInformDetail(memberBasicInform, email);
    }

}
