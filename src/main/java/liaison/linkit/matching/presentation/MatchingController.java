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

    @PostMapping("/profile/{profileId}")
    @MemberOnly
    public ResponseEntity<Void> createProfileMatching(
            @Auth final Accessor accessor,
            @PathVariable final Long profileId,
            @RequestBody @Valid MatchingCreateRequest matchingCreateRequest
    ) {
        log.info("profileId={}", profileId);

        matchingService.createProfileMatching(accessor.getMemberId(), profileId, matchingCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/teamProfile/{teamProfileId}")
    @MemberOnly
    public ResponseEntity<Void> createTeamProfileMatching(
            @Auth final Accessor accessor,
            @PathVariable final Long teamProfileId,
            @RequestBody @Valid MatchingCreateRequest matchingCreateRequest
    ) {

        matchingService.createTeamProfileMatching(accessor.getMemberId(), teamProfileId, matchingCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
