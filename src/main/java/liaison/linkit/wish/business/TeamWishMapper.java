package liaison.linkit.wish.business;

import liaison.linkit.common.annotation.Mapper;
import liaison.linkit.member.domain.Member;
import liaison.linkit.team.domain.announcement.TeamMemberAnnouncement;
import liaison.linkit.wish.domain.TeamWish;
import liaison.linkit.wish.presentation.dto.teamWish.TeamWishResponseDTO;

@Mapper
public class TeamWishMapper {
    public TeamWish toTeam(final TeamMemberAnnouncement teamMemberAnnouncement, final Member member) {
        return TeamWish.builder().teamMemberAnnouncement(teamMemberAnnouncement).member(member).build();
    }

    public TeamWishResponseDTO.AddTeamWish toAddTeamWish() {
        return TeamWishResponseDTO.AddTeamWish.builder().build();
    }

    public TeamWishResponseDTO.RemoveTeamWish toRemoveTeamWish() {
        return TeamWishResponseDTO.RemoveTeamWish.builder()
                .build();
    }
}
