package liaison.linkit.scrap.business.mapper;

import liaison.linkit.common.annotation.Mapper;
import liaison.linkit.scrap.domain.AnnouncementScrap;
import liaison.linkit.scrap.presentation.dto.announcementScrap.AnnouncementScrapResponseDTO;

@Mapper
public class AnnouncementScrapMapper {

    public AnnouncementScrapResponseDTO.AddTeamMemberAnnouncementScrap toAddTeamMemberAnnouncementScrap(
            final AnnouncementScrap announcementScrap
    ) {
        return AnnouncementScrapResponseDTO.AddTeamMemberAnnouncementScrap
                .builder()
                .createdAt(announcementScrap.getCreatedAt())
                .build();
    }

    public AnnouncementScrapResponseDTO.RemoveTeamMemberAnnouncementScrap toRemoveTeamMemberAnnouncementScrap() {
        return AnnouncementScrapResponseDTO.RemoveTeamMemberAnnouncementScrap
                .builder()
                .build();
    }
}
