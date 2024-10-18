package liaison.linkit.member.presentation;

import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.common.presentation.CommonResponse;
import liaison.linkit.member.business.MemberService;
import liaison.linkit.member.presentation.dto.request.memberBasicInform.MemberBasicInformRequestDTO;
import liaison.linkit.member.presentation.dto.request.memberBasicInform.MemberBasicInformRequestDTO.memberBasicInformRequest;
import liaison.linkit.member.presentation.dto.response.MemberBasicInformResponseDTO;
import liaison.linkit.member.presentation.dto.response.MemberBasicInformResponseDTO.RequestMemberBasicInform;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
public class MemberController {

    public final MemberService memberService;

    @PostMapping("/basic-inform")
    @MemberOnly
    public CommonResponse<RequestMemberBasicInform> createMemberBasicInform(
            @Auth final Accessor accessor,
            @RequestBody final memberBasicInformRequest request
    ) {
        return CommonResponse.onSuccess(memberService.save(accessor.getMemberId(), request));
    }

    @GetMapping("/basic-inform")
    @MemberOnly
    public CommonResponse<MemberBasicInformResponseDTO.MemberBasicInformDetail> getMemberBasicInform(@Auth final Accessor accessor) {
        memberService.validateMemberBasicInformByMember(accessor.getMemberId());
        return CommonResponse.onSuccess(memberService.getPersonalMemberBasicInform(accessor.getMemberId()));
    }

    // 회원 기본 정보 수정 API
    @PutMapping("/basic-inform")
    @MemberOnly
    public CommonResponse<RequestMemberBasicInform> updateMemberBasicInform(
            @Auth final Accessor accessor,
            @RequestBody final MemberBasicInformRequestDTO.memberBasicInformRequest request
    ) {
        return CommonResponse.onSuccess(memberService.update(accessor.getMemberId(), request));
    }
}

