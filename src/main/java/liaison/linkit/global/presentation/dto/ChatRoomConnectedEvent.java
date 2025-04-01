package liaison.linkit.global.presentation.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ChatRoomConnectedEvent { // 채팅방 입장에 대한 이벤트 발행
    private final Long memberId;
    private final Long chatRoomId;
}
