package liaison.linkit.matching.business.assembler;

import java.util.ArrayList;
import java.util.List;

import liaison.linkit.matching.business.mapper.MatchingMapper;
import liaison.linkit.matching.exception.NotAllowMatchingBadRequestException;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.SelectMatchingRequestToProfileMenu;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.SelectMatchingRequestToTeamMenu;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.SenderTeamInformation;
import liaison.linkit.profile.business.mapper.ProfilePositionMapper;
import liaison.linkit.profile.domain.profile.Profile;
import liaison.linkit.profile.implement.position.ProfilePositionQueryAdapter;
import liaison.linkit.profile.implement.profile.ProfileQueryAdapter;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO.ProfilePositionDetail;
import liaison.linkit.team.business.mapper.scale.TeamScaleMapper;
import liaison.linkit.team.domain.team.Team;
import liaison.linkit.team.implement.scale.TeamScaleQueryAdapter;
import liaison.linkit.team.implement.team.TeamQueryAdapter;
import liaison.linkit.team.implement.teamMember.TeamMemberQueryAdapter;
import liaison.linkit.team.presentation.team.dto.TeamResponseDTO.TeamScaleItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SendMatchingModalAssembler {

    // Adapters
    private final ProfileQueryAdapter profileQueryAdapter;
    private final ProfilePositionQueryAdapter profilePositionQueryAdapter;
    private final TeamMemberQueryAdapter teamMemberQueryAdapter;
    private final TeamScaleQueryAdapter teamScaleQueryAdapter;
    private final TeamQueryAdapter teamQueryAdapter;

    // Mappers
    private final ProfilePositionMapper profilePositionMapper;
    private final MatchingMapper matchingMapper;
    private final TeamScaleMapper teamScaleMapper;

    public SelectMatchingRequestToProfileMenu assembleSelectMatchingRequestToProfileMenu(
            final Long senderMemberId, final String receiverEmailId) {
        final Profile senderProfile = getSenderProfile(senderMemberId);
        if (senderProfile.getMember().getEmailId().equals(receiverEmailId)) {
            throw NotAllowMatchingBadRequestException.EXCEPTION;
        }

        final ProfilePositionDetail senderPos = getProfilePositionDetail(senderProfile);
        final boolean teamInfoExists =
                teamMemberQueryAdapter.existsTeamOwnerByMemberId(senderMemberId);
        final List<SenderTeamInformation> senderTeamInfos = getSenderTeamInfos(senderMemberId);
        final Profile receiverProfile = profileQueryAdapter.findByEmailId(receiverEmailId);
        final ProfilePositionDetail receiverPos = getProfilePositionDetail(receiverProfile);

        return matchingMapper.toSelectMatchingRequestToProfileMenu(
                teamInfoExists,
                senderProfile,
                senderPos,
                senderTeamInfos,
                receiverProfile,
                receiverPos);
    }

    public SelectMatchingRequestToTeamMenu assembleSelectMatchingRequestToTeamMenu(
            final Long senderMemberId, final String receiverTeamCode) {
        final Profile senderProfile = getSenderProfile(senderMemberId);
        final ProfilePositionDetail senderPos = getProfilePositionDetail(senderProfile);
        final boolean teamInfoExists =
                teamMemberQueryAdapter.existsTeamOwnerByMemberId(senderMemberId);
        final List<SenderTeamInformation> senderTeamInfos = getSenderTeamInfos(senderMemberId);
        final Team receiverTeam = teamQueryAdapter.findByTeamCode(receiverTeamCode);
        final TeamScaleItem receiverScale = getTeamScale(receiverTeam);

        return matchingMapper.toSelectMatchingRequestTeamMenu(
                teamInfoExists,
                senderProfile,
                senderPos,
                senderTeamInfos,
                receiverTeam,
                receiverScale);
    }

    // -- 헬퍼 메서드 --

    private Profile getSenderProfile(final Long senderMemberId) {
        return profileQueryAdapter.findByMemberId(senderMemberId);
    }

    private ProfilePositionDetail getProfilePositionDetail(final Profile profile) {
        return profilePositionQueryAdapter.existsProfilePositionByProfileId(profile.getId())
                ? profilePositionMapper.toProfilePositionDetail(
                        profilePositionQueryAdapter.findProfilePositionByProfileId(profile.getId()))
                : new ProfilePositionDetail();
    }

    private List<SenderTeamInformation> getSenderTeamInfos(final Long senderMemberId) {
        if (!teamMemberQueryAdapter.existsTeamOwnerByMemberId(senderMemberId)) {
            return new ArrayList<>();
        }
        List<Team> teams = teamMemberQueryAdapter.getAllTeamsInOwnerStateByMemberId(senderMemberId);
        return teams.stream()
                .map(
                        team ->
                                SenderTeamInformation.builder()
                                        .teamCode(team.getTeamCode())
                                        .teamName(team.getTeamName())
                                        .teamLogoImagePath(team.getTeamLogoImagePath())
                                        .teamScaleItem(getTeamScale(team))
                                        .build())
                .toList();
    }

    private TeamScaleItem getTeamScale(final Team team) {
        return teamScaleQueryAdapter.existsTeamScaleByTeamId(team.getId())
                ? teamScaleMapper.toTeamScaleItem(
                        teamScaleQueryAdapter.findTeamScaleByTeamId(team.getId()))
                : new TeamScaleItem();
    }
}
