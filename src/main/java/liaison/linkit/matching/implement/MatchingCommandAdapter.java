package liaison.linkit.matching.implement;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.matching.domain.repository.MatchingRepository;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class MatchingCommandAdapter {
    private final MatchingRepository matchingRepository;
}
