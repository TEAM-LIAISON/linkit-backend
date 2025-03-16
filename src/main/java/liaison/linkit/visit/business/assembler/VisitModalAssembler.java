package liaison.linkit.visit.business.assembler;

import java.util.List;

import liaison.linkit.common.business.RegionMapper;
import liaison.linkit.common.implement.RegionQueryAdapter;
import liaison.linkit.common.presentation.RegionResponseDTO;
import liaison.linkit.global.util.DateUtils;
import liaison.linkit.profile.business.mapper.ProfilePositionMapper;
import liaison.linkit.profile.domain.profile.Profile;
import liaison.linkit.profile.domain.region.ProfileRegion;
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
    private final RegionQueryAdapter regionQueryAdapter;

    // Mappers
    private final ProfilePositionMapper profilePositionMapper;
    private final RegionMapper regionMapper;

    public VisitResponseDTO.VisitInforms assembleProfileVisitInforms(final Long memberId) {
        final Profile visitedProfile = profileQueryAdapter.findByMemberId(memberId);

        final List<ProfileVisit> profileVisits =
                profileVisitQueryAdapter.getProfileVisitsByVisitedProfileId(visitedProfile.getId());

        final List<VisitResponseDTO.VisitInform> profileVisitInforms =
                getProfileVisitInforms(profileVisits);

        return VisitResponseDTO.VisitInforms.builder().visitInforms(profileVisitInforms).build();
    }

    public VisitResponseDTO.VisitInforms assembleTeamVisitInforms(
            final Long memberId, final String teamCode) {
        final Team visitedTeam = teamQueryAdapter.findByTeamCode(teamCode);

        final List<TeamVisit> teamVisits =
                teamVisitQueryAdapter.getTeamVisitsByVisitedTeamId(visitedTeam.getId());

        final List<VisitResponseDTO.VisitInform> teamVisitInforms = getTeamVisitInforms(teamVisits);

        return VisitResponseDTO.VisitInforms.builder().visitInforms(teamVisitInforms).build();
    }

    private List<VisitResponseDTO.VisitInform> getProfileVisitInforms(
            final List<ProfileVisit> profileVisits) {
        return profileVisits.stream()
                .map(
                        visit -> {
                            Profile profile = visit.getProfile();
                            return VisitResponseDTO.VisitInform.builder()
                                    .profileImagePath(profile.getProfileImagePath())
                                    .memberName(
                                            profile.getMember()
                                                    .getMemberBasicInform()
                                                    .getMemberName())
                                    .emailId(profile.getMember().getEmailId())
                                    .profilePositionDetail(assembleProfilePositionDetail(profile))
                                    .regionDetail(assemblerProfileRegionDetail(profile))
                                    .visitedAt(DateUtils.formatRelativeTime(visit.getVisitTime()))
                                    .build();
                        })
                .toList();
    }

    private List<VisitResponseDTO.VisitInform> getTeamVisitInforms(
            final List<TeamVisit> teamVisits) {
        return teamVisits.stream()
                .map(
                        visit -> {
                            Profile profile = visit.getProfile();
                            return VisitResponseDTO.VisitInform.builder()
                                    .profileImagePath(profile.getProfileImagePath())
                                    .memberName(
                                            profile.getMember()
                                                    .getMemberBasicInform()
                                                    .getMemberName())
                                    .emailId(profile.getMember().getEmailId())
                                    .profilePositionDetail(assembleProfilePositionDetail(profile))
                                    .regionDetail(assemblerProfileRegionDetail(profile))
                                    .visitedAt(DateUtils.formatRelativeTime(visit.getVisitTime()))
                                    .build();
                        })
                .toList();
    }

    private ProfilePositionDetail assembleProfilePositionDetail(final Profile profile) {
        return profilePositionQueryAdapter.existsProfilePositionByProfileId(profile.getId())
                ? profilePositionMapper.toProfilePositionDetail(
                        profilePositionQueryAdapter.findProfilePositionByProfileId(profile.getId()))
                : new ProfilePositionDetail();
    }

    private RegionResponseDTO.RegionDetail assemblerProfileRegionDetail(final Profile profile) {
        RegionResponseDTO.RegionDetail regionDetail = new RegionResponseDTO.RegionDetail();
        if (regionQueryAdapter.existsProfileRegionByProfileId(profile.getId())) {
            ProfileRegion profileRegion =
                    regionQueryAdapter.findProfileRegionByProfileId(profile.getId());
            regionDetail = regionMapper.toRegionDetail(profileRegion.getRegion());
        }

        return regionDetail;
    }
}
