package liaison.linkit.visit.business.assembler;

import java.util.List;

import liaison.linkit.profile.business.mapper.ProfilePositionMapper;
import liaison.linkit.profile.domain.profile.Profile;
import liaison.linkit.profile.implement.position.ProfilePositionQueryAdapter;
import liaison.linkit.profile.implement.profile.ProfileQueryAdapter;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO.ProfilePositionDetail;
import liaison.linkit.team.domain.team.Team;
import liaison.linkit.team.implement.team.TeamQueryAdapter;
import liaison.linkit.visit.domain.ProfileVisit;
import liaison.linkit.visit.domain.TeamVisit;
import liaison.linkit.visit.implement.ProfileVisitQueryAdapter;
import liaison.linkit.visit.implement.TeamVisitQueryAdapter;
import liaison.linkit.visit.presentation.dto.VisitResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class VisitModalAssembler {

    // Adapters
    private final ProfileVisitQueryAdapter profileVisitQueryAdapter;
    private final ProfilePositionQueryAdapter profilePositionQueryAdapter;
    private final ProfileQueryAdapter profileQueryAdapter;

    private final TeamVisitQueryAdapter teamVisitQueryAdapter;
    private final TeamQueryAdapter teamQueryAdapter;

    // Mappers
    private final ProfilePositionMapper profilePositionMapper;

    public VisitResponseDTO.VisitInforms assembleProfileVisitInforms(final Long memberId) {
        final Profile visitedProfile = profileQueryAdapter.findByMemberId(memberId);

        final List<ProfileVisit> profileVisits =
                profileVisitQueryAdapter.getProfileVisitsByVisitedProfileId(visitedProfile.getId());

        final List<Profile> profileVisitors =
                profileVisits.stream().map(ProfileVisit::getProfile).toList();

        final List<VisitResponseDTO.VisitInform> profileVisitInforms =
                getProfileVisitorInfos(profileVisitors);

        return VisitResponseDTO.VisitInforms.builder().visitInforms(profileVisitInforms).build();
    }

    public VisitResponseDTO.VisitInforms assembleTeamVisitInforms(
            final Long memberId, final String teamCode) {
        final Team visitedTeam = teamQueryAdapter.findByTeamCode(teamCode);

        final List<TeamVisit> teamVisits =
                teamVisitQueryAdapter.getTeamVisitsByVisitedTeamId(visitedTeam.getId());

        final List<Profile> profileVisitors =
                teamVisits.stream().map(TeamVisit::getProfile).toList();

        final List<VisitResponseDTO.VisitInform> profileVisitInforms =
                getProfileVisitorInfos(profileVisitors);

        return VisitResponseDTO.VisitInforms.builder().visitInforms(profileVisitInforms).build();
    }

    private List<VisitResponseDTO.VisitInform> getProfileVisitorInfos(
            final List<Profile> visitorProfiles) {
        return visitorProfiles.stream()
                .map(
                        profile ->
                                VisitResponseDTO.VisitInform.builder()
                                        .profileImagePath(profile.getProfileImagePath())
                                        .memberName(
                                                profile.getMember()
                                                        .getMemberBasicInform()
                                                        .getMemberName())
                                        .emailId(profile.getMember().getEmailId())
                                        .profilePositionDetail(
                                                assembleProfilePositionDetail(profile))
                                        .build())
                .toList();
    }

    private ProfilePositionDetail assembleProfilePositionDetail(final Profile profile) {
        return profilePositionQueryAdapter.existsProfilePositionByProfileId(profile.getId())
                ? profilePositionMapper.toProfilePositionDetail(
                        profilePositionQueryAdapter.findProfilePositionByProfileId(profile.getId()))
                : new ProfilePositionDetail();
    }
}
