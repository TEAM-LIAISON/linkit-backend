package liaison.linkit.team.business.mapper.announcement;

import liaison.linkit.common.annotation.Mapper;
import liaison.linkit.team.domain.announcement.AnnouncementWorkType;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO;

@Mapper
public class AnnouncementWorkTypeMapper {
    public TeamMemberAnnouncementResponseDTO.AnnouncementWorkTypeItem toAnnouncementWorkTypeItem(
            final AnnouncementWorkType announcementWorkType) {
        return TeamMemberAnnouncementResponseDTO.AnnouncementWorkTypeItem.builder()
                .announcementWorkTypeName(announcementWorkType.getWorkType().getWorkTypeName())
                .build();
    }
}
