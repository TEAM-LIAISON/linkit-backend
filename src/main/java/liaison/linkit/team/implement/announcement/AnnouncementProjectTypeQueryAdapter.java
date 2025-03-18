package liaison.linkit.team.implement.announcement;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.team.domain.repository.announcement.AnnouncementProjectTypeRepository;
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
}
