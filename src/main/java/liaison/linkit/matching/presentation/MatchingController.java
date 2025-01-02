package liaison.linkit.matching.presentation;

import liaison.linkit.auth.Auth;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.common.presentation.CommonResponse;
import liaison.linkit.matching.presentation.dto.MatchingRequestDTO;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO;
import liaison.linkit.matching.service.MatchingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/matching")
public class MatchingController {

    private final MatchingService matchingService;

    // 1. 상단 메뉴 (읽지 않은 알림들에 대한 개수 정보 필요)

    // 2. 수신함 정보

    // 3. 발신함 정보

    // 4. 스크랩 정보 (구현 완료) -> 스크랩 디렉토리에 존재

//    @GetMapping("/menu")
//    public CommonResponse<MatchingResponseDTO.MatchingMenuResponse> getMatchingMenu(
//            @Auth final Accessor accessor
//    ) {
//        return CommonResponse.onSuccess(matchingService.getMatchingMenu(accessor.getMemberId()));
//    }

    @PostMapping
    public CommonResponse<MatchingResponseDTO.AddMatchingResponse> addMatching(
            @Auth final Accessor accessor,
            @RequestBody final MatchingRequestDTO.AddMatchingRequest addMatchingRequest
    ) {
        return CommonResponse.onSuccess(matchingService.addMatching(accessor.getMemberId(), addMatchingRequest));
    }
}
