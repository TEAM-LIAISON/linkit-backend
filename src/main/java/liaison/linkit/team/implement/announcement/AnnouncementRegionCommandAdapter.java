package liaison.linkit.team.implement.announcement;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.team.domain.announcement.AnnouncementRegion;
import liaison.linkit.team.domain.repository.announcement.AnnouncementRegionRepository;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class AnnouncementRegionCommandAdapter {
    final AnnouncementRegionRepository announcementRegionRepository;

    public void deleteByTeamMemberAnnouncementId(final Long teamMemberAnnouncementId) {
        announcementRegionRepository.deleteByTeamMemberAnnouncementId(teamMemberAnnouncementId);
    }

    public void save(final AnnouncementRegion announcementRegion) {
        announcementRegionRepository.save(announcementRegion);
    }
}
