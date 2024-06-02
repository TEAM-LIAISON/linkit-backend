package liaison.linkit.team.service;

import liaison.linkit.global.exception.AuthException;
import liaison.linkit.profile.domain.repository.teambuilding.TeamBuildingFieldRepository;
import liaison.linkit.profile.domain.teambuilding.TeamBuildingField;
import liaison.linkit.team.domain.TeamProfile;
import liaison.linkit.team.domain.repository.TeamProfileRepository;
import liaison.linkit.team.domain.repository.teambuilding.TeamProfileTeamBuildingFieldRepository;
import liaison.linkit.team.domain.teambuilding.TeamProfileTeamBuildingField;
import liaison.linkit.team.dto.request.TeamProfileTeamBuildingFieldCreateRequest;
import liaison.linkit.team.dto.response.TeamProfileTeamBuildingFieldResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static liaison.linkit.global.exception.ExceptionCode.INVALID_TEAM_PROFILE_TEAM_BUILDING_WITH_MEMBER;

@Service
@RequiredArgsConstructor
@Transactional
public class TeamProfileTeamBuildingFieldService {
    private final TeamProfileRepository teamProfileRepository;
    private final TeamProfileTeamBuildingFieldRepository teamProfileTeamBuildingFieldRepository;
    private final TeamBuildingFieldRepository teamBuildingFieldRepository;


    public void validateTeamProfileTeamBuildingFieldByMember(final Long memberId) {
        final Long teamProfileId = teamProfileRepository.findByMemberId(memberId).getId();
        if (!teamProfileTeamBuildingFieldRepository.existsByTeamProfileId(teamProfileId)) {
            throw new AuthException(INVALID_TEAM_PROFILE_TEAM_BUILDING_WITH_MEMBER);
        }
    }

    @Transactional(readOnly = true)
    public TeamProfileTeamBuildingFieldResponse getAllTeamProfileTeamBuildingFields(final Long memberId) {
        Long teamProfileId = teamProfileRepository.findByMemberId(memberId).getId();

        List<TeamProfileTeamBuildingField> teamProfileTeamBuildingFields = teamProfileTeamBuildingFieldRepository.findAllByTeamProfileId(teamProfileId);

        List<String> teamBuildingFieldNames = teamProfileTeamBuildingFields.stream()
                .map(teamProfileTeamBuildingField -> teamBuildingFieldRepository.findById(teamProfileTeamBuildingField.getTeamBuildingField().getId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(TeamBuildingField::getTeamBuildingFieldName)
                .toList();

        return TeamProfileTeamBuildingFieldResponse.of(teamBuildingFieldNames);
    }

    public void save(final Long memberId, final TeamProfileTeamBuildingFieldCreateRequest createRequest) {
        final TeamProfile teamProfile = teamProfileRepository.findByMemberId(memberId);

        // 기존에 기입했었다면, 삭제 먼저
        if (teamProfileTeamBuildingFieldRepository.existsByTeamProfileId(teamProfile.getId())) {
            teamProfileTeamBuildingFieldRepository.deleteAllByTeamProfileId(teamProfile.getId());
        }

        final List<TeamBuildingField> teamBuildingFields = teamBuildingFieldRepository
                .findTeamBuildingFieldsByFieldNames(createRequest.getTeamBuildingFieldNames());

        final List<TeamProfileTeamBuildingField> teamProfileTeamBuildingFields = teamBuildingFields.stream()
                .map(teamBuildingField -> new TeamProfileTeamBuildingField(null, teamProfile, teamBuildingField))
                .toList();

        teamProfileTeamBuildingFieldRepository.saveAll(teamProfileTeamBuildingFields);

        // 존재 여부 변수 변경
        teamProfile.updateIsTeamProfileTeamBuildingField(true);
    }

    public void saveTeamBuildingField(
            final Long memberId,
            final List<String> teamBuildingFieldNames
    ) {
        final TeamProfile teamProfile = teamProfileRepository.findByMemberId(memberId);

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
}
