package liaison.linkit.matching.business;

import liaison.linkit.common.annotation.Mapper;
import liaison.linkit.matching.domain.Matching;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.AddMatchingResponse;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.MatchingMenuResponse;

@Mapper
public class MatchingMapper {
    public MatchingResponseDTO.AddMatchingResponse toAddMatchingResponse(final Matching matching) {
        return AddMatchingResponse.builder()
                .senderType(matching.getSenderType())
                .receiverType(matching.getReceiverType())
                .receiverEmailId(matching.getReceiverEmailId())
                .build();
    }

    public MatchingResponseDTO.MatchingMenuResponse toMatchingMenuResponse(
            final int receivedMatchingNotificationCount,
            final int requestedMatchingNotificationCount
    ) {
        return MatchingMenuResponse.builder()
                .receivedMatchingNotificationCount(receivedMatchingNotificationCount)
                .requestedMatchingNotificationCount(requestedMatchingNotificationCount)
                .build();
    }
}
