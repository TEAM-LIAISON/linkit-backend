package liaison.linkit.member.presentation;

import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.member.dto.request.MemberBasicInformCreateRequest;
import liaison.linkit.member.dto.response.MemberBasicInformResponse;
import liaison.linkit.member.dto.response.MemberResponse;
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

    // 조회 화면 필요
    @GetMapping("/basic-inform")
    @MemberOnly
    public ResponseEntity<MemberBasicInformResponse> getMemberBasicInform(
            @Auth final Accessor accessor
    ) {
        Long memberBasicInformId = memberService.validateMemberBasicInformByMember(accessor.getMemberId());
        final MemberBasicInformResponse memberBasicInformResponse = memberService.getMemberBasicInformDetail(memberBasicInformId);
        return ResponseEntity.ok().body(memberBasicInformResponse);
    }

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

    // 1.4.1. 개인정보 기입 이메일 정보 제공용
    @GetMapping("/email")
    @MemberOnly
    public ResponseEntity<MemberResponse> getMemberEmail(
            @Auth final Accessor accessor
    ) {
        final MemberResponse memberResponse = memberService.getMemberEmail(accessor.getMemberId());
        return ResponseEntity.ok().body(memberResponse);
    }

    // 수정 및 삭제 추가 구현 필요
}

