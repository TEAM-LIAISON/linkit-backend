package liaison.linkit.member.presentation;

import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.common.presentation.CommonResponse;
import liaison.linkit.member.business.MemberService;
import liaison.linkit.member.presentation.dto.request.memberBasicInform.MemberBasicInformRequestDTO;
import liaison.linkit.member.presentation.dto.response.MemberBasicInformResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
public class MemberController {

    public final MemberService memberService;

    // 회원 기본 정보 수정
    @PostMapping("/basic-inform")
    @MemberOnly
    public CommonResponse<MemberBasicInformResponseDTO.UpdateMemberBasicInformResponse> updateMemberBasicInform(
            @Auth final Accessor accessor,
            @RequestBody final MemberBasicInformRequestDTO.UpdateMemberBasicInformRequest request
    ) {
        return CommonResponse.onSuccess(memberService.updateMemberBasicInform(accessor.getMemberId(), request));
    }

    // 서비스 이용 동의 수정
    @PostMapping("/consent")
    @MemberOnly
    public CommonResponse<MemberBasicInformResponseDTO.UpdateConsentServiceUseResponse> updateConsentServiceUse(
            @Auth final Accessor accessor,
            @RequestBody final MemberBasicInformRequestDTO.UpdateConsentServiceUseRequest updateConsentServiceUseRequest
    ) {
        return CommonResponse.onSuccess(memberService.updateConsentServiceUse(accessor.getMemberId(), updateConsentServiceUseRequest));
    }

//    @GetMapping("/basic-inform")
//    @MemberOnly
//    public CommonResponse<MemberBasicInformResponseDTO.MemberBasicInformDetail> getMemberBasicInform(@Auth final Accessor accessor) {
//        memberService.validateMemberBasicInformByMember(accessor.getMemberId());
//        return CommonResponse.onSuccess(memberService.getPersonalMemberBasicInform(accessor.getMemberId()));
//    }


}

