package liaison.linkit.matching.implement;

import java.util.List;
import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.matching.domain.Matching;
import liaison.linkit.matching.domain.repository.MatchingRepository;
import liaison.linkit.matching.domain.type.MatchingStatusType;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class MatchingCommandAdapter {
    private final MatchingRepository matchingRepository;

    public void updateAll(final List<Matching> matchings) {
        matchingRepository.saveAll(matchings);
    }

    public void updateMatchingStatusType(final Matching matching, final MatchingStatusType matchingStatusType) {
        matchingRepository.updateMatchingStatusType(matching, matchingStatusType);
    }

    public Matching addMatching(final Matching matching) {
        return matchingRepository.save(matching);
    }

    public void updateMatchingToCreatedRoomState(final Matching matching) {
        matchingRepository.updateMatchingToCreatedRoomState(matching);
    }

    public void deleteAllBySenderProfile(final String emailId) {
        matchingRepository.deleteAllBySenderProfile(emailId);
    }

    public void deleteAllBySenderTeamCodes(final List<String> teamCodes) {
        matchingRepository.deleteAllBySenderTeamCodes(teamCodes);
    }

    public void deleteAllByReceiverProfile(final String emailId) {
        matchingRepository.deleteAllByReceiverProfile(emailId);
    }

    public void deleteAllByReceiverTeamCodes(final List<String> teamCodes) {
        matchingRepository.deleteAllByReceiverTeamCodes(teamCodes);
    }

    public void deleteAllByReceiverAnnouncements(final List<Long> teamMemberAnnouncementIds) {
        matchingRepository.deleteAllByReceiverAnnouncements(teamMemberAnnouncementIds);
    }
}
