package liaison.linkit.team.business.mapper.teamMember;


import java.util.List;
import java.util.stream.Collectors;
import liaison.linkit.common.annotation.Mapper;
import liaison.linkit.common.presentation.RegionResponseDTO.RegionDetail;
import liaison.linkit.member.domain.Member;
import liaison.linkit.profile.domain.profile.Profile;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO.ProfilePositionDetail;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO.ProfileTeamInform;
import liaison.linkit.team.domain.team.Team;
import liaison.linkit.team.domain.teamMember.TeamMember;
import liaison.linkit.team.domain.teamMember.TeamMemberInvitation;
import liaison.linkit.team.domain.teamMember.TeamMemberInviteState;
import liaison.linkit.team.domain.teamMember.TeamMemberType;
import liaison.linkit.team.domain.teamMember.type.TeamMemberManagingTeamState;
import liaison.linkit.team.presentation.teamMember.dto.TeamMemberResponseDTO;
import liaison.linkit.team.presentation.teamMember.dto.TeamMemberResponseDTO.AcceptedTeamMemberItem;
import liaison.linkit.team.presentation.teamMember.dto.TeamMemberResponseDTO.AddTeamMemberResponse;
import liaison.linkit.team.presentation.teamMember.dto.TeamMemberResponseDTO.TeamJoinResponse;
import liaison.linkit.team.presentation.teamMember.dto.TeamMemberResponseDTO.TeamMemberViewItems;
import liaison.linkit.team.presentation.teamMember.dto.TeamMemberResponseDTO.TeamOutResponse;
import liaison.linkit.team.presentation.teamMember.dto.TeamMemberResponseDTO.UpdateTeamMemberTypeResponse;

@Mapper
public class TeamMemberMapper {

    // 팀원 객체 생성 빌더
    public TeamMember toTeamMember(final Member member, final Team team, final TeamMemberType teamMemberType) {
        return TeamMember.builder()
                .member(member)
                .team(team)
                .teamMemberType(teamMemberType)
                .teamMemberManagingTeamState(TeamMemberManagingTeamState.ACTIVE)
                .build();
    }

    public TeamMemberViewItems toTeamMemberItems(final List<AcceptedTeamMemberItem> acceptedTeamMemberItems) {
        return TeamMemberViewItems
                .builder()
                .acceptedTeamMemberItems(acceptedTeamMemberItems)
                .build();
    }

    public List<ProfileTeamInform> toProfileTeamInforms(final List<Team> teams) {
        return teams.stream()
                .map(this::toProfileTeamInform)
                .collect(Collectors.toList());
    }

    public ProfileTeamInform toProfileTeamInform(final Team team) {
        return ProfileTeamInform.builder()
                .teamName(team.getTeamName())
                .teamCode(team.getTeamCode())
                .teamLogoImagePath(team.getTeamLogoImagePath())
                .build();
    }

    public TeamMemberResponseDTO.AddTeamMemberResponse toAddTeamMemberInvitation(
            final TeamMemberInvitation teamMemberInvitation
    ) {
        return AddTeamMemberResponse.builder()
                .invitedTeamMemberEmail(teamMemberInvitation.getTeamMemberInvitationEmail())
                .teamCode(teamMemberInvitation.getTeam().getTeamCode())
                .build();
    }

    public TeamMemberResponseDTO.UpdateTeamMemberTypeResponse toUpdateTeamMemberTypeResponse(
            final TeamMember teamMember
    ) {
        return UpdateTeamMemberTypeResponse.builder()
                .emailId(teamMember.getMember().getEmailId())
                .teamMemberType(teamMember.getTeamMemberType())
                .build();
    }

    public AcceptedTeamMemberItem toAcceptedTeamMemberItem(
            final Profile profile,
            final ProfilePositionDetail profilePositionDetail,
            final RegionDetail regionDetail,
            final TeamMember teamMember,
            final TeamMemberInviteState teamMemberInviteState
    ) {
        return AcceptedTeamMemberItem.builder()
                .emailId(profile.getMember().getEmailId())
                .profileImagePath(profile.getProfileImagePath())
                .memberName(profile.getMember().getMemberBasicInform().getMemberName())
                .majorPosition(profilePositionDetail.getMajorPosition())
                .regionDetail(regionDetail)
                .teamMemberType(teamMember.getTeamMemberType())
                .teamMemberInviteState(teamMemberInviteState)
                .build();
    }

    public TeamMemberResponseDTO.TeamOutResponse toTeamOutResponse(final String teamCode, final String emailId) {
        return TeamOutResponse.builder()
                .teamCode(teamCode)
                .emailId(emailId)
                .build();
    }

    public TeamMemberResponseDTO.TeamJoinResponse toTeamJoinResponse(final String teamCode, final String emailId) {
        return TeamJoinResponse.builder()
                .teamCode(teamCode)
                .emailId(emailId)
                .build();
    }
}


