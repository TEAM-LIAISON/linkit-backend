package liaison.linkit.team.domain.repository.announcement;

public interface AnnouncementWorkTypeCustomRepository {

    boolean existsAnnouncementWorkTypeByTeamMemberAnnouncementId(
            final Long teamMemberAnnouncementId);

    void deleteByTeamMemberAnnouncementId(final Long teamMemberAnnouncementId);
}
