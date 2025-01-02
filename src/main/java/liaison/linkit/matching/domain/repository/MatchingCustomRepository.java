package liaison.linkit.matching.domain.repository;

import java.util.List;
import liaison.linkit.matching.domain.Matching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MatchingCustomRepository {
    Page<Matching> findReceivedToProfile(
            final String emailId,
            final Pageable pageable
    );

    int countByReceiverTeamCodes(final List<String> receiverTeamCodes);

    int countByReceiverAnnouncementIds(final List<Long> receiverAnnouncementIds);

}
