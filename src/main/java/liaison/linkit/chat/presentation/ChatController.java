package liaison.linkit.chat.presentation;

import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final ChatService chatService;

    // ==============================
    // 1) REST API: 채팅방 생성
    // ==============================
    @PostMapping("/api/v1/chat/room")
    @MemberOnly
    public CommonResponse<ChatResponseDTO.CreateChatRoomResponse> createChatRoom(
            @RequestBody CreateChatRoomRequest request,
            @Auth Accessor accessor
    ) {
        log.info("createChatRoom {}", request);
        return CommonResponse.onSuccess(chatService.createChatRoom(request, accessor.getMemberId()));
    }

    // ==============================
    // 2) STOMP: 메시지 전송
    // ==============================

    /**
     * 클라이언트가 /pub/chat/send 로 메시지를 전송하면 서버가 이 메서드를 통해 메시지를 수신하고, 내부 로직 처리 후 /sub/chat/{chatRoomId} 경로로 메시지를 브로드캐스트한다.
     */
    @MessageMapping("/chat/send")
    public void sendChatMessage(
    ) {
        log.info("Sending chat message");
        chatService.handleChatMessage();
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
