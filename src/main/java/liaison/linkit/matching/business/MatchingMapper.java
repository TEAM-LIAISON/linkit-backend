package liaison.linkit.matching.business;

import java.util.List;
import liaison.linkit.common.annotation.Mapper;
import liaison.linkit.global.util.DateUtils;
import liaison.linkit.matching.domain.Matching;
import liaison.linkit.matching.domain.type.MatchingStatusType;
import liaison.linkit.matching.domain.type.ReceiverDeleteStatus;
import liaison.linkit.matching.domain.type.ReceiverReadStatus;
import liaison.linkit.matching.domain.type.SenderDeleteStatus;
import liaison.linkit.matching.presentation.dto.MatchingRequestDTO;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.AddMatchingResponse;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.DeleteReceivedMatchingItem;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.DeleteReceivedMatchingItems;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.DeleteRequestedMatchingItem;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.DeleteRequestedMatchingItems;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.MatchingNotificationMenu;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.ReceivedMatchingMenu;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.ReceiverAnnouncementInformation;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.ReceiverProfileInformation;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.ReceiverTeamInformation;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.RequestedMatchingMenu;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.SelectMatchingRequestToProfileMenu;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.SelectMatchingRequestToTeamMenu;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.SenderProfileInformation;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.SenderTeamInformation;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.UpdateMatchingStatusTypeResponse;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.UpdateReceivedMatchingCompletedStateReadItem;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.UpdateReceivedMatchingCompletedStateReadItems;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.UpdateReceivedMatchingRequestedStateToReadItem;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.UpdateReceivedMatchingRequestedStateToReadItems;
import liaison.linkit.profile.domain.profile.Profile;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO.ProfilePositionDetail;
import liaison.linkit.team.domain.announcement.TeamMemberAnnouncement;
import liaison.linkit.team.domain.team.Team;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO.AnnouncementPositionItem;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO.AnnouncementSkillName;
import liaison.linkit.team.presentation.team.dto.TeamResponseDTO.TeamScaleItem;
import lombok.extern.slf4j.Slf4j;

@Mapper
@Slf4j
public class MatchingMapper {

