package liaison.linkit.wish.implement.teamScrap;

import java.util.List;
import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.wish.domain.TeamWish;
import liaison.linkit.wish.domain.repository.teamWish.TeamWishRepository;
import liaison.linkit.wish.exception.teamWish.TeamWishNotFoundException;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class TeamScrapQueryAdapter {
    private final TeamWishRepository teamWishRepository;

    public List<TeamWish> findAllByMemberId(final Long memberId) {
        return teamWishRepository.findAllByMemberId(memberId);
    }

    public TeamWish findByTeamMemberAnnouncementId(final Long teamMemberAnnouncementId) {
        return teamWishRepository.findByTeamMemberAnnouncementId(teamMemberAnnouncementId)
                .orElseThrow(() -> TeamWishNotFoundException.EXCEPTION);
    }

    public TeamWish findByMemberIdAndTeamMemberAnnouncementId(final Long memberId,
                                                              final Long teamMemberAnnouncementId) {
        return teamWishRepository.findByMemberIdAndTeamMemberAnnouncementId(teamMemberAnnouncementId, memberId)
                .orElseThrow(() -> TeamWishNotFoundException.EXCEPTION);
    }
}
