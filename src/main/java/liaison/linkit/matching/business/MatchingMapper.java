package liaison.linkit.matching.business;

import java.util.List;
import liaison.linkit.common.annotation.Mapper;
import liaison.linkit.matching.domain.Matching;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.AddMatchingResponse;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.MatchingMenu;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.ReceivedMatchingMenu;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.RequestedMatchingMenu;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.UpdateReceivedMatchingCompletedStateReadItem;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.UpdateReceivedMatchingCompletedStateReadItems;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.UpdateReceivedMatchingRequestedStateToReadItem;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.UpdateReceivedMatchingRequestedStateToReadItems;

@Mapper
public class MatchingMapper {

    public RequestedMatchingMenu toMatchingRequestedMenu(
            final Matching requestedMatchingItem
    ) {
        return RequestedMatchingMenu.builder()
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

    public ReceivedMatchingMenu toMatchingReceivedMenu(
            final Matching receivedMatchingItem
    ) {
        return ReceivedMatchingMenu.builder()
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

    public UpdateReceivedMatchingRequestedStateToReadItems toUpdateMatchingReceivedToReadItems(
            final List<UpdateReceivedMatchingRequestedStateToReadItem> items
    ) {
        return UpdateReceivedMatchingRequestedStateToReadItems.builder()
                .updateReceivedMatchingRequestedStateToReadItems(items)
                .build();
    }

    public UpdateReceivedMatchingCompletedStateReadItems toUpdateMatchingCompletedToReadItems(
            final List<UpdateReceivedMatchingCompletedStateReadItem> items
    ) {
        return UpdateReceivedMatchingCompletedStateReadItems.builder()
                .updateReceivedMatchingCompletedStateReadItems(items)
                .build();
    }
}
