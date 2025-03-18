package liaison.linkit.team.implement.announcement;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.team.domain.repository.announcement.AnnouncementWorkTypeRepository;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class AnnouncementWorkTypeQueryAdapter {

    private final AnnouncementWorkTypeRepository announcementWorkTypeRepository;

    public boolean existsAnnouncementWorkTypeByTeamMemberAnnouncementId(
            final Long teamMemberAnnouncementId) {
        return announcementWorkTypeRepository.existsAnnouncementWorkTypeByTeamMemberAnnouncementId(
                teamMemberAnnouncementId);
    }
}
