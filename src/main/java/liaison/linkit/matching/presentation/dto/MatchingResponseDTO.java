package liaison.linkit.matching.presentation.dto;

import liaison.linkit.matching.domain.type.ReceiverType;
import liaison.linkit.matching.domain.type.SenderType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MatchingResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MatchingMenuResponse {
        private int receivedMatchingNotificationCount;                  // 수신함에서 매칭 성사가 안된 안읽은 요청의 개수 + 매칭 성사가 되고 나서 내가 아직 읽지 않은 요청의 개수
        private int requestedMatchingNotificationCount;                 // 발신함에서 매칭 성사가 되었는데, 안읽은 요청의 개수
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddMatchingResponse {
        private SenderType senderType;
        private ReceiverType receiverType;
        private String senderEmailId;
        private String senderTeamCode;
        private String receiverEmailId;
        private String receiverTeamCode;
        private Long receiverAnnouncementId;
        private String requestMessage;
    }
}
