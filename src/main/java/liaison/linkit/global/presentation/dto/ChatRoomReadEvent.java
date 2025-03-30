package liaison.linkit.global.presentation.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ChatRoomReadEvent {
    private final Long memberId;
    private final Long chatRoomId;
}
