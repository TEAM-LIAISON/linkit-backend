package liaison.linkit.team.implement.announcement;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.team.domain.announcement.AnnouncementWorkType;
import liaison.linkit.team.domain.repository.announcement.AnnouncementWorkTypeRepository;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class AnnouncementWorkTypeCommandAdapter {
    private final AnnouncementWorkTypeRepository announcementWorkTypeRepository;

    public AnnouncementWorkType save(final AnnouncementWorkType announcementWorkType) {
        return announcementWorkTypeRepository.save(announcementWorkType);
    }
}
