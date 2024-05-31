package liaison.linkit.profile.service;

import liaison.linkit.global.exception.AuthException;
import liaison.linkit.profile.domain.Profile;
import liaison.linkit.profile.domain.repository.ProfileRepository;
import liaison.linkit.profile.domain.repository.teambuilding.ProfileTeamBuildingFieldRepository;
import liaison.linkit.profile.domain.repository.teambuilding.TeamBuildingFieldRepository;
import liaison.linkit.profile.domain.teambuilding.ProfileTeamBuildingField;
import liaison.linkit.profile.domain.teambuilding.TeamBuildingField;
import liaison.linkit.profile.dto.request.teamBuilding.ProfileTeamBuildingCreateRequest;
import liaison.linkit.profile.dto.request.teamBuilding.ProfileTeamBuildingUpdateRequest;
import liaison.linkit.profile.dto.response.ProfileTeamBuildingFieldResponse;
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
    private final ProfileTeamBuildingFieldRepository profileTeamBuildingFieldRepository;
    private final TeamBuildingFieldRepository teamBuildingFieldRepository;

    public void validateProfileTeamBuildingFieldByMember(final Long memberId) {
        Long profileId = profileRepository.findByMemberId(memberId).getId();
        if (!profileTeamBuildingFieldRepository.existsByProfileId(profileId)) {
            throw new AuthException(INVALID_PROFILE_TEAM_BUILDING_WITH_MEMBER);
        }
    }

    // 희망 팀빌딩 분야 저장 비즈니스 로직
    public void save(final Long memberId, final ProfileTeamBuildingCreateRequest createRequest) {
        final Profile profile = profileRepository.findByMemberId(memberId);
        // 이미 저장되어 있었던 부분이면?
        if (profileTeamBuildingFieldRepository.existsByProfileId(profile.getId())) {
            profileTeamBuildingFieldRepository.deleteAllByProfileId(profile.getId());

            final List<TeamBuildingField> teamBuildingFields = teamBuildingFieldRepository
                    .findTeamBuildingFieldsByFieldNames(createRequest.getTeamBuildingFieldNames());

            // Request DTO -> 각 문자열을 TeamBuildingField 테이블에서 찾아서 가져옴
            final List<ProfileTeamBuildingField> profileTeamBuildingFields = teamBuildingFields.stream()
                    .map(teamBuildingField -> new ProfileTeamBuildingField(null, profile, teamBuildingField))
                    .toList();

            // profileTeamBuildingFieldRepository 모두 저장
            profileTeamBuildingFieldRepository.saveAll(profileTeamBuildingFields);
        } else {
            final List<TeamBuildingField> teamBuildingFields = teamBuildingFieldRepository
                    .findTeamBuildingFieldsByFieldNames(createRequest.getTeamBuildingFieldNames());

            // Request DTO -> 각 문자열을 TeamBuildingField 테이블에서 찾아서 가져옴
            final List<ProfileTeamBuildingField> profileTeamBuildingFields = teamBuildingFields.stream()
                    .map(teamBuildingField -> new ProfileTeamBuildingField(null, profile, teamBuildingField))
                    .toList();

            // profileTeamBuildingFieldRepository 모두 저장
            profileTeamBuildingFieldRepository.saveAll(profileTeamBuildingFields);
        }

        // 프로그레스바 처리 비즈니스 로직
        profile.updateIsProfileTeamBuildingField(true);
        profileRepository.save(profile);
    }


    @Transactional(readOnly = true)
    public ProfileTeamBuildingFieldResponse getAllProfileTeamBuildings(final Long memberId) {
        Long profileId = profileRepository.findByMemberId(memberId).getId();

        List<ProfileTeamBuildingField> profileTeamBuildingFields = profileTeamBuildingFieldRepository.findAllByProfileId(profileId);

        List<String> teamBuildingFieldNames = profileTeamBuildingFields.stream()
                .map(profileTeamBuildingField -> teamBuildingFieldRepository.findById(profileTeamBuildingField.getTeamBuildingField().getId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(TeamBuildingField::getTeamBuildingFieldName)
                .toList();

        return ProfileTeamBuildingFieldResponse.of(teamBuildingFieldNames);
    }

    public ProfileTeamBuildingFieldResponse update(
            final Long memberId,
            final ProfileTeamBuildingUpdateRequest updateRequest
    ) {
        Long profileId = profileRepository.findByMemberId(memberId).getId();
        final Profile profile = profileRepository.findByMemberId(memberId);

        profileTeamBuildingFieldRepository.deleteAllByProfileId(profileId);

        final List<TeamBuildingField> teamBuildingFields = teamBuildingFieldRepository
                .findTeamBuildingFieldsByFieldNames(updateRequest.getTeamBuildingFieldNames());

        // Request DTO -> 각 문자열을 TeamBuildingField 테이블에서 찾아서 가져옴
        final List<ProfileTeamBuildingField> profileTeamBuildingFields = teamBuildingFields.stream()
                .map(teamBuildingField -> new ProfileTeamBuildingField(null, profile, teamBuildingField))
                .toList();

        // profileTeamBuildingFieldRepository 모두 저장
        profileTeamBuildingFieldRepository.saveAll(profileTeamBuildingFields);

        List<String> teamBuildingFieldNames = teamBuildingFields.stream()
                .map(TeamBuildingField::getTeamBuildingFieldName)
                .toList();

        return new ProfileTeamBuildingFieldResponse(teamBuildingFieldNames);
    }
}
