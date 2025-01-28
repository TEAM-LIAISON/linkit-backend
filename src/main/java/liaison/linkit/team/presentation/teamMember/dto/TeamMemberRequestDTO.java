package liaison.linkit.team.presentation.teamMember.dto;

import jakarta.validation.constraints.NotBlank;
import liaison.linkit.team.domain.teamMember.TeamMemberType;
import liaison.linkit.team.domain.teamMember.type.TeamMemberRegisterType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TeamMemberRequestDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddTeamMemberRequest {

        @NotBlank(message = "팀원 초대 이메일을 입력해주세요.")
        private String teamMemberInvitationEmail;

        @NotBlank(message = "팀원 초대 시 권한을 선택해주세요.")
        private TeamMemberType teamMemberType;

    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateTeamMemberTypeRequest {

        @NotBlank(message = "변경하려고하는 팀원의 권한을 선택해주세요.")
        private TeamMemberType teamMemberType;

    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RemoveTeamMemberRequest {
        @NotBlank(message = "삭제하려고하는 팀원의 등록 상태를 입력해주세요")
        private TeamMemberRegisterType teamMemberRegisterType;

        @NotBlank(message = "삭제하려고 하는 팀원의 emailId / email 중 입력해주세요")
        private String removeIdentifier;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TeamJoinRequest {
        private Boolean isTeamJoin;
    }
}
