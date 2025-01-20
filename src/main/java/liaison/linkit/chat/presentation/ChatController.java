package liaison.linkit.chat.presentation;

import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.chat.presentation.dto.ChatRequestDTO.ChatMessageRequest;
import liaison.linkit.chat.presentation.dto.ChatRequestDTO.CreateChatRoomRequest;
import liaison.linkit.chat.presentation.dto.ChatResponseDTO;
import liaison.linkit.chat.service.ChatService;
import liaison.linkit.common.presentation.CommonResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
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
            @Payload ChatMessageRequest chatMessageRequest,
            @Header(name = "memberId", required = true) Long memberId,
            @Header(name = "chatRoomId", required = true) Long chatRoomId,
            Message<?> message
    ) {
        // StompHeaderAccessor로 message의 정보를 추출
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(message);
        String destination = headerAccessor.getDestination();
        log.info("Destination: {}", destination);

        // /pub/chat/send/{chatRoomId}에서 chatRoomId 추출
        chatService.handleChatMessage(chatMessageRequest, memberId, chatRoomId);
    }

    /**
     * 채팅방의 이전 메시지 내역 조회
     *
     * @param chatRoomId 채팅방 ID
     * @param pageable   페이징 정보 (size: 한 번에 가져올 메시지 수, page: 페이지 번호)
     */
    @GetMapping("/api/v1/chat/room/{chatRoomId}/messages")
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

    // ==============================
    // 4) REST API: 채팅방 왼쪽 메뉴 조회
    // ==============================
    @GetMapping("/api/v1/chat/left/menu")
    @MemberOnly
    public CommonResponse<ChatResponseDTO.ChatLeftMenu> getChatLeftMenu(
            @Auth final Accessor accessor
    ) {
        return CommonResponse.onSuccess(chatService.getChatLeftMenu(accessor.getMemberId()));
    }

    @PostMapping("/api/v1/chat/room/{chatRoomId}/leave")
    @MemberOnly
    public CommonResponse<ChatResponseDTO.ChatRoomLeaveResponse> leaveChatRoom(
            @Auth final Accessor accessor,
            @PathVariable final Long chatRoomId
    ) {
        return CommonResponse.onSuccess(chatService.leaveChatRoom(accessor.getMemberId(), chatRoomId));
    }
}
