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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public void save(Long memberId, MemberBasicInformCreateRequest memberBasicInformCreateRequest) {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER_ID));

        final MemberRole memberRole = memberRoleRepository.findById(getMemberRole(memberBasicInformCreateRequest.getRoleName()))
                .orElseThrow(()-> new BadRequestException(NOT_FOUND_MEMBER_ROLE_ID));

        final MemberBasicInform newBasicMemberBasicInform = new MemberBasicInform(
                memberBasicInformCreateRequest.getMemberName(),
                memberBasicInformCreateRequest.getContact(),
                memberRole,
                memberBasicInformCreateRequest.isMarketingAgree(),
                member
        );
        memberBasicInformRepository.save(newBasicMemberBasicInform);
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
