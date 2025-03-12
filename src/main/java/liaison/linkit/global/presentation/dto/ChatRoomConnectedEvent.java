package liaison.linkit.global.presentation.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ChatRoomConnectedEvent {
    private final Long memberId;
    private final Long chatRoomId;
}
