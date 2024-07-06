package liaison.linkit.matching.presentation;

import jakarta.validation.Valid;
import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.matching.dto.request.MatchingCreateRequest;
import liaison.linkit.matching.service.MatchingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/matching")
@Slf4j
public class MatchingController {

    public final MatchingService matchingService;

    // 매칭 요청을 보내기 전에 GET 메서드로 해당 사용자의 권한을 체킹?
    // 아니면 요청이 발생했을 때 사용자의 권한을 조회하는 방법?

    // 개인 이력서에 매칭 요청을 보내는 경우
    @PostMapping("/private/profile/{miniProfileId}")
    @MemberOnly
    // 매칭 권한 별도 구현
    public ResponseEntity<Void> createPrivateProfileMatching(
            @Auth final Accessor accessor,
            @PathVariable final Long miniProfileId,
            @RequestBody @Valid MatchingCreateRequest matchingCreateRequest
    ) {
        log.info("miniProfileId={}", miniProfileId);
        matchingService.createProfileMatching(accessor.getMemberId(), miniProfileId, matchingCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

//    팀 소개서에 매칭 요청을 보내는 경우
//    @PostMapping("/team/profile/{teamMiniProfileId}")
//    @MemberOnly
//    @CheckProfileAccess
//    public 
}
