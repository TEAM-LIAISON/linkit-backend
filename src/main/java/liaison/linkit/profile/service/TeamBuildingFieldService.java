package liaison.linkit.profile.service;

import static liaison.linkit.global.exception.ExceptionCode.NOT_FOUND_PROFILE_BY_ID;
import static liaison.linkit.global.exception.ExceptionCode.NOT_FOUND_PROFILE_BY_MEMBER_ID;
import static liaison.linkit.global.exception.ExceptionCode.NOT_FOUND_PROFILE_TEAM_BUILDING_FIELD_BY_PROFILE_ID;

import java.util.List;
import java.util.Optional;
import liaison.linkit.global.exception.AuthException;
import liaison.linkit.global.exception.BadRequestException;
import liaison.linkit.profile.domain.Profile;
import liaison.linkit.profile.domain.repository.profile.ProfileRepository;
import liaison.linkit.profile.domain.repository.teambuilding.ProfileTeamBuildingFieldRepository;
import liaison.linkit.profile.domain.repository.teambuilding.TeamBuildingFieldRepository;
import liaison.linkit.profile.domain.teambuilding.ProfileTeamBuildingField;
import liaison.linkit.profile.domain.teambuilding.TeamBuildingField;
import liaison.linkit.profile.dto.request.teamBuilding.ProfileTeamBuildingCreateRequest;
import liaison.linkit.profile.dto.response.teamBuilding.ProfileTeamBuildingFieldResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class TeamBuildingFieldService {

    private final ProfileRepository profileRepository;
    private final ProfileTeamBuildingFieldRepository profileTeamBuildingFieldRepository;
    private final TeamBuildingFieldRepository teamBuildingFieldRepository;

    // 모든 "내 이력서" 서비스 계층에 필요한 profile 조회 메서드
    private Profile getProfile(final Long memberId) {
        return profileRepository.findByMemberId(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_PROFILE_BY_MEMBER_ID));
    }

    // 유효성 검증
    public void validateProfileTeamBuildingFieldByMember(final Long memberId) {
        if (!profileTeamBuildingFieldRepository.existsByProfileId(getProfile(memberId).getId())) {
            throw new AuthException(NOT_FOUND_PROFILE_TEAM_BUILDING_FIELD_BY_PROFILE_ID);
        }
    }

    public void validateBrowseProfileTeamBuildingFieldByProfile(final Long profileId) {
        if (!profileTeamBuildingFieldRepository.existsByProfileId(profileId)) {
            throw new AuthException(NOT_FOUND_PROFILE_TEAM_BUILDING_FIELD_BY_PROFILE_ID);
        }
    }

    // validate 및 실제 비즈니스 로직 구분 라인 -------------------------------------------------------------

    // 희망 팀빌딩 분야 저장 비즈니스 로직
    public void save(final Long memberId, final ProfileTeamBuildingCreateRequest createRequest) {

        final Profile profile = getProfile(memberId);

        // 이미 저장된 이력이 존재하는 프로필의 경우 먼저 삭제한다.

        if (profileTeamBuildingFieldRepository.existsByProfileId(profile.getId())) {
            // 전체 삭제
            profileTeamBuildingFieldRepository.deleteAllByProfileId(profile.getId());
            // 삭제 이후 false 변경과 11.8 빼기 진행해줘야 함
            profile.updateIsProfileTeamBuildingField(false);
        }

        // 무조건 입력 값이 들어온다
        final List<TeamBuildingField> teamBuildingFields = teamBuildingFieldRepository
                .findTeamBuildingFieldsByFieldNames(createRequest.getTeamBuildingFieldNames());

        // Request DTO -> 각 문자열을 TeamBuildingField 테이블에서 찾아서 가져옴
        final List<ProfileTeamBuildingField> profileTeamBuildingFields = teamBuildingFields.stream()
                .map(teamBuildingField -> new ProfileTeamBuildingField(null, profile, teamBuildingField))
                .toList();

        // profileTeamBuildingFieldRepository 모두 저장
        profileTeamBuildingFieldRepository.saveAll(profileTeamBuildingFields);

        // 프로그레스바 처리 비즈니스 로직
        profile.updateIsProfileTeamBuildingField(true);
    }

    // 내 이력서에 선택한 희망 팀빌딩 분야 전체 조회
    @Transactional(readOnly = true)
    public ProfileTeamBuildingFieldResponse getAllProfileTeamBuildingFields(final Long memberId) {
        try {
            final Profile profile = getProfile(memberId);

            List<ProfileTeamBuildingField> profileTeamBuildingFields = profileTeamBuildingFieldRepository.findAllByProfileId(
                    profile.getId());

            List<String> teamBuildingFieldNames = profileTeamBuildingFields.stream()
                    .map(profileTeamBuildingField -> teamBuildingFieldRepository.findById(
                            profileTeamBuildingField.getTeamBuildingField().getId()))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .map(TeamBuildingField::getTeamBuildingFieldName)
                    .toList();

            return ProfileTeamBuildingFieldResponse.of(teamBuildingFieldNames);
        } catch (Exception e) {
            throw new BadRequestException(NOT_FOUND_PROFILE_TEAM_BUILDING_FIELD_BY_PROFILE_ID);
        }
    }

    @Transactional(readOnly = true)
    public ProfileTeamBuildingFieldResponse getAllBrowseProfileTeamBuildingFields(final Long profileId) {
        try {
            final Profile profile = profileRepository.findById(profileId)
                    .orElseThrow(() -> new BadRequestException(NOT_FOUND_PROFILE_BY_ID));

            List<ProfileTeamBuildingField> profileTeamBuildingFields = profileTeamBuildingFieldRepository.findAllByProfileId(
                    profile.getId());

            List<String> teamBuildingFieldNames = profileTeamBuildingFields.stream()
                    .map(profileTeamBuildingField -> teamBuildingFieldRepository.findById(
                            profileTeamBuildingField.getTeamBuildingField().getId()))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .map(TeamBuildingField::getTeamBuildingFieldName)
                    .toList();

            return ProfileTeamBuildingFieldResponse.of(teamBuildingFieldNames);
        } catch (Exception e) {
            throw new BadRequestException(NOT_FOUND_PROFILE_TEAM_BUILDING_FIELD_BY_PROFILE_ID);
        }

    }
}
