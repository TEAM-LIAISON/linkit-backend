package liaison.linkit.team.domain.repository.announcement;

import java.util.Optional;

import liaison.linkit.team.domain.announcement.AnnouncementProjectType;

public interface AnnouncementProjectTypeCustomRepository {

    boolean existsAnnouncementProjectTypeByTeamMemberAnnouncementId(
            final Long teamMemberAnnouncementId);

    Optional<AnnouncementProjectType> findAnnouncementProjectTypeByTeamMemberAnnouncementId(
            final Long teamMemberAnnouncementId);

    void deleteByTeamMemberAnnouncementId(final Long teamMemberAnnouncementId);
}
