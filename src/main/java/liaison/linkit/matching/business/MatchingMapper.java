package liaison.linkit.matching.business;

import liaison.linkit.common.annotation.Mapper;
import liaison.linkit.matching.domain.Matching;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.AddMatchingResponse;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.MatchingMenu;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.MatchingReceivedMenu;

@Mapper
public class MatchingMapper {

    public MatchingReceivedMenu toMatchingReceivedMenu(
            final Matching receivedMatchingItem
    ) {
        return MatchingReceivedMenu.builder()
                .receiverEmailId(receivedMatchingItem.getReceiverEmailId())
                .build();
    }

    public MatchingResponseDTO.AddMatchingResponse toAddMatchingResponse(final Matching matching) {
        return AddMatchingResponse.builder()
                .senderType(matching.getSenderType())
                .receiverType(matching.getReceiverType())
                .receiverEmailId(matching.getReceiverEmailId())
                .build();
    }

    public MatchingMenu toMatchingMenuResponse(
            final int receivedMatchingNotificationCount,
            final int requestedMatchingNotificationCount
    ) {
        return MatchingMenu.builder()
                .receivedMatchingNotificationCount(receivedMatchingNotificationCount)
                .requestedMatchingNotificationCount(requestedMatchingNotificationCount)
                .build();
    }
}
