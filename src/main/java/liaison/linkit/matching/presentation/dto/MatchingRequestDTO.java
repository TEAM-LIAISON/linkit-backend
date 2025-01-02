package liaison.linkit.matching.presentation.dto;

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
    public static class AddMatchingRequest {
        private SenderType senderType;                  //     PROFILE, TEAM
        private ReceiverType receiverType;              //     PROFILE, TEAM, ANNOUNCEMENT

        private Long senderEmailId;
        private Long senderTeamCode;

        private Long receiverProfileId;
        private Long receiverTeamCode;
        private Long receiverAnnouncementId;

        private String requestMessage;
    }
}
