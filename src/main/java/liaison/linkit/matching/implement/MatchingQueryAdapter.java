package liaison.linkit.matching.implement;

import java.util.List;
import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.matching.domain.Matching;
import liaison.linkit.matching.domain.repository.MatchingRepository;
import liaison.linkit.team.domain.announcement.TeamMemberAnnouncement;
import liaison.linkit.team.domain.team.Team;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Adapter
@RequiredArgsConstructor
public class MatchingQueryAdapter {
    private final MatchingRepository matchingRepository;

    public Page<Matching> findReceivedToProfile(
            final String emailId,
            final Pageable pageable
    ) {
        return matchingRepository.findReceivedToProfile(emailId, pageable);
    }

    public Page<Matching> findReceivedToTeam(
            final List<Team> teams,
            final Pageable pageable
    ) {
        return matchingRepository.findReceivedToTeam(teams, pageable);
    }

    public Page<Matching> findReceivedToAnnouncement(
            final List<Long> announcementIds,
            final Pageable pageable
    ) {
        return matchingRepository.findReceivedToAnnouncement(announcementIds, pageable);
    }

    public int countByReceiverTeamCodes(final List<String> receiverTeamCodes) {
        return matchingRepository.countByReceiverTeamCodes(receiverTeamCodes);
    }

    public int countByReceiverAnnouncementIds(final List<Long> receiverAnnouncementIds) {
        return matchingRepository.countByReceiverAnnouncementIds(receiverAnnouncementIds);
    }
}
