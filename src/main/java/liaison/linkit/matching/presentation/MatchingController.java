package liaison.linkit.matching.presentation;

import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.common.presentation.CommonResponse;
import liaison.linkit.matching.domain.type.ReceiverType;
import liaison.linkit.matching.domain.type.SenderType;
import liaison.linkit.matching.presentation.dto.MatchingRequestDTO;
import liaison.linkit.matching.presentation.dto.MatchingRequestDTO.UpdateReceivedMatchingReadRequest;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.MatchingMenu;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.ReceivedMatchingMenu;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.RequestedMatchingMenu;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.UpdateReceivedMatchingCompletedStateReadItems;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.UpdateReceivedMatchingRequestedStateToReadItems;
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
        return CommonResponse.onSuccess(matchingService.getMatchingNotificationMenu(accessor.getMemberId()));
    }

    // 매칭 수신함
    @GetMapping("/received/menu")
    @MemberOnly
    public CommonResponse<Page<ReceivedMatchingMenu>> getReceivedMatchingMenu(
            @Auth final Accessor accessor,
            @RequestParam(value = "receiverType", required = false) final ReceiverType receiverType,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<ReceivedMatchingMenu> matchingReceivedMenus = matchingService.getReceivedMatchingMenuResponse(accessor.getMemberId(), receiverType, pageable);
        return CommonResponse.onSuccess(matchingReceivedMenus);
    }

    // 매칭 수신함에서 (요청온 매칭) 읽음처리
    @GetMapping("/received/menu/requested/read")
    @MemberOnly
    public CommonResponse<UpdateReceivedMatchingRequestedStateToReadItems> updateReceivedMatchingRequestedStateRead(
            @Auth final Accessor accessor,
            @RequestBody final UpdateReceivedMatchingReadRequest request
    ) {
        return CommonResponse.onSuccess(matchingService.updateReceivedMatchingRequestedStateToRead(accessor.getMemberId(), request));
    }

    // 매칭 수신함에서 (성사된 매칭) 읽음처리
    @GetMapping("/received/menu/completed/read")
    @MemberOnly
    public CommonResponse<UpdateReceivedMatchingCompletedStateReadItems> updateReceivedMatchingCompletedStateRead(
            @Auth final Accessor accessor,
            @RequestBody final UpdateReceivedMatchingReadRequest request
    ) {
        return CommonResponse.onSuccess(matchingService.updateReceivedMatchingCompletedStateToRead(accessor.getMemberId(), request));
    }


    // 매칭 발신함
    @GetMapping("/requested/menu")
    @MemberOnly
    public CommonResponse<Page<RequestedMatchingMenu>> getRequestedMatchingMenuResponse(
            @Auth final Accessor accessor,
            @RequestParam(value = "senderType", required = false) final SenderType senderType,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<RequestedMatchingMenu> requestedMatchingMenus = matchingService.getRequestedMatchingMenuResponse(accessor.getMemberId(), senderType, pageable);
        return CommonResponse.onSuccess(requestedMatchingMenus);
    }


    // 매칭 요청 보내기
    @PostMapping
    public CommonResponse<MatchingResponseDTO.AddMatchingResponse> addMatching(
            @Auth final Accessor accessor,
            @RequestBody final MatchingRequestDTO.AddMatchingRequest addMatchingRequest
    ) {
        return CommonResponse.onSuccess(matchingService.addMatching(accessor.getMemberId(), addMatchingRequest));
    }
}
