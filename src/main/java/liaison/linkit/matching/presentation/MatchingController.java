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

    @PostMapping("/private/profile/{miniProfileId}")
    @MemberOnly
    // 매칭 권한 검증 어노테이션 추가
    public ResponseEntity<Void> createPrivateProfileMatching(
            @Auth final Accessor accessor,
            @PathVariable final Long miniProfileId,
            @RequestBody @Valid MatchingCreateRequest matchingCreateRequest
    ) {
        log.info("miniProfileId={}", miniProfileId);

        matchingService.createProfileMatching(accessor.getMemberId(), miniProfileId, matchingCreateRequest);

        return ResponseEntity.status(HttpStatus.CREATED).build();

    }
}
