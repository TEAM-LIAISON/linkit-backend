package liaison.linkit.profile.business.assembler;

import java.util.List;

import liaison.linkit.profile.business.mapper.ProfilePositionMapper;
import liaison.linkit.profile.domain.profile.Profile;
import liaison.linkit.profile.domain.visit.ProfileVisit;
import liaison.linkit.profile.implement.position.ProfilePositionQueryAdapter;
import liaison.linkit.profile.implement.profile.ProfileQueryAdapter;
import liaison.linkit.profile.implement.visit.ProfileVisitQueryAdapter;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO.ProfilePositionDetail;
import liaison.linkit.profile.presentation.visit.dto.ProfileVisitResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProfileVisitModalAssembler {

    // Adapters
    private final ProfileVisitQueryAdapter profileVisitQueryAdapter;
    private final ProfilePositionQueryAdapter profilePositionQueryAdapter;
    private final ProfileQueryAdapter profileQueryAdapter;

    // Mappers
    private final ProfilePositionMapper profilePositionMapper;

    public ProfileVisitResponseDTO.ProfileVisitInforms assembleProfileVisitInforms(
            final Long memberId) {
        final Profile visitedProfile = profileQueryAdapter.findByMemberId(memberId);

        final List<ProfileVisit> profileVisits =
                profileVisitQueryAdapter.getProfileVisitsByVisitedProfileId(visitedProfile.getId());

        final List<Profile> profileVisitors =
                profileVisits.stream().map(ProfileVisit::getProfile).toList();

        final List<ProfileVisitResponseDTO.ProfileVisitInform> profileVisitInforms =
                getProfileVisitorInfos(profileVisitors);

        return ProfileVisitResponseDTO.ProfileVisitInforms.builder()
                .profileVisitInforms(profileVisitInforms)
                .build();
    }

    private List<ProfileVisitResponseDTO.ProfileVisitInform> getProfileVisitorInfos(
            final List<Profile> visitorProfiles) {
        return visitorProfiles.stream()
                .map(
                        profile ->
                                ProfileVisitResponseDTO.ProfileVisitInform.builder()
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
