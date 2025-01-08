package liaison.linkit.matching.presentation.dto;

import java.util.ArrayList;
import java.util.List;
import liaison.linkit.matching.domain.type.MatchingStatusType;
import liaison.linkit.matching.domain.type.ReceiverDeleteStatus;
import liaison.linkit.matching.domain.type.ReceiverReadStatus;
import liaison.linkit.matching.domain.type.ReceiverType;
import liaison.linkit.matching.domain.type.SenderDeleteStatus;
import liaison.linkit.matching.domain.type.SenderType;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO.ProfilePositionDetail;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO.AnnouncementPositionItem;
import liaison.linkit.team.presentation.team.dto.TeamResponseDTO.TeamScaleItem;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MatchingResponseDTO {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SelectMatchingRequestToTeamMenu {
        private Boolean isTeamInformationExists;

        @Builder.Default
        private SenderProfileInformation senderProfileInformation = new SenderProfileInformation();

        @Builder.Default
        private List<SenderTeamInformation> senderTeamInformation = new ArrayList<>();

        @Builder.Default
        private ReceiverTeamInformation receiverTeamInformation = new ReceiverTeamInformation();
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SelectMatchingRequestToProfileMenu {
        private Boolean isTeamInformationExists;

        @Builder.Default
        private SenderProfileInformation senderProfileInformation = new SenderProfileInformation();

        @Builder.Default
        private List<SenderTeamInformation> senderTeamInformation = new ArrayList<>();

        @Builder.Default
        private ReceiverProfileInformation receiverProfileInformation = new ReceiverProfileInformation();
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReceivedMatchingMenu {
        private Long matchingId;

        private SenderType senderType;

        private ReceiverType receiverType;

        @Builder.Default
        private SenderProfileInformation senderProfileInformation = new SenderProfileInformation();

        @Builder.Default
        private SenderTeamInformation senderTeamInformation = new SenderTeamInformation();

        @Builder.Default
        private ReceiverProfileInformation receiverProfileInformation = new ReceiverProfileInformation();

        @Builder.Default
        private ReceiverTeamInformation receiverTeamInformation = new ReceiverTeamInformation();

        @Builder.Default
        private ReceiverAnnouncementInformation receiverAnnouncementInformation = new ReceiverAnnouncementInformation();

        private String requestMessage;

        private MatchingStatusType matchingStatusType;

        private ReceiverReadStatus receiverReadStatus;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateMatchingStatusTypeResponse {
        private Long matchingId;
        private MatchingStatusType matchingStatusType;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RequestedMatchingMenu {
        private Long matchingId;

        private SenderType senderType;

        private ReceiverType receiverType;

        @Builder.Default
        private SenderProfileInformation senderProfileInformation = new SenderProfileInformation();

        @Builder.Default
        private SenderTeamInformation senderTeamInformation = new SenderTeamInformation();

        @Builder.Default
        private ReceiverProfileInformation receiverProfileInformation = new ReceiverProfileInformation();

        @Builder.Default
        private ReceiverTeamInformation receiverTeamInformation = new ReceiverTeamInformation();

        @Builder.Default
        private ReceiverAnnouncementInformation receiverAnnouncementInformation = new ReceiverAnnouncementInformation();

        private String requestMessage;

        private MatchingStatusType matchingStatusType;

        private ReceiverReadStatus receiverReadStatus;
    }

    // 매칭 요청 응답
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddMatchingResponse {
        private Long matchingId;

        private SenderType senderType;
        private ReceiverType receiverType;

        @Builder.Default
        private SenderProfileInformation senderProfileInformation = new SenderProfileInformation();

        @Builder.Default
        private SenderTeamInformation senderTeamInformation = new SenderTeamInformation();

        @Builder.Default
        private ReceiverProfileInformation receiverProfileInformation = new ReceiverProfileInformation();

        @Builder.Default
        private ReceiverTeamInformation receiverTeamInformation = new ReceiverTeamInformation();

        @Builder.Default
        private ReceiverAnnouncementInformation receiverAnnouncementInformation = new ReceiverAnnouncementInformation();

        private String requestMessage;

        private MatchingStatusType matchingStatusType;

        private ReceiverReadStatus receiverReadStatus;
    }

    // 발신자 프로필 정보
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SenderProfileInformation {
        private String profileImagePath;
        private String memberName;
        private String emailId;

        @Builder.Default
        private ProfilePositionDetail profilePositionDetail = new ProfilePositionDetail();
    }

    // 발신자 팀 정보
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SenderTeamInformation {
        private String teamCode;                    // 팀 코드
        private String teamName;                    // 팀 이름
        private String teamLogoImagePath;           // 팀 로고 이미지 경로

        @Builder.Default
        private TeamScaleItem teamScaleItem = new TeamScaleItem();
    }

    // 수신자 프로필 정보
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReceiverProfileInformation {
        private String profileImagePath;
        private String memberName;
        private String emailId;

        @Builder.Default
        private ProfilePositionDetail profilePositionDetail = new ProfilePositionDetail();
    }

    // 수신자 팀 정보
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReceiverTeamInformation {
        private String teamCode;                    // 팀 코드
        private String teamName;                    // 팀 이름
        private String teamLogoImagePath;           // 팀 로고 이미지 경로

        @Builder.Default
        private TeamScaleItem teamScaleItem = new TeamScaleItem();  // 팀 규모
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReceiverAnnouncementInformation {
        private String announcementTitle;

        @Builder.Default
        private AnnouncementPositionItem announcementPositionItem = new AnnouncementPositionItem();

        @Builder.Default
        private List<TeamMemberAnnouncementResponseDTO.AnnouncementSkillName> announcementSkillNames = new ArrayList<>();
    }

    // 매칭 알림 메뉴
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MatchingNotificationMenu {
        private int receivedMatchingNotificationCount;                  // 수신함에서 매칭 성사가 안된 안읽은 요청의 개수 + 매칭 성사가 되고 나서 내가 아직 읽지 않은 요청의 개수
        private int requestedMatchingNotificationCount;                 // 발신함에서 매칭 성사가 되었는데, 안읽은 요청의 개수
    }

    // 발신함 삭제 응답
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeleteRequestedMatchingItems {
        @Builder.Default
        private List<DeleteRequestedMatchingItem> deleteRequestedMatchingItems = new ArrayList<>();
    }

    // 발신함 삭제 개별 응답
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeleteRequestedMatchingItem {
        private Long matchingId;
        private SenderDeleteStatus senderDeleteStatus;
    }

    // 수신함 삭제 응답
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeleteReceivedMatchingItems {
        @Builder.Default
        private List<DeleteReceivedMatchingItem> deleteReceivedMatchingItems = new ArrayList<>();
    }

    // 수신함 삭제 개별 응답
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeleteReceivedMatchingItem {
        private Long matchingId;
        private ReceiverDeleteStatus receiverDeleteStatus;
    }

    // 수신함에서 성사된 매칭 읽음 처리 응답
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateReceivedMatchingCompletedStateReadItems {
        @Builder.Default
        private List<UpdateReceivedMatchingCompletedStateReadItem> updateReceivedMatchingCompletedStateReadItems = new ArrayList<>();
    }

    // 수신함에서 성사된 매칭 읽음 처리 개별 응답
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateReceivedMatchingCompletedStateReadItem {
        private Long matchingId;
        private ReceiverReadStatus receiverReadStatus;
    }

    // 수신함에서 요청 온 매칭 읽음 처리 응답
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateReceivedMatchingRequestedStateToReadItems {
        @Builder.Default
        private List<UpdateReceivedMatchingRequestedStateToReadItem> updateReceivedMatchingRequestedStateToReadItems = new ArrayList<>();
    }

    // 수신함에서 요청 온 매칭 읽음 처리 개별 응답
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateReceivedMatchingRequestedStateToReadItem {
        private Long matchingId;
        private ReceiverReadStatus receiverReadStatus;
    }
}
