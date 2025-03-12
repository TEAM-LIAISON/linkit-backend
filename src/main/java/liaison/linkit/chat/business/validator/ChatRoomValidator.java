package liaison.linkit.chat.business.validator;

import java.util.List;

import liaison.linkit.chat.exception.CreateChatReceiverBadRequestException;
import liaison.linkit.chat.exception.CreateChatRoomBadRequestException;
import liaison.linkit.chat.implement.ChatRoomQueryAdapter;
import liaison.linkit.matching.domain.Matching;
import liaison.linkit.matching.domain.type.ReceiverType;
import liaison.linkit.profile.domain.profile.Profile;
import liaison.linkit.team.domain.announcement.TeamMemberAnnouncement;
import liaison.linkit.team.domain.team.Team;
import liaison.linkit.team.implement.announcement.TeamMemberAnnouncementQueryAdapter;
import liaison.linkit.team.implement.teamMember.TeamMemberQueryAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatRoomValidator {

    // Adapter
    private final ChatRoomQueryAdapter chatRoomQueryAdapter;
    private final TeamMemberQueryAdapter teamMemberQueryAdapter;
    private final TeamMemberAnnouncementQueryAdapter teamMemberAnnouncementQueryAdapter;

    public void validateExistsChatRoomByMatchingId(final Long matchingId) {
        if (chatRoomQueryAdapter.existsChatRoomByMatchingId(matchingId)) {
            throw CreateChatRoomBadRequestException.EXCEPTION;
        }
    }

    public void validateReceiverLogic(
            final Matching matching, final Long memberId, final Profile profile) {
        if (matching.getReceiverType() == ReceiverType.PROFILE) {
            if (!matching.getReceiverEmailId().equals(profile.getMember().getEmailId())) {
                throw CreateChatReceiverBadRequestException.EXCEPTION;
            }
        } else if (matching.getReceiverType().equals(ReceiverType.TEAM)) {
            List<Team> teams = teamMemberQueryAdapter.getAllTeamsInOwnerStateByMemberId(memberId);
            boolean hasTeam =
                    teams.stream()
                            .anyMatch(
                                    team ->
                                            team.getTeamCode()
                                                    .equals(matching.getReceiverTeamCode()));
            if (!hasTeam) {
                throw CreateChatReceiverBadRequestException.EXCEPTION;
            }
        } else if (matching.getReceiverType().equals(ReceiverType.ANNOUNCEMENT)) {
            List<Team> teams = teamMemberQueryAdapter.getAllTeamsInOwnerStateByMemberId(memberId);
            List<Long> teamIds = teams.stream().map(Team::getId).toList();

            List<TeamMemberAnnouncement> announcements =
                    teamMemberAnnouncementQueryAdapter.getAllByTeamIds(teamIds);

            boolean hasAnnouncement =
                    announcements.stream()
                            .anyMatch(
                                    ann ->
                                            ann.getId()
                                                    .equals(matching.getReceiverAnnouncementId()));

            if (!hasAnnouncement) {
                throw CreateChatReceiverBadRequestException.EXCEPTION;
            }
        }
    }
}
