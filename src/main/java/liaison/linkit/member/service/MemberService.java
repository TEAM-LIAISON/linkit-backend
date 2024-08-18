package liaison.linkit.member.service;

import liaison.linkit.global.exception.AuthException;
import liaison.linkit.global.exception.BadRequestException;
import liaison.linkit.member.domain.Member;
import liaison.linkit.member.domain.MemberBasicInform;
import liaison.linkit.member.domain.repository.memberBasicInform.MemberBasicInformRepository;
import liaison.linkit.member.domain.repository.member.MemberRepository;
import liaison.linkit.member.dto.request.memberBasicInform.MemberBasicInformCreateRequest;
import liaison.linkit.member.dto.request.memberBasicInform.MemberBasicInformUpdateRequest;
import liaison.linkit.member.dto.response.MemberBasicInformResponse;
import liaison.linkit.member.dto.response.MemberResponse;
import liaison.linkit.profile.domain.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import static liaison.linkit.global.exception.ExceptionCode.NOT_FOUND_MEMBER_BASIC_INFORM_BY_MEMBER_ID;
import static liaison.linkit.global.exception.ExceptionCode.NOT_FOUND_MEMBER_BY_MEMBER_ID;

// 소셜로그인 이후 기본 정보 기입 플로우부터
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MemberService {

    // 회원 기본 정보를 다룸

    private final MemberRepository memberRepository;
    private final MemberBasicInformRepository memberBasicInformRepository;
    private final ProfileRepository profileRepository;

    // 회원 정보를 가져오는 메서드
    private Member getMember(final Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER_BY_MEMBER_ID));
    }

    // 회원 기본 정보를 가져오는 메서드
    private MemberBasicInform getMemberBasicInform(final Long memberId) {
        return memberBasicInformRepository.findByMemberId(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER_BASIC_INFORM_BY_MEMBER_ID));
    }

    // 멤버 아이디로 회원 기본 정보의 유효성을 검증하는 로직
    public void validateMemberBasicInformByMember(final Long memberId) {
        if (!memberBasicInformRepository.existsByMemberId(memberId)) {
            throw new AuthException(NOT_FOUND_MEMBER_BASIC_INFORM_BY_MEMBER_ID);
        }
    }

    // 회원 기본 정보 저장 메서드
    public void save(
            final Long memberId, final MemberBasicInformCreateRequest memberBasicInformCreateRequest
    ) throws ResponseStatusException {

        final Member member = getMember(memberId);

        // dto -> 객체
        final MemberBasicInform newBasicMemberBasicInform = new MemberBasicInform(
                memberBasicInformCreateRequest.getMemberName(),
                memberBasicInformCreateRequest.getContact(),
                memberBasicInformCreateRequest.isMarketingAgree(),
                member
        );

        member.changeIsMemberBasicInform(true);

        // 기본 정보 입력 사용자로부터 재요청
        if (memberBasicInformRepository.existsByMemberId(memberId)) {
            final MemberBasicInform savedMemberBasicInform = memberBasicInformRepository.findByMemberId(memberId)
                    .orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER_BASIC_INFORM_BY_MEMBER_ID));

            // 객체 update
            savedMemberBasicInform.update(newBasicMemberBasicInform);
        } else {
            memberBasicInformRepository.save(newBasicMemberBasicInform);
        }
    }

    // (해당 회원의) 회원 기본 정보를 조회하는 메서드
    @Transactional(readOnly = true)
    public MemberBasicInformResponse getPersonalMemberBasicInform(final Long memberId) {
        final MemberBasicInform memberBasicInform = getMemberBasicInform(memberId);
        return MemberBasicInformResponse.personalMemberBasicInform(memberBasicInform);
    }

    // (해당 회원의) 회원 기본 정보 중 이메일을 조회하는 메서드
    @Transactional(readOnly = true)
    public MemberResponse getMemberEmail(final Long memberId) {
        final Member member = getMember(memberId);
        return MemberResponse.getEmail(member);
    }

    public void update(
            final Long memberId,
            final MemberBasicInformUpdateRequest memberBasicInformUpdateRequest
    ) {
        final Member member = getMember(memberId);
        final MemberBasicInform updateMemberBasicInform = new MemberBasicInform(
                memberId,
                memberBasicInformUpdateRequest.getMemberName(),
                memberBasicInformUpdateRequest.getContact(),
                memberBasicInformUpdateRequest.isMarketingAgree(),
                member
        );

        // 저장되어 있던 회원 기본 정보 삭제
        memberBasicInformRepository.delete(member.getMemberBasicInform());

        // 새롭게 전달 받은 회원 기본 정보 저장
        memberBasicInformRepository.save(updateMemberBasicInform);
    }


}
