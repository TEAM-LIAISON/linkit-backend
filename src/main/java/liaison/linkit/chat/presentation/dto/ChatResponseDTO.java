package liaison.linkit.chat.presentation.dto;

import liaison.linkit.chat.domain.ChatRoom.ParticipantType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatLeftMenu {
        private Long chatRoomId;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateChatRoomResponse {
        private Long chatRoomId;

        private Long participantAId;
        private ParticipantType participantAType;
        private String participantAName;                // Profile인 경우 회원 이름, Team인 경우 팀 이름

        private Long participantBId;
        private ParticipantType participantBType;
        private String participantBName;                // Profile인 경우 회원 이름, Team인 경우 팀 이름

        private ChatMessage lastMessage;                // 마지막 메시지 정보
        private Long unreadCount;                       // 읽지 않은 메시지 수
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatMessage {
        private String content;         // 메시지 내용
        private String timestamp;       // 전송 시간
    }
}
