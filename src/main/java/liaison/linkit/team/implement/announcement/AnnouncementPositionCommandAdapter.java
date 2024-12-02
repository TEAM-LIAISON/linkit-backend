package liaison.linkit.team.implement.announcement;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.team.domain.announcement.AnnouncementPosition;
import liaison.linkit.team.domain.repository.announcement.AnnouncementPositionRepository;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class AnnouncementPositionCommandAdapter {
    private final AnnouncementPositionRepository announcementPositionRepository;

    public void deleteAllByTeamMemberAnnouncementId(final Long teamMemberAnnouncementId) {
        announcementPositionRepository.deleteAllByTeamMemberAnnouncementId(teamMemberAnnouncementId);
    }

    public AnnouncementPosition save(final AnnouncementPosition announcementPosition) {
        return announcementPositionRepository.save(announcementPosition);
    }
}
