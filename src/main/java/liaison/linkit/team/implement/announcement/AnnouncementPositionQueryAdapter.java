package liaison.linkit.team.implement.announcement;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.team.domain.announcement.AnnouncementPosition;
import liaison.linkit.team.domain.repository.announcement.AnnouncementPositionRepository;
import liaison.linkit.team.exception.announcement.AnnouncementRegionNotFoundException;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class AnnouncementPositionQueryAdapter {
    private final AnnouncementPositionRepository announcementPositionRepository;

    public boolean existsAnnouncementPositionByTeamMemberAnnouncementId(
            final Long teamMemberAnnouncementId) {
        return announcementPositionRepository.existsAnnouncementPositionByTeamMemberAnnouncementId(
                teamMemberAnnouncementId);
    }

    public AnnouncementPosition findAnnouncementPositionByTeamMemberAnnouncementId(
            final Long teamMemberAnnouncementId) {
        return announcementPositionRepository
                .findAnnouncementPositionByTeamMemberAnnouncementId(teamMemberAnnouncementId)
                .orElseThrow(() -> AnnouncementRegionNotFoundException.EXCEPTION);
    }
}
