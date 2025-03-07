package liaison.linkit.matching.presentation;

import java.io.UnsupportedEncodingException;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;

import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.common.presentation.CommonResponse;
import liaison.linkit.global.config.log.Logging;
import liaison.linkit.matching.business.service.MatchingService;
import liaison.linkit.matching.business.service.ReceiveMatchingService;
import liaison.linkit.matching.business.service.SendMatchingService;
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

    // 매칭 요청 발신 관리 서비스 계층
    private final SendMatchingService sendMatchingService;
    private final ReceiveMatchingService receiveMatchingService;

    // 프로필 뷰어에서 매칭 요청 버튼 클릭하면 뜨는 모달 정보
    @GetMapping("/profile/{emailId}/select/request/menu")
    @MemberOnly
    @Logging(item = "Matching", action = "GET_SELECT_MATCHING_REQUEST_TO_PROFILE_MENU")
    public CommonResponse<SelectMatchingRequestToProfileMenu> selectMatchingRequestToProfileMenu(
            @Auth final Accessor accessor, @PathVariable final String emailId) {
        return CommonResponse.onSuccess(
                sendMatchingService.selectMatchingRequestToProfileMenu(
                        accessor.getMemberId(), emailId));
    }

    // 팀 뷰어에서 매칭 요청 버튼 클릭하면 뜨는 모달 정보
    @GetMapping("/team/{teamCode}/select/request/menu")
    @MemberOnly
    @Logging(item = "Matching", action = "GET_SELECT_MATCHING_REQUEST_TO_TEAM_MENU")
    public CommonResponse<MatchingResponseDTO.SelectMatchingRequestToTeamMenu>
            selectMatchingRequestToTeamMenu(
                    @Auth final Accessor accessor, @PathVariable final String teamCode) {
        return CommonResponse.onSuccess(
                sendMatchingService.selectMatchingRequestToTeamMenu(
                        accessor.getMemberId(), teamCode));
    }

    // 매칭 요청 보내기 (명세 완료)
    @PostMapping
    @MemberOnly
    @Logging(item = "Matching", action = "POST_ADD_MATCHING")
    public CommonResponse<MatchingResponseDTO.AddMatchingResponse> addMatching(
            @Auth final Accessor accessor,
            @RequestBody final MatchingRequestDTO.AddMatchingRequest addMatchingRequest)
            throws MessagingException, UnsupportedEncodingException {
        return CommonResponse.onSuccess(
                sendMatchingService.addMatching(accessor.getMemberId(), addMatchingRequest));
    }

    // 매칭 수신함 (명세 완료)
    @GetMapping("/received/menu")
    @MemberOnly
    @Logging(item = "Matching", action = "GET_RECEIVED_MATCHING_MENU")
    public CommonResponse<Page<ReceivedMatchingMenu>> getReceivedMatchingMenu(
            @Auth final Accessor accessor,
            @RequestParam(value = "receiverType", required = false) final ReceiverType receiverType,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<ReceivedMatchingMenu> matchingReceivedMenus =
                receiveMatchingService.getReceivedMatchingMenuResponse(
                        accessor.getMemberId(), receiverType, pageable);
        return CommonResponse.onSuccess(matchingReceivedMenus);
    }

    // 매칭 수신함에서 읽음처리 (명세 완료)
    @PostMapping("/received/menu/read")
    @MemberOnly
    @Logging(item = "Matching", action = "POST_UPDATE_RECEIVED_MATCHING_STATE_READ")
    public CommonResponse<UpdateReceivedMatchingCompletedStateReadItems>
            updateReceivedMatchingStateRead(
                    @Valid @RequestBody final UpdateReceivedMatchingReadRequest request) {
        return CommonResponse.onSuccess(
                receiveMatchingService.updateReceivedMatchingStateToRead(request));
    }

    // 매칭 수신함에서 삭제 처리 (명세 완료)
    @PostMapping("/received/menu/delete")
    @MemberOnly
    @Logging(item = "Matching", action = "POST_DELETE_RECEIVED_MATCHING_ITEMS")
    public CommonResponse<MatchingResponseDTO.DeleteReceivedMatchingItems>
            deleteReceivedMatchingItems(
                    @Valid @RequestBody
                            final MatchingRequestDTO.DeleteReceivedMatchingRequest req) {
        return CommonResponse.onSuccess(receiveMatchingService.deleteReceivedMatchingItems(req));
    }

    // 매칭 수신함에서 수락하기/거절하기
    @PostMapping("/{matchingId}")
    @MemberOnly
    @Logging(item = "Matching", action = "POST_UPDATE_MATCHING_STATUS_TYPE")
    public CommonResponse<MatchingResponseDTO.UpdateMatchingStatusTypeResponse>
            updateMatchingStatusTypeResponse(
                    @Auth final Accessor accessor,
                    @PathVariable final Long matchingId,
                    @Valid @RequestBody
                            final MatchingRequestDTO.UpdateMatchingStatusTypeRequest
                                    updateMatchingStatusTypeRequest)
                    throws MessagingException, UnsupportedEncodingException {
        return CommonResponse.onSuccess(
                receiveMatchingService.updateMatchingStatusType(
                        accessor.getMemberId(), matchingId, updateMatchingStatusTypeRequest));
    }

    // 매칭 발신함 (명세 완료)
    @GetMapping("/requested/menu")
    @MemberOnly
    @Logging(item = "Matching", action = "GET_REQUESTED_MATCHING_MENU")
    public CommonResponse<Page<RequestedMatchingMenu>> getRequestedMatchingMenu(
            @Auth final Accessor accessor,
            @RequestParam(value = "senderType", required = false) final SenderType senderType,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<RequestedMatchingMenu> requestedMatchingMenus =
                sendMatchingService.getRequestedMatchingMenuResponse(
                        accessor.getMemberId(), senderType, pageable);
        return CommonResponse.onSuccess(requestedMatchingMenus);
    }

    // 매칭 발신함에서 삭제 처리
    @PostMapping("/requested/menu/delete")
    @MemberOnly
    @Logging(item = "Matching", action = "POST_DELETE_REQUESTED_MATCHING_ITEMS")
    public CommonResponse<MatchingResponseDTO.DeleteRequestedMatchingItems>
            deleteRequestedMatchingItems(
                    @Valid @RequestBody
                            final MatchingRequestDTO.DeleteRequestedMatchingRequest req) {
        return CommonResponse.onSuccess(sendMatchingService.deleteRequestedMatchingItems(req));
    }

    // 상단 메뉴 (명세 완료)
    @GetMapping("/notification/menu")
    @Logging(item = "Matching", action = "GET_MATCHING_NOTIFICATION_MENU")
    public CommonResponse<MatchingNotificationMenu> getMatchingNotificationMenu(
            @Auth final Accessor accessor) {
        return CommonResponse.onSuccess(
                matchingService.getMatchingNotificationMenu(accessor.getMemberId()));
    }
}