    public Matching toMatching(final MatchingRequestDTO.AddMatchingRequest addMatchingRequest) {
        log.info("Add matching request: {}", addMatchingRequest);
        return Matching.builder()
                .id(null)
                .senderType(addMatchingRequest.getSenderType())
                .senderEmailId(addMatchingRequest.getSenderEmailId())
                .senderTeamCode(addMatchingRequest.getSenderTeamCode())
                .receiverType(addMatchingRequest.getReceiverType())
                .receiverEmailId(addMatchingRequest.getReceiverEmailId())
                .receiverTeamCode(addMatchingRequest.getReceiverTeamCode())
                .receiverAnnouncementId(addMatchingRequest.getReceiverAnnouncementId())
                .requestMessage(addMatchingRequest.getRequestMessage())
                .matchingStatusType(MatchingStatusType.REQUESTED)
                .senderDeleteStatus(SenderDeleteStatus.REMAINING)
                .receiverDeleteStatus(ReceiverDeleteStatus.REMAINING)
                .receiverReadStatus(ReceiverReadStatus.UNREAD_REQUESTED_MATCHING)
                .build();
    }

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
            final Matching requestedMatchingItem,
            final Long chatRoomId,
            final SenderProfileInformation senderProfileInformation,
            final SenderTeamInformation senderTeamInformation,
            final ReceiverProfileInformation receiverProfileInformation,
            final ReceiverTeamInformation receiverTeamInformation,
            final ReceiverAnnouncementInformation receiverAnnouncementInformation
    ) {
        return RequestedMatchingMenu.builder()
                .matchingId(requestedMatchingItem.getId())
                .senderType(requestedMatchingItem.getSenderType())
                .receiverType(requestedMatchingItem.getReceiverType())
                .isChatRoomCreated(requestedMatchingItem.isChatRoomCreated())
                .chatRoomId(chatRoomId)
                .senderProfileInformation(senderProfileInformation)
                .senderTeamInformation(senderTeamInformation)
                .receiverProfileInformation(receiverProfileInformation)
                .receiverTeamInformation(receiverTeamInformation)
                .receiverAnnouncementInformation(receiverAnnouncementInformation)
                .requestMessage(requestedMatchingItem.getRequestMessage())
                .modifiedAt(DateUtils.formatRelativeTime(requestedMatchingItem.getModifiedAt()))
                .matchingStatusType(requestedMatchingItem.getMatchingStatusType())
                .receiverReadStatus(requestedMatchingItem.getReceiverReadStatus())
                .build();
    }

    public UpdateMatchingStatusTypeResponse toUpdateMatchingStatusTypeResponse(final Matching matching, final MatchingStatusType matchingStatusType) {
        return UpdateMatchingStatusTypeResponse.builder()
                .matchingId(matching.getId())
                .matchingStatusType(matchingStatusType)
                .build();
    }

    public ReceivedMatchingMenu toMatchingReceivedMenu(
            final Matching receivedMatchingItem,
            final Long chatRoomId,
            final SenderProfileInformation senderProfileInformation,
            final SenderTeamInformation senderTeamInformation,
            final ReceiverProfileInformation receiverProfileInformation,
            final ReceiverTeamInformation receiverTeamInformation,
            final ReceiverAnnouncementInformation receiverAnnouncementInformation
    ) {
        return ReceivedMatchingMenu.builder()
                .matchingId(receivedMatchingItem.getId())
                .senderType(receivedMatchingItem.getSenderType())
                .receiverType(receivedMatchingItem.getReceiverType())
                .isChatRoomCreated(receivedMatchingItem.isChatRoomCreated())
                .chatRoomId(chatRoomId)
                .senderProfileInformation(senderProfileInformation)
                .senderTeamInformation(senderTeamInformation)
                .receiverProfileInformation(receiverProfileInformation)
                .receiverTeamInformation(receiverTeamInformation)
                .receiverAnnouncementInformation(receiverAnnouncementInformation)
                .requestMessage(receivedMatchingItem.getRequestMessage())
                .modifiedAt(DateUtils.formatRelativeTime(receivedMatchingItem.getModifiedAt()))
                .matchingStatusType(receivedMatchingItem.getMatchingStatusType())
                .receiverReadStatus(receivedMatchingItem.getReceiverReadStatus())
                .build();
    }

    public MatchingResponseDTO.AddMatchingResponse toAddMatchingResponse(
            final Matching matching,
            final SenderProfileInformation senderProfileInformation,
            final SenderTeamInformation senderTeamInformation,
            final ReceiverProfileInformation receiverProfileInformation,
            final ReceiverTeamInformation receiverTeamInformation,
            final ReceiverAnnouncementInformation receiverAnnouncementInformation
    ) {

        return AddMatchingResponse.builder()
                .matchingId(matching.getId())
                .senderType(matching.getSenderType())
                .receiverType(matching.getReceiverType())
                .isChatRoomCreated(matching.isChatRoomCreated())
                .senderProfileInformation(senderProfileInformation)
                .senderTeamInformation(senderTeamInformation)
                .receiverProfileInformation(receiverProfileInformation)
                .receiverTeamInformation(receiverTeamInformation)
                .receiverAnnouncementInformation(receiverAnnouncementInformation)
                .requestMessage(matching.getRequestMessage())
                .matchingStatusType(matching.getMatchingStatusType())
                .receiverReadStatus(matching.getReceiverReadStatus())
                .build();
    }

    public MatchingResponseDTO.SenderProfileInformation toSenderProfileInformation(
            final Profile senderProfile,
            final ProfilePositionDetail senderProfilePositionDetail
    ) {
        return SenderProfileInformation.builder()
                .profileImagePath(senderProfile.getProfileImagePath())
                .emailId(senderProfile.getMember().getEmailId())
                .memberName(senderProfile.getMember().getMemberBasicInform().getMemberName())
                .profilePositionDetail(senderProfilePositionDetail)
                .build();
    }

    public MatchingResponseDTO.SenderTeamInformation toSenderTeamInformation(
            final Team senderTeam,
            final TeamScaleItem senderTeamScaleItem
    ) {
        return SenderTeamInformation.builder()
                .teamLogoImagePath(senderTeam.getTeamLogoImagePath())
                .teamName(senderTeam.getTeamName())
                .teamCode(senderTeam.getTeamCode())
                .teamScaleItem(senderTeamScaleItem)
                .build();
    }

    public MatchingResponseDTO.ReceiverProfileInformation toReceiverProfileInformation(
            final Profile receiverProfile,
            final ProfilePositionDetail receiverProfilePositionDetail
    ) {
        return ReceiverProfileInformation.builder()
                .profileImagePath(receiverProfile.getProfileImagePath())
                .emailId(receiverProfile.getMember().getEmailId())
                .memberName(receiverProfile.getMember().getMemberBasicInform().getMemberName())
                .profilePositionDetail(receiverProfilePositionDetail)
                .build();
    }

    public MatchingResponseDTO.ReceiverTeamInformation toReceiverTeamInformation(
            final Team receiverTeam,
            final TeamScaleItem receiverTeamScaleItem
    ) {
        return ReceiverTeamInformation.builder()
                .teamLogoImagePath(receiverTeam.getTeamLogoImagePath())
                .teamName(receiverTeam.getTeamName())
                .teamCode(receiverTeam.getTeamCode())
                .teamScaleItem(receiverTeamScaleItem)
                .build();
    }

    public MatchingResponseDTO.ReceiverAnnouncementInformation toReceiverAnnouncementInformation(
            final TeamMemberAnnouncement teamMemberAnnouncement,
            final AnnouncementPositionItem announcementPositionItem,
            final List<AnnouncementSkillName> announcementSkillNames
    ) {
        return ReceiverAnnouncementInformation.builder()
                .teamMemberAnnouncementId(teamMemberAnnouncement.getId())
                .teamName(teamMemberAnnouncement.getTeam().getTeamName())
                .teamLogoImagePath(teamMemberAnnouncement.getTeam().getTeamLogoImagePath())
                .announcementTitle(teamMemberAnnouncement.getAnnouncementTitle())
                .announcementPositionItem(announcementPositionItem)
                .announcementSkillNames(announcementSkillNames)
                .build();
    }

    public MatchingNotificationMenu toMatchingMenuResponse(
            final int receivedMatchingNotificationCount,
            final int requestedMatchingNotificationCount
    ) {
        return MatchingNotificationMenu.builder()
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
