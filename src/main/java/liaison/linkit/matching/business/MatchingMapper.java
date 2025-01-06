package liaison.linkit.matching.business;

import java.util.List;
import liaison.linkit.common.annotation.Mapper;
import liaison.linkit.matching.domain.Matching;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.AddMatchingResponse;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.DeleteReceivedMatchingItem;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.DeleteReceivedMatchingItems;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.DeleteRequestedMatchingItem;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.DeleteRequestedMatchingItems;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.MatchingMenu;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.ReceivedMatchingMenu;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.ReceiverProfileInformation;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.ReceiverTeamInformation;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.RequestedMatchingMenu;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.SelectMatchingRequestToProfileMenu;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.SelectMatchingRequestToTeamMenu;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.SenderProfileInformation;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.SenderTeamInformation;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.UpdateReceivedMatchingCompletedStateReadItem;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.UpdateReceivedMatchingCompletedStateReadItems;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.UpdateReceivedMatchingRequestedStateToReadItem;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.UpdateReceivedMatchingRequestedStateToReadItems;
import liaison.linkit.profile.domain.profile.Profile;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO.ProfilePositionDetail;
import liaison.linkit.team.domain.team.Team;
import liaison.linkit.team.presentation.team.dto.TeamResponseDTO.TeamScaleItem;

@Mapper
public class MatchingMapper {

    public SelectMatchingRequestToProfileMenu toSelectMatchingRequestToProfileMenu(
            final Boolean isTeamInformationExists,
            final Profile profile,
            final ProfilePositionDetail senderProfilePositionDetail,
            final List<SenderTeamInformation> senderTeamInformations,
            final Profile receiverProfile,
            final ProfilePositionDetail receiverProfilePositionDetail
    ) {
        return SelectMatchingRequestToProfileMenu.builder()
                .isTeamInformationExists(isTeamInformationExists)
                .senderProfileInformation(
                        SenderProfileInformation.builder()
                                .profileImagePath(profile.getProfileImagePath())
                                .memberName(profile.getMember().getMemberBasicInform().getMemberName())
                                .emailId(profile.getMember().getEmailId())
                                .profilePositionDetail(senderProfilePositionDetail)
                                .build()
                )
                .senderTeamInformation(senderTeamInformations)
                .receiverProfileInformation(
                        ReceiverProfileInformation.builder()
                                .profileImagePath(receiverProfile.getProfileImagePath())
                                .memberName(receiverProfile.getMember().getMemberBasicInform().getMemberName())
                                .emailId(receiverProfile.getMember().getEmailId())
                                .profilePositionDetail(receiverProfilePositionDetail)
                                .build()
                )
                .build();
    }

    public SelectMatchingRequestToTeamMenu toSelectMatchingRequestTeamMenu(
            final Boolean isTeamInformationExists,
            final Profile senderProfile,
            final ProfilePositionDetail senderProfilePositionDetail,
            final List<SenderTeamInformation> senderTeamInformations,
            final Team receiverTeam,
            final TeamScaleItem teamScaleItem
    ) {
        return SelectMatchingRequestToTeamMenu.builder()
                .isTeamInformationExists(isTeamInformationExists)
                .senderProfileInformation(
                        SenderProfileInformation.builder()
                                .profileImagePath(senderProfile.getProfileImagePath())
                                .memberName(senderProfile.getMember().getMemberBasicInform().getMemberName())
                                .emailId(senderProfile.getMember().getEmailId())
                                .profilePositionDetail(senderProfilePositionDetail)
                                .build()
                )
                .senderTeamInformation(senderTeamInformations)
                .receiverTeamInformation(
                        ReceiverTeamInformation.builder()
                                .teamLogoImagePath(receiverTeam.getTeamLogoImagePath())
                                .teamName(receiverTeam.getTeamName())
                                .teamCode(receiverTeam.getTeamCode())
                                .teamScaleItem(teamScaleItem)
                                .build()
                )
                .build();
    }

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

    public DeleteReceivedMatchingItems toDeleteReceivedMatchingItems(
            final List<DeleteReceivedMatchingItem> items
    ) {
        return DeleteReceivedMatchingItems.builder()
                .deleteReceivedMatchingItems(items)
                .build();
    }

    public DeleteRequestedMatchingItems toDeleteRequestedMatchingItems(
            final List<DeleteRequestedMatchingItem> items
    ) {
        return DeleteRequestedMatchingItems.builder()
                .deleteRequestedMatchingItems(items)
                .build();
    }
}
