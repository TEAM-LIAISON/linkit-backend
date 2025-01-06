package liaison.linkit.chat.presentation;

import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.chat.presentation.dto.ChatRequestDTO;
import liaison.linkit.chat.presentation.dto.ChatRequestDTO.CreateChatRoomRequest;
import liaison.linkit.chat.presentation.dto.ChatResponseDTO;
import liaison.linkit.chat.service.ChatService;
import liaison.linkit.common.presentation.CommonResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chat")
@Slf4j
public class ChatController {

    private final ChatService chatService;

    // 기존 REST API 채팅방 생성
    @PostMapping("/room")
    @MemberOnly
    public CommonResponse<ChatResponseDTO.CreateChatRoomResponse> createChatRoom(
            @RequestBody CreateChatRoomRequest request,
            @Auth Accessor accessor
    ) {
        return CommonResponse.onSuccess(chatService.createChatRoom(request, accessor.getMemberId()));
    }


    // 웹소켓 메시지 처리
    @MessageMapping("/chat/message")
    public void message(
            @Auth Accessor accessor,
            final ChatRequestDTO.ChatMessageRequest chatMessageRequest
    ) {
        // 메시지 저장 및 발송
        chatService.handleChatMessage(chatMessageRequest, accessor.getMemberId());
    }

    /**
     * 채팅방의 이전 메시지 내역 조회
     *
     * @param chatRoomId 채팅방 ID
     * @param pageable   페이징 정보 (size: 한 번에 가져올 메시지 수, page: 페이지 번호)
     */
    @GetMapping("/room/{chatRoomId}/messages")
    @MemberOnly
    public CommonResponse<ChatResponseDTO.ChatMessageHistoryResponse> getChatMessages(
            @PathVariable final Long chatRoomId,
            @Auth final Accessor accessor,
            @PageableDefault(size = 50, sort = "timestamp", direction = Sort.Direction.DESC) final Pageable pageable
    ) {
        return CommonResponse.onSuccess(
                chatService.getChatMessages(chatRoomId, accessor.getMemberId(), pageable)
        );
    }
}
