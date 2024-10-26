package liaison.linkit.scrap.business.mapper;

import liaison.linkit.common.annotation.Mapper;
import liaison.linkit.scrap.domain.TeamMemberAnnouncementScrap;
import liaison.linkit.scrap.presentation.dto.teamMemberAnnouncementScrap.TeamMemberAnnouncementScrapResponseDTO;

@Mapper
public class TeamMemberAnnouncementScrapMapper {

    public TeamMemberAnnouncementScrapResponseDTO.AddTeamMemberAnnouncementScrap toAddTeamMemberAnnouncementScrap(
            final TeamMemberAnnouncementScrap teamMemberAnnouncementScrap
    ) {
        return TeamMemberAnnouncementScrapResponseDTO.AddTeamMemberAnnouncementScrap
                .builder()
                .createdAt(teamMemberAnnouncementScrap.getCreatedAt())
                .build();
    }

    public TeamMemberAnnouncementScrapResponseDTO.RemoveTeamMemberAnnouncementScrap toRemoveTeamMemberAnnouncementScrap() {
        return TeamMemberAnnouncementScrapResponseDTO.RemoveTeamMemberAnnouncementScrap
                .builder()
                .build();
    }
}
