package liaison.linkit.matching.domain.repository;

import java.util.List;

public interface MatchingCustomRepository {
    int countByReceiverTeamCodes(final List<String> receiverTeamCodes);

    int countByReceiverAnnouncementIds(final List<Long> receiverAnnouncementIds);

}
