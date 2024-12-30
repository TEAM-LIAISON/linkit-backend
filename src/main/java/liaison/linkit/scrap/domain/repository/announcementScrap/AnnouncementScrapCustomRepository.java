package liaison.linkit.scrap.domain.repository.announcementScrap;

public interface AnnouncementScrapCustomRepository {
    void deleteByMemberIdAndTeamMemberAnnouncementId(final Long memberId, final Long teamMemberAnnouncementId);

    boolean existsByMemberIdAndTeamMemberAnnouncementId(final Long memberId, final Long teamMemberAnnouncementId);

    int getTotalAnnouncementScrapCount(final Long teamMemberAnnouncementId);
}
