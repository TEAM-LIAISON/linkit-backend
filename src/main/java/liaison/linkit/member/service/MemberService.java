package liaison.linkit.member.service;

import liaison.linkit.global.exception.AuthException;
import liaison.linkit.global.exception.BadRequestException;
import liaison.linkit.member.domain.Member;
import liaison.linkit.member.domain.MemberBasicInform;
import liaison.linkit.member.domain.MemberRole;
import liaison.linkit.member.domain.repository.MemberBasicInformRepository;
import liaison.linkit.member.domain.repository.MemberRepository;
import liaison.linkit.member.domain.repository.MemberRoleRepository;
import liaison.linkit.member.dto.request.MemberBasicInformCreateRequest;
import liaison.linkit.member.dto.response.MemberBasicInformResponse;
import liaison.linkit.member.dto.response.MemberResponse;
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

    private final MemberRepository memberRepository;
    private final MemberBasicInformRepository memberBasicInformRepository;
    private final MemberRoleRepository memberRoleRepository;

    public Long validateMemberBasicInformByMember(final Long memberId) {
        if (!memberBasicInformRepository.existsByMemberId(memberId)) {
            throw new AuthException(INVALID_MEMBER_BASIC_INFORM_WITH_MEMBER);
        } else {
            return memberBasicInformRepository.findByMemberId(memberId).getId();
        }
    }

    public void save(final Long memberId, final MemberBasicInformCreateRequest memberBasicInformCreateRequest) throws ResponseStatusException {
        log.info("memberId={}", memberId);
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER_ID));


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

    public Long getMemberRole(final String roleName) {
        final MemberRole memberRole = memberRoleRepository.findMemberRolesByRoleName(roleName);
        return memberRole.getId();
    }

    @Transactional(readOnly = true)
    public MemberBasicInformResponse getMemberBasicInformDetail(final Long memberBasicInformId) {
        final MemberBasicInform memberBasicInform = memberBasicInformRepository.findById(memberBasicInformId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER_BASIC_INFORM_ID));
        return MemberBasicInformResponse.of(memberBasicInform);
    }

    @Transactional(readOnly = true)
    public MemberResponse getMemberEmail(final Long memberId) {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER_ID));
        return MemberResponse.of(member);
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
