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
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.MatchingNotificationMenu;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.ReceivedMatchingMenu;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.RequestedMatchingMenu;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.SelectMatchingRequestToProfileMenu;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.UpdateReceivedMatchingCompletedStateReadItems;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.UpdateReceivedMatchingRequestedStateToReadItems;
import liaison.linkit.matching.service.MatchingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/matching")
@Slf4j
public class MatchingController {

    private final MatchingService matchingService;

    // 프로필 뷰어에서 매칭 요청 버튼 클릭하면 뜨는 모달 정보
    @GetMapping("/profile/{emailId}/select/request/menu")
    public CommonResponse<SelectMatchingRequestToProfileMenu> selectMatchingRequestToProfileMenu(
            @Auth final Accessor accessor,
            @PathVariable final String emailId
    ) {
        log.info("selectMatchingRequestToProfileMenu");
        return CommonResponse.onSuccess(matchingService.selectMatchingRequestToProfileMenu(accessor.getMemberId(), emailId));
    }

    // 팀 뷰어에서 매칭 요청 버튼 클릭하면 뜨는 모달 정보
    @GetMapping("/team/{teamCode}/select/request/menu")
    @MemberOnly
    public CommonResponse<MatchingResponseDTO.SelectMatchingRequestToTeamMenu> selectMatchingRequestToTeamMenu(
            @Auth final Accessor accessor,
            @PathVariable final String teamCode
    ) {
        return CommonResponse.onSuccess(matchingService.selectMatchingRequestToTeamMenu(accessor.getMemberId(), teamCode));
    }

    // 매칭 요청 보내기
    @PostMapping
    @MemberOnly
    public CommonResponse<MatchingResponseDTO.AddMatchingResponse> addMatching(
            @Auth final Accessor accessor,
            @RequestBody final MatchingRequestDTO.AddMatchingRequest addMatchingRequest
    ) {
        return CommonResponse.onSuccess(matchingService.addMatching(accessor.getMemberId(), addMatchingRequest));
    }

    // 매칭 수신함 (명세 완료)
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

    // 매칭 수신함에서 (요청온 매칭) 읽음처리 (명세 완료)
    @PostMapping("/received/menu/requested/read")
    @MemberOnly
    public CommonResponse<UpdateReceivedMatchingRequestedStateToReadItems> updateReceivedMatchingRequestedStateRead(
            @Auth final Accessor accessor,
            @RequestBody final UpdateReceivedMatchingReadRequest request
    ) {
        return CommonResponse.onSuccess(matchingService.updateReceivedMatchingRequestedStateToRead(accessor.getMemberId(), request));
    }

    // 매칭 수신함에서 (성사된 매칭) 읽음처리 (명세 완료)
    @PostMapping("/received/menu/completed/read")
    @MemberOnly
    public CommonResponse<UpdateReceivedMatchingCompletedStateReadItems> updateReceivedMatchingCompletedStateRead(
            @Auth final Accessor accessor,
            @RequestBody final UpdateReceivedMatchingReadRequest request
    ) {
        return CommonResponse.onSuccess(matchingService.updateReceivedMatchingCompletedStateToRead(accessor.getMemberId(), request));
    }

    // 매칭 수신함에서 삭제 처리 (명세 완료)
    @PostMapping("/received/menu/delete")
    @MemberOnly
    public CommonResponse<MatchingResponseDTO.DeleteReceivedMatchingItems> deleteReceivedMatchingItems(
            @Auth final Accessor accessor,
            @RequestBody final MatchingRequestDTO.DeleteReceivedMatchingRequest request
    ) {
        return CommonResponse.onSuccess(matchingService.deleteReceivedMatchingItems(accessor.getMemberId(), request));
    }

    // 매칭 수신함에서 수락하기/거절하기
    @PostMapping("/{matchingId}")
    @MemberOnly
    public CommonResponse<MatchingResponseDTO.UpdateMatchingStatusTypeResponse> updateMatchingStatusTypeResponse(
            @Auth final Accessor accessor,
            @PathVariable final Long matchingId,
            @RequestBody final MatchingRequestDTO.UpdateMatchingStatusTypeRequest updateMatchingStatusTypeRequest
    ) {
        return CommonResponse.onSuccess(matchingService.updateMatchingStatusTypeResponse(accessor.getMemberId(), matchingId, updateMatchingStatusTypeRequest));
    }

    // 매칭 발신함 (명세 완료)
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

    // 매칭 발신함에서 삭제 처리 (명세 완료)
    @PostMapping("/requested/menu/delete")
    @MemberOnly
    public CommonResponse<MatchingResponseDTO.DeleteRequestedMatchingItems> deleteRequestedMatchingItems(
            @Auth final Accessor accessor,
            @RequestBody final MatchingRequestDTO.DeleteRequestedMatchingRequest request
    ) {
        return CommonResponse.onSuccess(matchingService.deleteRequestedMatchingItems(accessor.getMemberId(), request));
    }

    // 상단 메뉴 (명세 완료)
    @GetMapping("/notification/menu")
    public CommonResponse<MatchingNotificationMenu> getMatchingNotificationMenu(
            @Auth final Accessor accessor
    ) {
        return CommonResponse.onSuccess(matchingService.getMatchingNotificationMenu(accessor.getMemberId()));
    }
}
