package liaison.linkit.scrap.business.mapper;

import java.util.List;
import liaison.linkit.common.annotation.Mapper;
import liaison.linkit.scrap.presentation.dto.announcementScrap.AnnouncementScrapResponseDTO;
import liaison.linkit.scrap.presentation.dto.announcementScrap.AnnouncementScrapResponseDTO.UpdateAnnouncementScrap;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO.AnnouncementInformMenu;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO.AnnouncementInformMenus;

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

    public AnnouncementInformMenus toAnnouncementInformMenus(final List<AnnouncementInformMenu> announcementInformMenus) {
        return AnnouncementInformMenus.builder()
                .announcementInformMenus(announcementInformMenus)
                .build();
    }

}
