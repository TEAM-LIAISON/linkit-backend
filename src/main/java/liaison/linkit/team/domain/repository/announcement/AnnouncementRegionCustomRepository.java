package liaison.linkit.team.domain.repository.announcement;

import java.util.Optional;
import liaison.linkit.team.domain.announcement.AnnouncementRegion;

public interface AnnouncementRegionCustomRepository {
    Optional<AnnouncementRegion> findAnnouncementRegionByTeamMemberAnnouncementId(final Long teamMemberAnnouncementId);

    boolean existsAnnouncementRegionByTeamMemberAnnouncementId(final Long teamMemberAnnouncementId);

    void deleteByTeamMemberAnnouncementId(final Long teamMemberAnnouncementId);


}
