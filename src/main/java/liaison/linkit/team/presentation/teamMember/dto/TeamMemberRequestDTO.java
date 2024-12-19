package liaison.linkit.team.presentation.teamMember.dto;

import liaison.linkit.team.domain.teamMember.TeamMemberType;
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
        private String teamMemberInvitationEmail;   // 팀원 초대 이메일
        private TeamMemberType teamMemberType;      // 팀원 권한
    }
}
