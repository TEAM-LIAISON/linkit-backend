package liaison.linkit.team.domain.repository.announcement;

import java.util.Optional;

import liaison.linkit.team.domain.announcement.AnnouncementPosition;

public interface AnnouncementPositionCustomRepository {
    boolean existsAnnouncementPositionByTeamMemberAnnouncementId(
            final Long teamMemberAnnouncementId);

    Optional<AnnouncementPosition> findAnnouncementPositionByTeamMemberAnnouncementId(
            final Long teamMemberAnnouncementId);

    void deleteAllByTeamMemberAnnouncementId(final Long teamMemberAnnouncementId);
}
