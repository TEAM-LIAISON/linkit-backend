package liaison.linkit.team.domain.repository.announcement;

import java.util.Optional;

import liaison.linkit.team.domain.announcement.AnnouncementWorkType;

public interface AnnouncementWorkTypeCustomRepository {

    boolean existsAnnouncementWorkTypeByTeamMemberAnnouncementId(
            final Long teamMemberAnnouncementId);

    Optional<AnnouncementWorkType> findAnnouncementWorkTypeByTeamMemberAnnouncementId(
            final Long teamMemberAnnouncementId);

    void deleteByTeamMemberAnnouncementId(final Long teamMemberAnnouncementId);
}
