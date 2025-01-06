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

//    @MessageMapping("/chat/message")
//    public void handleChatMessage(@Payload ChatMessageRequest messageRequest,
//                                  @Header("Authorization") String token,
//                                  @Auth Accessor accessor) {
//        chatService.sendMessage(messageRequest, accessor.getMemberId());
//    }

    // 새로운 채팅방 생성
    @PostMapping("/room")
    @MemberOnly
    public CommonResponse<ChatResponseDTO.CreateChatRoomResponse> createChatRoom(
            @RequestBody CreateChatRoomRequest request,
            @Auth Accessor accessor
    ) {
        return CommonResponse.onSuccess(chatService.createChatRoom(request, accessor.getMemberId()));
    }

//    @GetMapping("/rooms")
//    @MemberOnly
//    public CommonResponse<List<ChatRoomSummary>> getChatRooms(
//            @Auth Accessor accessor) {
//        return CommonResponse.onSuccess(
//                chatService.getChatRooms(accessor.getMemberId())
//        );
//    }
//
//    @GetMapping("/rooms/{roomId}/messages")
//    @MemberOnly
//    public CommonResponse<List<ChatMessage>> getChatMessages(
//            @PathVariable Long roomId,
//            @Auth Accessor accessor) {
//        return CommonResponse.onSuccess(
//                chatService.getChatMessages(roomId, accessor.getMemberId())
//        );
//    }
}
