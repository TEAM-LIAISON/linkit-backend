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

import static liaison.linkit.global.exception.ExceptionCode.INVALID_PROFILE_TEAM_BUILDING_WITH_MEMBER;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ProfileTeamBuildingFieldService {

    private final ProfileRepository profileRepository;
    private final ProfileTeamBuildingRepository profileTeamBuildingRepository;
    private final TeamBuildingRepository teamBuildingRepository;

    public Long validateProfileTeamBuildingFieldByMember(Long memberId) {
        Long profileId = profileRepository.findByMemberId(memberId).getId();
        if (!profileTeamBuildingRepository.existsByProfileId(profileId)) {
            throw new AuthException(INVALID_PROFILE_TEAM_BUILDING_WITH_MEMBER);
        } else {
            return profileTeamBuildingRepository.findByProfileId(profileId).getId();
        }
    }

    public void save(final Long memberId, final ProfileTeamBuildingCreateRequest createRequest) {
        log.info("member.id={}", memberId);

        final Profile profile = profileRepository.findByMemberId(memberId);

        log.info("profile.id={}", profile.getId());

        final List<TeamBuildingField> teamBuildingFields = teamBuildingRepository
                .findTeamBuildingFieldsByFieldNames(createRequest.getTeamBuildingFieldNames());

        final List<ProfileTeamBuildingField> profileTeamBuildingFields = teamBuildingFields.stream()
                .map(teamBuildingField -> new ProfileTeamBuildingField(null, profile, teamBuildingField))
                .toList();

        profileTeamBuildingRepository.saveAll(profileTeamBuildingFields);
    }

    private ProfileTeamBuildingResponse getProfileTeamBuildingResponse(
            final List<ProfileTeamBuildingField> profileTeamBuildingFields
    ) {
        List<String> teamBuildingFieldNames = profileTeamBuildingFields.stream()
                .map(profileTeamBuildingField -> profileTeamBuildingField.getTeamBuildingField().getTeamBuildingFieldName())
                .toList();
        return ProfileTeamBuildingResponse.of(teamBuildingFieldNames);
    }

//    @Transactional(readOnly = true)
//    public List<ProfileTeamBuildingResponse> getAllProfileTeamBuildings(final Long memberId) {
//        Long profileId = profileRepository.findByMemberId(memberId).getId();
//        final List<ProfileTeamBuildingField> profileTeamBuildingFields = profileTeamBuildingRepository.findAllByProfileId(profileId);
//        return profileTeamBuildingFields.stream()
//                .map(this::getProfileResponse)
//                .toList();
//    }
}
