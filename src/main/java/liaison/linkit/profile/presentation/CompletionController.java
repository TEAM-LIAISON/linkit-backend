package liaison.linkit.profile.presentation;

import liaison.linkit.profile.service.CompletionService;
import liaison.linkit.profile.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/completion")
public class CompletionController {
    private final CompletionService completionService;
    private final ProfileService profileService;

//    // 사용자의 프로필 완성도와 관련된 값을 조회한다.
//    // 조회 로직만 필요하다.
//    @GetMapping
//    @MemberOnly
//    public ResponseEntity<CompletionResponse> getCompletion(
//            @Auth final Accessor accessor
//    ) {
//        profileService.validateProfileByMember(accessor.getMemberId());
//        final CompletionResponse completionResponse = completionService.getCompletion(accessor.getMemberId());
//        return ResponseEntity.ok().body(completionResponse);
//    }
}
