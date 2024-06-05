package liaison.linkit.member.service;

import liaison.linkit.global.exception.AuthException;
import liaison.linkit.global.exception.BadRequestException;
import liaison.linkit.member.domain.Member;
import liaison.linkit.member.domain.MemberBasicInform;
import liaison.linkit.member.domain.repository.MemberBasicInformRepository;
import liaison.linkit.member.domain.repository.MemberRepository;
import liaison.linkit.member.dto.request.MemberBasicInformCreateRequest;
import liaison.linkit.member.dto.response.MemberBasicInformResponse;
import liaison.linkit.member.dto.response.MemberResponse;
import liaison.linkit.profile.domain.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import static liaison.linkit.global.exception.ExceptionCode.*;

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
    public void save(final Long memberId, final MemberBasicInformCreateRequest memberBasicInformCreateRequest) throws ResponseStatusException {
        final Member member = getMember(memberId);
        final MemberBasicInform newBasicMemberBasicInform = new MemberBasicInform(
                memberBasicInformCreateRequest.getMemberName(),
                memberBasicInformCreateRequest.getContact(),
                memberBasicInformCreateRequest.isMarketingAgree(),
                member
        );

        member.changeIsMemberBasicInform(true);

        try {
            memberBasicInformRepository.save(newBasicMemberBasicInform);
        } catch (DataIntegrityViolationException e) {
            if (e.getMessage().contains("Duplicate entry")) {
                String duplicateKey = e.getMessage().split("'")[1]; // 오류 메시지 구조에 따라 다를 수 있음
                throw new ResponseStatusException(HttpStatus.CONFLICT, "DUPLICATE_ENTRY: 중복된 데이터가 존재합니다. 키 값: " + duplicateKey);
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "데이터 저장 중 오류가 발생했습니다.");
            }
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

//    public void updateBasicMemberInform(final Long memberId, final MemberBasicInformRequest memberBasicInformRequest){
//       final Member member = memberRepository.findById(memberId)
//                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER_ID));
//
//       final MemberBasicInform memberBasicInform = memberBasicInformRepository.findByMember(member)
//               .orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER_INFORM_ID));
//
//       final MemberBasicInform updateBasicMemberBasicInform = new MemberBasicInform(
//               memberBasicInform.getId(),
//               memberBasicInformRequest.getUsername(),
//               memberBasicInformRequest.getContact(),
//               memberBasicInformRequest.getMajor(),
//               memberBasicInformRequest.getJob(),
//               memberBasicInformRequest.getTeamBuildingStep()
//       );
//
//       memberBasicInformRepository.save(updateBasicMemberBasicInform);
//    }

//    private MemberBasicInform findOrCreateMemberInform(final Long memberId, final MemberBasicInformRequest memberBasicInformRequest) {
//        final Member member = memberRepository.findById(memberId)
//                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER_ID));
//        return memberBasicInformRepository.findByMember(member)
//                .orElseGet(() -> createMemberInform(member, memberBasicInformRequest));
//    }
//
//    private MemberBasicInform createMemberInform(final Member member, final MemberBasicInformRequest memberBasicInformRequest){
//        if(!memberBasicInformRepository.existsByMember(member)){
//
//            return memberBasicInformRepository.save(new )
//        }
//    }
}
