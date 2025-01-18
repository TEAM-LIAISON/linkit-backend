package liaison.linkit.team.presentation.teamMember.dto;

import java.util.ArrayList;
import java.util.List;
import liaison.linkit.common.presentation.RegionResponseDTO.RegionDetail;
import liaison.linkit.profile.presentation.miniProfile.dto.MiniProfileResponseDTO.ProfileCurrentStateItem;
import liaison.linkit.team.domain.teamMember.TeamMemberInviteState;
import liaison.linkit.team.domain.teamMember.TeamMemberType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TeamMemberResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TeamMemberViewItems {
        @Builder.Default
        private List<AcceptedTeamMemberItem> acceptedTeamMemberItems = new ArrayList<>();
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TeamMemberItems {

        @Builder.Default
        private List<AcceptedTeamMemberItem> acceptedTeamMemberItems = new ArrayList<>();

        @Builder.Default
        private List<PendingTeamMemberItem> pendingTeamMemberItems = new ArrayList<>();
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AcceptedTeamMemberItem {                // 초대 수락 완료

        private String emailId;

        private String profileImagePath;                        // 프로필 이미지 경로
        private String memberName;                              // 회원 이름
        private String majorPosition;                           // 주요 포지션

        @Builder.Default
        private RegionDetail regionDetail = new RegionDetail();

        private TeamMemberType teamMemberType;                  // 초대 요청 시 회원 타입
        private TeamMemberInviteState teamMemberInviteState;    // 초대 상태 (PENDING, REJECTED, ACCEPTED, ADMIN)
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PendingTeamMemberItem {                 // 초대 발송 완료 (수락 대기 중)
        private String teamMemberInvitationEmail;               // 초대 발송한 이메일
        private TeamMemberType teamMemberType;                  // 초대 요청 시 회원 타입
        private TeamMemberInviteState teamMemberInviteState;    // 초대 상태
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProfileInformMenu {
        @Builder.Default
        private List<ProfileCurrentStateItem> profileCurrentStates = new ArrayList<>();

        private String profileImagePath;
        private String memberName;
        private Boolean isProfilePublic;
        private String majorPosition;

        @Builder.Default
        private RegionDetail regionDetail = new RegionDetail();
    }


    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddTeamMemberResponse {
        private String invitedTeamMemberEmail;
        private String teamCode;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateTeamMemberTypeResponse {
        private String emailId;
        private TeamMemberType teamMemberType;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TeamOutResponse {
        private String teamCode;
        private String emailId;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TeamJoinResponse {
        private String teamCode;
        private String emailId;
    }


    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateManagingTeamStateResponse {
        private String teamCode;
    }
}
