package liaison.linkit.wish.implement.teamScrap;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.wish.domain.TeamWish;
import liaison.linkit.wish.domain.repository.teamWish.TeamWishRepository;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class TeamScrapCommandAdapter {
    private TeamWishRepository teamWishRepository;

    public TeamWish create(final TeamWish teamWish) {
        return teamWishRepository.save(teamWish);
    }

    public void delete(final TeamWish teamWish) {
        teamWishRepository.delete(teamWish);
    }

    public void deleteByMemberIdAndTeamMemberAnnouncementId(final Long memberId, final Long teamMemberAnnouncementId) {
        teamWishRepository.deleteByMemberIdAndTeamMemberAnnouncementId(memberId, teamMemberAnnouncementId);
    }
}
