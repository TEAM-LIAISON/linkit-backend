package liaison.linkit.team.business.mapper.announcement;

import liaison.linkit.common.annotation.Mapper;
import liaison.linkit.team.domain.announcement.AnnouncementProjectType;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO;

@Mapper
public class AnnouncementProjectTypeMapper {
    public TeamMemberAnnouncementResponseDTO.AnnouncementProjectTypeItem
            toAnnouncementProjectTypeItem(final AnnouncementProjectType announcementProjectType) {
        return TeamMemberAnnouncementResponseDTO.AnnouncementProjectTypeItem.builder()
                .announcementProjectTypeName(
                        announcementProjectType.getProjectType().getProjectTypeName())
                .build();
    }
}
