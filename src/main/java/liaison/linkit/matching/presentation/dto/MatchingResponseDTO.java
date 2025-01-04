package liaison.linkit.matching.presentation.dto;

import java.util.ArrayList;
import java.util.List;
import liaison.linkit.matching.domain.type.MatchingStatusType;
import liaison.linkit.matching.domain.type.ReceiverDeleteStatus;
import liaison.linkit.matching.domain.type.ReceiverReadStatus;
import liaison.linkit.matching.domain.type.ReceiverType;
import liaison.linkit.matching.domain.type.SenderDeleteStatus;
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
    public static class DeleteRequestedMatchingItems {
        @Builder.Default
        private List<DeleteRequestedMatchingItem> deleteRequestedMatchingItems = new ArrayList<>();
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeleteRequestedMatchingItem {
        private Long matchingId;
        private SenderDeleteStatus senderDeleteStatus;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeleteReceivedMatchingItems {
        @Builder.Default
        private List<DeleteReceivedMatchingItem> deleteReceivedMatchingItems = new ArrayList<>();
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeleteReceivedMatchingItem {
        private Long matchingId;
        private ReceiverDeleteStatus receiverDeleteStatus;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateReceivedMatchingCompletedStateReadItems {
        @Builder.Default
        private List<UpdateReceivedMatchingCompletedStateReadItem> updateReceivedMatchingCompletedStateReadItems = new ArrayList<>();
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateReceivedMatchingCompletedStateReadItem {
        private Long matchingId;
        private ReceiverReadStatus receiverReadStatus;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateReceivedMatchingRequestedStateToReadItems {
        @Builder.Default
        private List<UpdateReceivedMatchingRequestedStateToReadItem> updateReceivedMatchingRequestedStateToReadItems = new ArrayList<>();
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateReceivedMatchingRequestedStateToReadItem {
        private Long matchingId;
        private ReceiverReadStatus receiverReadStatus;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReceivedMatchingMenu {

        // 부가적인 사용자 정보 (이름, 로고 이미지 경로) 등은 추가 필요

        private Long matchingId;

        private SenderType senderType;
        private ReceiverType receiverType;

        private String senderEmailId;
        private String senderTeamCode;

        private String receiverEmailId;
        private String receiverTeamCode;
        private Long receiverAnnouncementId;

        private String requestMessage;

        private MatchingStatusType matchingStatusType;

        private ReceiverReadStatus receiverReadStatus;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RequestedMatchingMenu {
        private Long matchingId;

        private SenderType senderType;
        private ReceiverType receiverType;

        private String senderEmailId;
        private String senderTeamCode;

        private String receiverEmailId;
        private String receiverTeamCode;
        private Long receiverAnnouncementId;

        private String requestMessage;

        private MatchingStatusType matchingStatusType;

        private ReceiverReadStatus receiverReadStatus;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MatchingMenu {
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
