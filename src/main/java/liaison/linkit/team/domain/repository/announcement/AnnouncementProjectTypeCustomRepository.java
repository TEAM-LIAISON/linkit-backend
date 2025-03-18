package liaison.linkit.team.domain.repository.announcement;

public interface AnnouncementProjectTypeCustomRepository {

    boolean existsAnnouncementProjectTypeByTeamMemberAnnouncementId(
            final Long teamMemberAnnouncementId);

    void deleteByTeamMemberAnnouncementId(final Long teamMemberAnnouncementId);
}
