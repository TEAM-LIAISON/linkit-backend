package liaison.linkit.matching.business;

import liaison.linkit.common.annotation.Mapper;
import liaison.linkit.matching.domain.Matching;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.AddMatchingResponse;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.MatchingMenu;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.MatchingReceivedMenu;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.MatchingRequestedMenu;

@Mapper
public class MatchingMapper {

    public MatchingRequestedMenu toMatchingRequestedMenu(
            final Matching requestedMatchingItem
    ) {
        return MatchingRequestedMenu.builder()
                .matchingId(requestedMatchingItem.getId())
                .senderType(requestedMatchingItem.getSenderType())
                .receiverType(requestedMatchingItem.getReceiverType())
                .senderEmailId(requestedMatchingItem.getSenderEmailId())
                .senderTeamCode(requestedMatchingItem.getSenderTeamCode())
                .receiverEmailId(requestedMatchingItem.getReceiverEmailId())
                .receiverTeamCode(requestedMatchingItem.getReceiverTeamCode())
                .receiverAnnouncementId(requestedMatchingItem.getReceiverAnnouncementId())
                .requestMessage(requestedMatchingItem.getRequestMessage())
                .matchingStatusType(requestedMatchingItem.getMatchingStatusType())
                .receiverReadStatus(requestedMatchingItem.getReceiverReadStatus())
                .build();
    }

    public MatchingReceivedMenu toMatchingReceivedMenu(
            final Matching receivedMatchingItem
    ) {
        return MatchingReceivedMenu.builder()
                .matchingId(receivedMatchingItem.getId())
                .senderType(receivedMatchingItem.getSenderType())
                .receiverType(receivedMatchingItem.getReceiverType())
                .senderEmailId(receivedMatchingItem.getSenderEmailId())
                .senderTeamCode(receivedMatchingItem.getSenderTeamCode())
                .receiverEmailId(receivedMatchingItem.getReceiverEmailId())
                .receiverTeamCode(receivedMatchingItem.getReceiverTeamCode())
                .receiverAnnouncementId(receivedMatchingItem.getReceiverAnnouncementId())
                .requestMessage(receivedMatchingItem.getRequestMessage())
                .matchingStatusType(receivedMatchingItem.getMatchingStatusType())
                .receiverReadStatus(receivedMatchingItem.getReceiverReadStatus())
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
