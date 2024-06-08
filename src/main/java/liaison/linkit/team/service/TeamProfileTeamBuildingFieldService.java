package liaison.linkit.team.service;

import liaison.linkit.global.exception.AuthException;
import liaison.linkit.global.exception.BadRequestException;
import liaison.linkit.profile.domain.repository.teambuilding.TeamBuildingFieldRepository;
import liaison.linkit.profile.domain.teambuilding.TeamBuildingField;
import liaison.linkit.team.domain.TeamProfile;
import liaison.linkit.team.domain.repository.TeamProfileRepository;
import liaison.linkit.team.domain.repository.teambuilding.TeamProfileTeamBuildingFieldRepository;
import liaison.linkit.team.domain.teambuilding.TeamProfileTeamBuildingField;
import liaison.linkit.team.dto.response.TeamProfileTeamBuildingFieldResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static liaison.linkit.global.exception.ExceptionCode.INVALID_TEAM_PROFILE_TEAM_BUILDING_WITH_MEMBER;
import static liaison.linkit.global.exception.ExceptionCode.NOT_FOUND_TEAM_PROFILE_BY_MEMBER_ID;

@Service
@RequiredArgsConstructor
@Transactional
public class TeamProfileTeamBuildingFieldService {
    private final TeamProfileRepository teamProfileRepository;
    private final TeamProfileTeamBuildingFieldRepository teamProfileTeamBuildingFieldRepository;
    private final TeamBuildingFieldRepository teamBuildingFieldRepository;

    private TeamProfile getTeamProfile(final Long memberId) {
        return teamProfileRepository.findByMemberId(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_TEAM_PROFILE_BY_MEMBER_ID));
    }

    public void validateTeamProfileTeamBuildingFieldByMember(final Long memberId) {
        final TeamProfile teamProfile = getTeamProfile(memberId);
        if (!teamProfileTeamBuildingFieldRepository.existsByTeamProfileId(teamProfile.getId())) {
            throw new AuthException(INVALID_TEAM_PROFILE_TEAM_BUILDING_WITH_MEMBER);
        }
    }

    // 저장 메서드
    public void saveTeamBuildingField(
            final Long memberId,
            final List<String> teamBuildingFieldNames
    ) {
        final TeamProfile teamProfile = getTeamProfile(memberId);

        if (teamProfileTeamBuildingFieldRepository.existsByTeamProfileId(teamProfile.getId())) {
            teamProfileTeamBuildingFieldRepository.deleteAllByTeamProfileId(teamProfile.getId());
        }

        final List<TeamBuildingField> teamBuildingFields = teamBuildingFieldRepository
                .findTeamBuildingFieldsByFieldNames(teamBuildingFieldNames);

        // Request DTO -> 각 문자열을 TeamBuildingField 테이블에서 찾아서 가져옴
        final List<TeamProfileTeamBuildingField> teamProfileTeamBuildingFields = teamBuildingFields.stream()
                .map(teamBuildingField -> new TeamProfileTeamBuildingField(null, teamProfile, teamBuildingField))
                .toList();

        teamProfileTeamBuildingFieldRepository.saveAll(teamProfileTeamBuildingFields);

        // 프로그레스바 처리 비즈니스 로직
        teamProfile.updateIsTeamProfileTeamBuildingField(true);
    }

    @Transactional(readOnly = true)
    public TeamProfileTeamBuildingFieldResponse getAllTeamProfileTeamBuildingFields(final Long memberId) {
        final TeamProfile teamProfile = getTeamProfile(memberId);

        List<TeamProfileTeamBuildingField> teamProfileTeamBuildingFields = teamProfileTeamBuildingFieldRepository.findAllByTeamProfileId(teamProfile.getId());

        List<String> teamBuildingFieldNames = teamProfileTeamBuildingFields.stream()
                .map(teamProfileTeamBuildingField -> teamBuildingFieldRepository.findById(teamProfileTeamBuildingField.getTeamBuildingField().getId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(TeamBuildingField::getTeamBuildingFieldName)
                .toList();

        return TeamProfileTeamBuildingFieldResponse.of(teamBuildingFieldNames);
    }


}
