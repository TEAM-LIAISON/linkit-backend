package liaison.linkit.team.implement.announcement;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.team.domain.repository.announcement.AnnouncementRegionRepository;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class AnnouncementRegionQueryAdapter {
    final AnnouncementRegionRepository announcementRegionRepository;

    public boolean existsAnnouncementRegionByTeamMemberAnnouncementId(final Long teamMemberAnnouncementId) {
        return announcementRegionRepository.existsAnnouncementRegionByTeamMemberAnnouncementId(teamMemberAnnouncementId);
    }
}
