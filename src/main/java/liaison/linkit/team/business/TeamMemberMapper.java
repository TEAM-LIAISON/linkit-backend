package liaison.linkit.team.business;


import java.util.List;
import java.util.stream.Collectors;
import liaison.linkit.common.annotation.Mapper;
import liaison.linkit.common.presentation.RegionResponseDTO.RegionDetail;
import liaison.linkit.global.type.TeamMemberType;
import liaison.linkit.member.domain.Member;
import liaison.linkit.profile.domain.profile.Profile;
import liaison.linkit.profile.presentation.miniProfile.dto.MiniProfileResponseDTO.ProfileCurrentStateItem;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO.ProfileTeamInform;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO.ProfilePositionDetail;
import liaison.linkit.team.domain.Team;
import liaison.linkit.team.domain.TeamMember;
import liaison.linkit.team.presentation.teamMember.dto.TeamMemberResponseDTO;
import liaison.linkit.team.presentation.teamMember.dto.TeamMemberResponseDTO.ProfileInformMenu;
import liaison.linkit.team.presentation.teamMember.dto.TeamMemberResponseDTO.TeamMemberItems;

@Mapper
public class TeamMemberMapper {
    public TeamMember toTeamMember(final Member member, final Team team) {
        return TeamMember.builder()
                .member(member)
                .team(team)
                .teamMemberType(TeamMemberType.TEAM_MANAGER)
                .build();
    }

    public TeamMemberItems toTeamMemberItems(final List<TeamMemberResponseDTO.ProfileInformMenu> profileInformMenus) {
        return TeamMemberItems
                .builder()
                .profileInformMenus(profileInformMenus)
                .build();
    }

    public ProfileInformMenu toProfileInformMenu(
            final List<ProfileCurrentStateItem> profileCurrentStateItems,
            final Profile profile,
            final ProfilePositionDetail profilePositionDetail,
            final RegionDetail regionDetail
    ) {
        return TeamMemberResponseDTO.ProfileInformMenu
                .builder()
                .profileCurrentStates(profileCurrentStateItems)
                .profileImagePath(profile.getProfileImagePath())
                .memberName(profile.getMember().getMemberBasicInform().getMemberName())
                .isProfilePublic(profile.isProfilePublic())
                .majorPosition(profilePositionDetail.getMajorPosition())
                .regionDetail(regionDetail)
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
                .teamLogoImagePath(team.getTeamLogoImagePath())
                .build();
    }
}


