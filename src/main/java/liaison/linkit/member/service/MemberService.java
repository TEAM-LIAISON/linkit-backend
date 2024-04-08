package liaison.linkit.member.service;

import liaison.linkit.global.exception.BadRequestException;
import liaison.linkit.member.domain.Member;
import liaison.linkit.member.domain.MemberBasicInform;
import liaison.linkit.member.domain.repository.MemberBasicInformRepository;
import liaison.linkit.member.domain.repository.MemberRepository;

import liaison.linkit.member.dto.request.MemberBasicInformRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static liaison.linkit.global.exception.ExceptionCode.NOT_FOUND_MEMBER_ID;
import static liaison.linkit.global.exception.ExceptionCode.NOT_FOUND_MEMBER_INFORM_ID;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {
    private final MemberRepository memberRepository;
    private final MemberBasicInformRepository memberBasicInformRepository;

    public void save(Long memberId, MemberBasicInformRequest memberBasicInformRequest) {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_MEMBER_ID));

        final MemberBasicInform newBasicMemberBasicInform = new MemberBasicInform(
                memberBasicInformRequest.getUsername(),
                memberBasicInformRequest.getContact(),
                memberBasicInformRequest.getMajor(),
                memberBasicInformRequest.getJob(),
                memberBasicInformRequest.getTeamBuildingStep(),
                member
        );

        memberBasicInformRepository.save(newBasicMemberBasicInform);
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
