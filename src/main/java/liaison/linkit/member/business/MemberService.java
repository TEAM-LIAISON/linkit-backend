package liaison.linkit.member.business;

import liaison.linkit.member.domain.Member;
import liaison.linkit.member.domain.MemberBasicInform;
import liaison.linkit.member.presentation.dto.request.memberBasicInform.MemberBasicInformRequestDTO;
import liaison.linkit.member.presentation.dto.request.memberBasicInform.MemberBasicInformRequestDTO.memberBasicInformRequest;
import liaison.linkit.member.presentation.dto.response.MemberBasicInformResponseDTO;
import liaison.linkit.member.implement.MemberBasicInformCommandAdapter;
import liaison.linkit.member.implement.MemberBasicInformQueryAdapter;
import liaison.linkit.member.implement.MemberQueryAdapter;
import liaison.linkit.member.presentation.dto.response.MemberBasicInformResponseDTO.RequestMemberBasicInform;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 소셜로그인 이후 기본 정보 기입 플로우부터
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MemberService {
    private final MemberQueryAdapter memberQueryAdapter;
    private final MemberBasicInformQueryAdapter memberBasicInformQueryAdapter;
    private final MemberBasicInformCommandAdapter memberBasicInformCommandAdapter;
    private final MemberBasicInformMapper memberBasicInformMapper;

    public void validateMemberBasicInformByMember(final Long memberId) {
        memberBasicInformQueryAdapter.existsByMemberId(memberId);
    }

    // 회원 기본 정보 생성 메서드 (CREATE)
    public RequestMemberBasicInform save(final Long memberId, final memberBasicInformRequest request) {
        final Member member = memberQueryAdapter.findById(memberId);
        final MemberBasicInform savedBasicMemberBasicInform = memberBasicInformCommandAdapter.save(memberBasicInformMapper.toMemberBasicInform(member, request));
        member.existIsMemberBasicInform(true);
        return memberBasicInformMapper.toRequestMemberBasicInform(savedBasicMemberBasicInform);
    }

    // 회원 기본 정보 조회 (READ)
    @Transactional(readOnly = true)
    public MemberBasicInformResponseDTO.MemberBasicInformDetail getPersonalMemberBasicInform(final Long memberId) {
        final MemberBasicInform memberBasicInform = memberBasicInformQueryAdapter.findByMemberId(memberId);
        final String email = memberQueryAdapter.findEmailById(memberId);
        return memberBasicInformMapper.toMemberBasicInformDetail(memberBasicInform, email);
    }

    // 회원 기본 정보 수정 (UPDATE)
    public RequestMemberBasicInform update(final Long memberId, final MemberBasicInformRequestDTO.memberBasicInformRequest request) {
        final Member member = memberQueryAdapter.findById(memberId);
        final MemberBasicInform updatedMemberBasicInform = memberBasicInformCommandAdapter.update(memberId, request);
        return memberBasicInformMapper.toRequestMemberBasicInform(updatedMemberBasicInform);
    }
}
