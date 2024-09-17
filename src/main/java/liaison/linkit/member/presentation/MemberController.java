package liaison.linkit.member.presentation;

import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.common.annotation.ApiErrorCodeExample;
import liaison.linkit.common.exception.AuthErrorCode;
import liaison.linkit.common.exception.GlobalErrorCode;
import liaison.linkit.member.dto.request.memberBasicInform.MemberBasicInformCreateRequest;
import liaison.linkit.member.dto.request.memberBasicInform.MemberBasicInformUpdateRequest;
import liaison.linkit.member.dto.response.MemberBasicInformResponse;
import liaison.linkit.member.exception.MemberErrorCode;
import liaison.linkit.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
@Slf4j
public class MemberController {

    public final MemberService memberService;

    // 회원 기본 정보 등록 API
    @PostMapping("/basic-inform")
    @MemberOnly
    public ResponseEntity<Void> createMemberBasicInform(
            @Auth final Accessor accessor,
            @RequestBody MemberBasicInformCreateRequest memberBasicInformCreateRequest
    ) {
        memberService.save(accessor.getMemberId(), memberBasicInformCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @ApiErrorCodeExample(
            value = {MemberErrorCode.class, AuthErrorCode.class, GlobalErrorCode.class}
    )
    @GetMapping("/basic-inform")
    @MemberOnly
    public ResponseEntity<MemberBasicInformResponse> getMemberBasicInform(
            @Auth final Accessor accessor
    ) {
        memberService.validateMemberBasicInformByMember(accessor.getMemberId());
        final MemberBasicInformResponse memberBasicInformResponse
                = memberService.getPersonalMemberBasicInform(accessor.getMemberId());
        return ResponseEntity.ok().body(memberBasicInformResponse);
    }

    // 회원 기본 정보 수정 API
    @PutMapping("/basic-inform")
    @MemberOnly
    public ResponseEntity<Void> updateMemberBasicInform(
            @Auth final Accessor accessor,
            @RequestBody MemberBasicInformUpdateRequest memberBasicInformUpdateRequest
    ) {
        memberService.update(accessor.getMemberId(), memberBasicInformUpdateRequest);
        return ResponseEntity.status(HttpStatus.OK).build();
    }


}

