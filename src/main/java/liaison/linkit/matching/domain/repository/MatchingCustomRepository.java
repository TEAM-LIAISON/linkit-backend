package liaison.linkit.matching.domain.repository;

import java.util.List;
import java.util.Optional;

import liaison.linkit.matching.domain.Matching;
import liaison.linkit.matching.domain.type.MatchingStatusType;
import liaison.linkit.team.domain.team.Team;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MatchingCustomRepository {

    Optional<Matching> findByMatchingId(final Long matchingId);

    List<Matching> findAllByIds(final List<Long> matchingIds);

    Page<Matching> findReceivedToProfile(final String emailId, final Pageable pageable);

    Page<Matching> findReceivedToTeam(final List<Team> teams, final Pageable pageable);

    Page<Matching> findReceivedToAnnouncement(
            final List<Long> announcementIds, final Pageable pageable);

    Page<Matching> findRequestedByProfile(final String emailId, final Pageable pageable);

    Page<Matching> findRequestedByTeam(final List<Team> teams, final Pageable pageable);

    boolean isCompletedMatching(final Long matchingId);

    int countByReceiverTeamCodes(final List<String> receiverTeamCodes);

    int countByReceiverAnnouncementIds(final List<Long> receiverAnnouncementIds);

    void updateMatchingStatusType(
            final Matching matching, final MatchingStatusType matchingStatusType);

    void updateMatchingToCreatedRoomState(final Matching matching);

    void deleteAllBySenderProfile(final String emailId);

    void deleteAllBySenderTeamCodes(final List<String> teamCodes);

    void deleteAllBySenderTeamCode(final String teamCode);

    void deleteAllByReceiverProfile(final String emailId);

    void deleteAllByReceiverTeamCodes(final List<String> teamCodes);

    void deleteAllByReceiverTeamCode(final String teamCode);

    void deleteAllByReceiverAnnouncements(final List<Long> announcementIds);
}
