package liaison.linkit.global.presentation.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ChatListConnectedEvent { // 채팅 목록 구독에 대한 이벤트 발행
    private final Long memberId;
}
