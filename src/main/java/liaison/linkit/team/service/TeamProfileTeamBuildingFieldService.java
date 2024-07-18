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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static liaison.linkit.global.exception.ExceptionCode.INVALID_TEAM_PROFILE_TEAM_BUILDING_WITH_MEMBER;
import static liaison.linkit.global.exception.ExceptionCode.NOT_FOUND_TEAM_PROFILE_BY_MEMBER_ID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
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
        // 팀 소개서 객체 조회
        final TeamProfile teamProfile = getTeamProfile(memberId);

        log.info("팀 소개서 희망 팀빌딩 분야 실행 part1");
        if (teamProfileTeamBuildingFieldRepository.existsByTeamProfileId(teamProfile.getId())) {
            teamProfileTeamBuildingFieldRepository.deleteAllByTeamProfileId(teamProfile.getId());
        }

        log.info("팀 소개서 희망 팀빌딩 분야 실행 part2 (삭제 완료)");
        final List<TeamBuildingField> teamBuildingFields = teamBuildingFieldRepository
                .findTeamBuildingFieldsByFieldNames(teamBuildingFieldNames);

        // Request DTO -> 각 문자열을 TeamBuildingField 테이블에서 찾아서 가져옴
        final List<TeamProfileTeamBuildingField> teamProfileTeamBuildingFields = teamBuildingFields.stream()
                .map(teamBuildingField -> new TeamProfileTeamBuildingField(null, teamProfile, teamBuildingField))
                .toList();

        // 팀 프로필 팀 희망 팀빌딩 분야 저장
        teamProfileTeamBuildingFieldRepository.saveAll(teamProfileTeamBuildingFields);

        // 프로그레스바 처리 비즈니스 로직
        teamProfile.updateIsTeamProfileTeamBuildingField(true);
        log.info("팀 소개서 희망 팀빌딩 분야 실행 part3 (저장 완료)");
    }

    @Transactional(readOnly = true)
    public TeamProfileTeamBuildingFieldResponse getAllTeamProfileTeamBuildingFields(final Long memberId) {
        final TeamProfile teamProfile = getTeamProfile(memberId);

        final List<TeamProfileTeamBuildingField> teamProfileTeamBuildingFields = teamProfileTeamBuildingFieldRepository.findAllByTeamProfileId(teamProfile.getId());

        List<String> teamBuildingFieldNames = teamProfileTeamBuildingFields.stream()
                .map(teamProfileTeamBuildingField -> teamBuildingFieldRepository.findById(teamProfileTeamBuildingField.getTeamBuildingField().getId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(TeamBuildingField::getTeamBuildingFieldName)
                .toList();

        return TeamProfileTeamBuildingFieldResponse.of(teamBuildingFieldNames);
    }


}
