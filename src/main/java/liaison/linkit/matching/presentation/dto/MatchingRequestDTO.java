package liaison.linkit.matching.presentation.dto;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;
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

        private SenderType senderType;                  //     PROFILE, TEAM
        private ReceiverType receiverType;              //     PROFILE, TEAM, ANNOUNCEMENT

        private String senderEmailId;                     //     발신자 유저 아이디
        private String senderTeamCode;                    //     발신자 팀 아이디

        private String receiverEmailId;                   //     수신자 유저 아이디
        private String receiverTeamCode;                  //     수신자 팀 아이디
        private Long receiverAnnouncementId;            //     수신자 공고 아이디

        private String requestMessage;                  //     매칭 요청 메시지
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateMatchingStatusTypeRequest {

        @NotEmpty(message = "matchingStatusType 값은 비어 있을 수 없습니다.")
        private MatchingStatusType matchingStatusType;
    }
}
