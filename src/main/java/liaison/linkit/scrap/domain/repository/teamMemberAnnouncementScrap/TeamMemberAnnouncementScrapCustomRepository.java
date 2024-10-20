package liaison.linkit.scrap.domain.repository.teamMemberAnnouncementScrap;

public interface TeamMemberAnnouncementScrapCustomRepository {
    void deleteByMemberIdAndTeamMemberAnnouncementId(final Long memberId, final Long teamMemberAnnouncementId);

    boolean existsByMemberIdAndTeamMemberAnnouncementId(final Long memberId, final Long teamMemberAnnouncementId);
}
