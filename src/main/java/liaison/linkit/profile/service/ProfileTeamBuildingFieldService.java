package liaison.linkit.profile.service;

import liaison.linkit.global.exception.AuthException;
import liaison.linkit.profile.domain.Profile;
import liaison.linkit.profile.domain.repository.ProfileRepository;
import liaison.linkit.profile.domain.repository.ProfileTeamBuildingRepository;
import liaison.linkit.profile.domain.repository.TeamBuildingRepository;
import liaison.linkit.profile.domain.teambuilding.ProfileTeamBuildingField;
import liaison.linkit.profile.domain.teambuilding.TeamBuildingField;
import liaison.linkit.profile.dto.request.ProfileTeamBuildingCreateRequest;
import liaison.linkit.profile.dto.response.ProfileTeamBuildingResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static liaison.linkit.global.exception.ExceptionCode.INVALID_PROFILE_TEAM_BUILDING_WITH_MEMBER;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ProfileTeamBuildingFieldService {

    private final ProfileRepository profileRepository;
    private final ProfileTeamBuildingRepository profileTeamBuildingRepository;
    private final TeamBuildingRepository teamBuildingRepository;

    public void validateProfileTeamBuildingFieldByMember(Long memberId) {
        Long profileId = profileRepository.findByMemberId(memberId).getId();
        if (!profileTeamBuildingRepository.existsByProfileId(profileId)) {
            throw new AuthException(INVALID_PROFILE_TEAM_BUILDING_WITH_MEMBER);
        }
    }

    public void save(final Long memberId, final ProfileTeamBuildingCreateRequest createRequest) {
        final Profile profile = profileRepository.findByMemberId(memberId);

        final List<TeamBuildingField> teamBuildingFields = teamBuildingRepository
                .findTeamBuildingFieldsByFieldNames(createRequest.getTeamBuildingFieldNames());

        final List<ProfileTeamBuildingField> profileTeamBuildingFields = teamBuildingFields.stream()
                .map(teamBuildingField -> new ProfileTeamBuildingField(null, profile, teamBuildingField))
                .toList();

        profileTeamBuildingRepository.saveAll(profileTeamBuildingFields);
        profile.updateIsProfileTeamBuildingField(true);
        profileRepository.save(profile);
    }


    @Transactional(readOnly = true)
    public ProfileTeamBuildingResponse getAllProfileTeamBuildings(final Long memberId) {
        Long profileId = profileRepository.findByMemberId(memberId).getId();

        List<ProfileTeamBuildingField> profileTeamBuildingFields = profileTeamBuildingRepository.findAllByProfileId(profileId);

        List<String> teamBuildingFieldNames = profileTeamBuildingFields.stream()
                .map(profileTeamBuildingField -> teamBuildingRepository.findById(profileTeamBuildingField.getTeamBuildingField().getId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(TeamBuildingField::getTeamBuildingFieldName)
                .toList();

        return new ProfileTeamBuildingResponse(teamBuildingFieldNames);
    }
}
