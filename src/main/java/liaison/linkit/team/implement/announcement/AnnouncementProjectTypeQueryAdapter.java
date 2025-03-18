package liaison.linkit.team.implement.announcement;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.team.domain.announcement.AnnouncementProjectType;
import liaison.linkit.team.domain.repository.announcement.AnnouncementProjectTypeRepository;
import liaison.linkit.team.exception.announcement.AnnouncementProjectTypeNotFoundException;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class AnnouncementProjectTypeQueryAdapter {
    private final AnnouncementProjectTypeRepository announcementProjectTypeRepository;

    public boolean existsAnnouncementProjectTypeByTeamMemberAnnouncementId(
            final Long teamMemberAnnouncementId) {
        return announcementProjectTypeRepository
                .existsAnnouncementProjectTypeByTeamMemberAnnouncementId(teamMemberAnnouncementId);
    }

    public AnnouncementProjectType findAnnouncementProjectTypeByTeamMemberAnnouncementId(
            final Long teamMemberAnnouncementId) {
        return announcementProjectTypeRepository
                .findAnnouncementProjectTypeByTeamMemberAnnouncementId(teamMemberAnnouncementId)
                .orElseThrow(() -> AnnouncementProjectTypeNotFoundException.EXCEPTION);
    }
}
