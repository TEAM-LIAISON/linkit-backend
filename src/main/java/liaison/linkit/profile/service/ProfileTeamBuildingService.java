package liaison.linkit.profile.service;

import liaison.linkit.global.exception.AuthException;
import liaison.linkit.profile.domain.Profile;
import liaison.linkit.profile.domain.repository.ProfileRepository;
import liaison.linkit.profile.domain.repository.ProfileTeamBuildingRepository;
import liaison.linkit.profile.domain.teambuilding.ProfileTeamBuildingField;
import liaison.linkit.profile.dto.request.ProfileTeamBuildingCreateRequest;
import liaison.linkit.profile.dto.response.ProfileTeamBuildingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static liaison.linkit.global.exception.ExceptionCode.INVALID_PROFILE_TEAM_BUILDING_WITH_MEMBER;

@Service
@RequiredArgsConstructor
@Transactional
public class ProfileTeamBuildingService {

    private final ProfileRepository profileRepository;
    private final ProfileTeamBuildingRepository profileTeamBuildingRepository;

    public Long validateEducationByMember(Long memberId) {
        Long profileId = profileRepository.findByMemberId(memberId).getId();
        if (!profileTeamBuildingRepository.existsByProfileId(profileId)) {
            throw new AuthException(INVALID_PROFILE_TEAM_BUILDING_WITH_MEMBER);
        } else {
            return profileTeamBuildingRepository.findByProfileId(profileId).getId();
        }
    }

    public ProfileTeamBuildingResponse save(final Long memberId, final ProfileTeamBuildingCreateRequest profileTeamBuildingCreateRequest) {
        final Profile profile = profileRepository.findByMemberId(memberId);

        final ProfileTeamBuildingField newProfileTeamBuilding = ProfileTeamBuildingField.of(
                profile,
                profileTeamBuildingCreateRequest.getTeamBuildingField()
        );

        final ProfileTeamBuildingField profileTeamBuildingField = profileTeamBuildingRepository.save(newProfileTeamBuilding);
        return getProfileResponse(profileTeamBuildingField);
    }

    private ProfileTeamBuildingResponse getProfileResponse(final ProfileTeamBuildingField profileTeamBuildingField) {
        return ProfileTeamBuildingResponse.of(profileTeamBuildingField);
    }

    @Transactional(readOnly = true)
    public List<ProfileTeamBuildingResponse> getAllProfileTeamBuildings(final Long memberId) {
        Long profileId = profileRepository.findByMemberId(memberId).getId();
        final List<ProfileTeamBuildingField> profileTeamBuildingFields = profileTeamBuildingRepository.findAllByProfileId(profileId);
        return profileTeamBuildingFields.stream()
                .map(this::getProfileResponse)
                .toList();
    }
}
