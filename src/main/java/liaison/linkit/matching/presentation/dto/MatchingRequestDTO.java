package liaison.linkit.matching.presentation.dto;

import java.util.List;
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
        private List<Long> matchingIds;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeleteReceivedMatchingRequest {
        private List<Long> matchingIds;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateReceivedMatchingReadRequest {
        private List<Long> matchingIds;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddMatchingRequest {
        private SenderType senderType;                  //     PROFILE, TEAM
        private ReceiverType receiverType;              //     PROFILE, TEAM, ANNOUNCEMENT

        private Long senderEmailId;                     //     발신자 유저 아이디
        private Long senderTeamCode;                    //     발신자 팀 아이디

        private Long receiverEmailId;                   //     수신자 유저 아이디
        private Long receiverTeamCode;                  //     수신자 팀 아이디
        private Long receiverAnnouncementId;            //     수신자 공고 아이디

        private String requestMessage;                  //     매칭 요청 메시지
    }
}
