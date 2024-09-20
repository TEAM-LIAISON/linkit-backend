package liaison.linkit.member.business;

import liaison.linkit.member.domain.Member;
import liaison.linkit.member.domain.MemberBasicInform;
import liaison.linkit.member.dto.request.memberBasicInform.MemberBasicInformCreateRequest;
import liaison.linkit.member.dto.request.memberBasicInform.MemberBasicInformUpdateRequest;
import liaison.linkit.member.dto.response.MemberBasicInformResponse;
import liaison.linkit.member.implement.MemberBasicInformCommandAdapter;
import liaison.linkit.member.implement.MemberBasicInformQueryAdapter;
import liaison.linkit.member.implement.MemberQueryAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

// 소셜로그인 이후 기본 정보 기입 플로우부터
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MemberService {
    private final MemberQueryAdapter memberQueryAdapter;
    private final MemberBasicInformQueryAdapter memberBasicInformQueryAdapter;
    private final MemberBasicInformCommandAdapter memberBasicInformCommandAdapter;

    // 멤버 아이디로 회원 기본 정보의 유효성을 검증하는 로직
    public void validateMemberBasicInformByMember(final Long memberId) {
        memberBasicInformQueryAdapter.existsByMemberId(memberId);
    }

    // 회원 기본 정보 저장 메서드
    public void save(
            final Long memberId, final MemberBasicInformCreateRequest memberBasicInformCreateRequest
    ) throws ResponseStatusException {

        final Member member = memberQueryAdapter.findById(memberId);

        // dto -> 객체
        final MemberBasicInform newBasicMemberBasicInform = new MemberBasicInform(
                memberBasicInformCreateRequest.getMemberName(),
                memberBasicInformCreateRequest.getContact(),
                memberBasicInformCreateRequest.isMarketingAgree(),
                member
        );

        member.changeIsMemberBasicInform(true);

        // 기본 정보 입력 사용자로부터 재요청
        if (memberBasicInformQueryAdapter.existsByMemberId(memberId)) {
            final MemberBasicInform savedMemberBasicInform = memberBasicInformQueryAdapter.findByMemberId(memberId);
            savedMemberBasicInform.update(newBasicMemberBasicInform);
        } else {
            memberBasicInformCommandAdapter.save(newBasicMemberBasicInform);
        }
    }

    // (해당 회원의) 회원 기본 정보를 조회하는 메서드
    @Transactional(readOnly = true)
    public MemberBasicInformResponse getPersonalMemberBasicInform(final Long memberId) {
        final MemberBasicInform memberBasicInform = memberBasicInformQueryAdapter.findByMemberId(memberId);
        return MemberBasicInformResponse.personalMemberBasicInform(memberBasicInform);
    }

    public void update(
            final Long memberId,
            final MemberBasicInformUpdateRequest memberBasicInformUpdateRequest
    ) {
        final Member member = memberQueryAdapter.findById(memberId);

        final MemberBasicInform updateMemberBasicInform = new MemberBasicInform(
                memberId,
                memberBasicInformUpdateRequest.getMemberName(),
                memberBasicInformUpdateRequest.getContact(),
                memberBasicInformUpdateRequest.isMarketingAgree(),
                member
        );

        memberBasicInformCommandAdapter.delete(member.getMemberBasicInform());
        memberBasicInformCommandAdapter.save(updateMemberBasicInform);
    }
}
