package liaison.linkit.matching.presentation.dto;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;

import liaison.linkit.matching.domain.type.MatchingStatusType;
import liaison.linkit.matching.domain.type.ReceiverType;
import liaison.linkit.matching.domain.type.SenderType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MatchingRequestDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeleteRequestedMatchingRequest {

        @NotEmpty(message = "matchingIds 리스트는 비어 있을 수 없습니다.")
        private List<Long> matchingIds;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeleteReceivedMatchingRequest {

        @NotEmpty(message = "matchingIds 리스트는 비어 있을 수 없습니다.")
        private List<Long> matchingIds;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateReceivedMatchingReadRequest {

        @NotEmpty(message = "matchingIds 리스트는 비어 있을 수 없습니다.")
        private List<Long> matchingIds;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddMatchingRequest {

        private SenderType senderType;
        private ReceiverType receiverType;

        private String senderEmailId;
        private String senderTeamCode;

        private String receiverEmailId;
        private String receiverTeamCode;
        private Long receiverAnnouncementId;

        private String requestMessage;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateMatchingStatusTypeRequest {

        private MatchingStatusType matchingStatusType;
    }
}
