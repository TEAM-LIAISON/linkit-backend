package liaison.linkit.member.presentation;

import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.member.dto.request.MemberBasicInformRequest;
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

    @PostMapping("/basic-inform")
    @MemberOnly
    public ResponseEntity<Void> createMemberBasicInform(
            @Auth final Accessor accessor,
            @RequestBody MemberBasicInformRequest memberBasicInformRequest
    ) {
        log.info("accessor={}", accessor.getAuthority());
        log.info("accessor.getMemberId()={}", accessor.getMemberId());
        memberService.save(accessor.getMemberId(), memberBasicInformRequest);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}

