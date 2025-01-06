package liaison.linkit.chat.presentation.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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

        private String participantAId;
        private ParticipantType participantAType;
        private String participantAName;                // Profile인 경우 회원 이름, Team인 경우 팀 이름

        private String participantBId;
        private ParticipantType participantBType;
        private String participantBName;                // Profile인 경우 회원 이름, Team인 경우 팀 이름

        private String lastMessage;                // 마지막 메시지 정보
        private String lastMessageTime;
        private Long unreadCount;                       // 읽지 않은 메시지 수
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatMessageHistoryResponse {
        private Long totalElements;      // 전체 메시지 수
        private Integer totalPages;      // 전체 페이지 수
        private Boolean hasNext;         // 다음 페이지 존재 여부

        @Builder.Default
        private List<ChatMessageResponse> messages = new ArrayList<>();
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatMessageResponse {
        private String messageId;               // 메시지 ID
        private Long chatRoomId;                // 채팅방 ID
        private Long senderMemberId;            // 발신자 회원 ID
        private String content;                 // 메시지 내용
        private LocalDateTime timestamp;        // 전송 시간
        private boolean isRead;                 // 읽음 여부
    }
}
