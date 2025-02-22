package liaison.linkit.team.business.mapper.announcement;

import liaison.linkit.common.annotation.Mapper;
import liaison.linkit.team.domain.announcement.AnnouncementPosition;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO.AnnouncementPositionItem;

@Mapper
public class AnnouncementPositionMapper {

    public AnnouncementPositionItem toAnnouncementPositionItem(
            final AnnouncementPosition announcementPosition) {
        return AnnouncementPositionItem.builder()
                .majorPosition(announcementPosition.getPosition().getMajorPosition())
                .subPosition(announcementPosition.getPosition().getSubPosition())
                .build();
    }
}
