package liaison.linkit.matching.presentation;

import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.common.presentation.CommonResponse;
import liaison.linkit.matching.domain.type.ReceiverType;
import liaison.linkit.matching.presentation.dto.MatchingRequestDTO;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.MatchingMenu;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.MatchingReceivedMenu;
import liaison.linkit.matching.service.MatchingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/matching")
public class MatchingController {

    private final MatchingService matchingService;

    // 2. 수신함 정보

    // 3. 발신함 정보

    // 상단 메뉴
    @GetMapping("/notification/menu")
    public CommonResponse<MatchingMenu> getMatchingMenu(
            @Auth final Accessor accessor
    ) {
        return CommonResponse.onSuccess(matchingService.getMatchingMenu(accessor.getMemberId()));
    }

    // 매칭 수신함
    @GetMapping("/received/menu")
    @MemberOnly
    public CommonResponse<Page<MatchingReceivedMenu>> getMatchingReceivedMenuResponse(
            @Auth final Accessor accessor,
            @RequestParam(value = "receiverType", required = false) final ReceiverType receiverType,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size
    ) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<MatchingReceivedMenu> matchingReceivedMenus = matchingService.getMatchingReceivedMenuResponse(accessor.getMemberId(), receiverType, pageable);
        return CommonResponse.onSuccess(matchingReceivedMenus);
    }

    //    // 매칭 발신함
//    @GetMapping("/requested/menu")
    // 매칭 요청 보내기
    @PostMapping
    public CommonResponse<MatchingResponseDTO.AddMatchingResponse> addMatching(
            @Auth final Accessor accessor,
            @RequestBody final MatchingRequestDTO.AddMatchingRequest addMatchingRequest
    ) {
        return CommonResponse.onSuccess(matchingService.addMatching(accessor.getMemberId(), addMatchingRequest));
    }
}
