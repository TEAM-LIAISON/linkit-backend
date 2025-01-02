package liaison.linkit.matching.implement;

import java.util.List;
import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.matching.domain.repository.MatchingRepository;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class MatchingQueryAdapter {
    private final MatchingRepository matchingRepository;

    public int countByReceiverTeamCodes(final List<String> receiverTeamCodes) {
        return matchingRepository.countByReceiverTeamCodes(receiverTeamCodes);
    }

    public int countByReceiverAnnouncementIds(final List<Long> receiverAnnouncementIds) {
        return matchingRepository.countByReceiverAnnouncementIds(receiverAnnouncementIds);
    }
}
