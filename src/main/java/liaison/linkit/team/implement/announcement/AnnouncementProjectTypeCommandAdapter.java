package liaison.linkit.team.implement.announcement;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.team.domain.announcement.AnnouncementProjectType;
import liaison.linkit.team.domain.repository.announcement.AnnouncementProjectTypeRepository;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class AnnouncementProjectTypeCommandAdapter {

    private final AnnouncementProjectTypeRepository announcementProjectTypeRepository;

    public AnnouncementProjectType save(final AnnouncementProjectType announcementProjectType) {
        return announcementProjectTypeRepository.save(announcementProjectType);
    }

    public void deleteByTeamMemberAnnouncementId(final Long teamMemberAnnouncementId) {
        announcementProjectTypeRepository.deleteByTeamMemberAnnouncementId(
                teamMemberAnnouncementId);
    }
}
