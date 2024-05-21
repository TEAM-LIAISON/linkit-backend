package liaison.linkit.team.presentation;

import jakarta.validation.Valid;
import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.team.dto.request.activity.BothActivityMethodCreateRequest;
import liaison.linkit.team.service.ActivityMethodService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/activity-method")
public class ActivityMethodController {
    private final ActivityMethodService activityMethodService;

    @PostMapping
    @MemberOnly
    public ResponseEntity<Void> createBothActivityMethod(
            @Auth final Accessor accessor,
            @RequestBody @Valid BothActivityMethodCreateRequest bothActivityMethodCreateRequest
    ) {
        activityMethodService.save(accessor.getMemberId(), bothActivityMethodCreateRequest);
        return ResponseEntity.ok().build();
    }
}
