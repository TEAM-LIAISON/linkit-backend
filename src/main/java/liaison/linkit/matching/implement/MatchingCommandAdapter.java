package liaison.linkit.matching.implement;

import java.util.List;
import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.matching.domain.Matching;
import liaison.linkit.matching.domain.repository.MatchingRepository;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class MatchingCommandAdapter {
    private final MatchingRepository matchingRepository;

    public void updateAll(final List<Matching> matchings) {
        matchingRepository.saveAll(matchings);
    }
}
