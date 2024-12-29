package liaison.linkit.matching.service;

import liaison.linkit.matching.business.MatchingMapper;
import liaison.linkit.matching.domain.Matching;
import liaison.linkit.matching.implement.MatchingCommandAdapter;
import liaison.linkit.matching.implement.MatchingQueryAdapter;
import liaison.linkit.matching.presentation.dto.MatchingRequestDTO;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MatchingService {

    private final MatchingCommandAdapter matchingCommandAdapter;
    private final MatchingQueryAdapter matchingQueryAdapter;
    private final MatchingMapper matchingMapper;

    public MatchingResponseDTO.AddMatchingResponse addMatching(
            final Long memberId,
            final MatchingRequestDTO.AddMatchingRequest addMatchingRequest
    ) {

        final Matching matching = Matching.builder()
                .id(null)
                .senderType(addMatchingRequest.getSenderType())
                .receiverType(addMatchingRequest.getReceiverType())
                .requestMessage(addMatchingRequest.getRequestMessage())
                .build();

        return matchingMapper.toAddMatchingResponse(matching);
    }
}
