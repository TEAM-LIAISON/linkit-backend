package liaison.linkit.scrap.business.mapper;

import liaison.linkit.common.annotation.Mapper;
import liaison.linkit.scrap.presentation.dto.announcementScrap.AnnouncementScrapResponseDTO;
import liaison.linkit.scrap.presentation.dto.announcementScrap.AnnouncementScrapResponseDTO.UpdateAnnouncementScrap;

@Mapper
public class AnnouncementScrapMapper {
    public AnnouncementScrapResponseDTO.UpdateAnnouncementScrap toUpdateAnnouncementScrap(
            final Long teamMemberAnnouncementId,
            final boolean changeScrapValue
    ) {
        return UpdateAnnouncementScrap.builder()
                .teamMemberAnnouncementId(teamMemberAnnouncementId)
                .isAnnouncementScrap(changeScrapValue)
                .build();
    }

}
