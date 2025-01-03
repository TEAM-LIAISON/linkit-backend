package liaison.linkit.matching.domain.repository;

import java.util.List;
import liaison.linkit.matching.domain.Matching;
import liaison.linkit.team.domain.team.Team;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MatchingCustomRepository {
    Page<Matching> findReceivedToProfile(
            final String emailId,
            final Pageable pageable
    );

    Page<Matching> findReceivedToTeam(
            final List<Team> teams,
            final Pageable pageable
    );

    Page<Matching> findReceivedToAnnouncement(
            final List<Long> announcementIds,
            final Pageable pageable
    );

    Page<Matching> findRequestedByProfile(
            final String emailId,
            final Pageable pageable
    );

    Page<Matching> findRequestedByTeam(
            final List<Team> teams,
            final Pageable pageable
    );

    int countByReceiverTeamCodes(final List<String> receiverTeamCodes);

    int countByReceiverAnnouncementIds(final List<Long> receiverAnnouncementIds);

}
