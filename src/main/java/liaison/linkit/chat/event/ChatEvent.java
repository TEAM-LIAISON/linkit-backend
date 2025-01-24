package liaison.linkit.chat.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

public abstract class ChatEvent {

    @Getter
    @AllArgsConstructor
    public static class ChatRoomConnectedEvent {
        private final Long memberId;
        private final Long chatRoomId;
    }

    @Getter
    @AllArgsConstructor
    public static class UserConnectedEvent {
        private final Long memberId;
        private final String sessionId;
    }

    @Getter
    @AllArgsConstructor
    public static class UserDisconnectedEvent {
        private final Long memberId;
        private final String sessionId;
    }
}
