package liaison.linkit.matching.presentation;

import jakarta.validation.Valid;
import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.matching.dto.request.MatchingCreateRequest;
import liaison.linkit.matching.service.MatchingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/matching")
public class MatchingController {
    public final MatchingService matchingService;

    @PostMapping("/{profileId}")
    @MemberOnly
    public ResponseEntity<Void> createProfileMatching(
            @Auth final Accessor accessor,
            @RequestBody @Valid MatchingCreateRequest matchingCreateRequest
    ) {
        matchingService.createProfileMatching(accessor.getMemberId(), matchingCreateRequest);
        return ResponseEntity.ok().build();
    }
}
