package liaison.linkit.team.service;

import liaison.linkit.global.exception.AuthException;
import liaison.linkit.global.exception.BadRequestException;
import liaison.linkit.team.domain.TeamProfile;
import liaison.linkit.team.domain.memberIntroduction.TeamMemberIntroduction;
import liaison.linkit.team.domain.repository.TeamProfileRepository;
import liaison.linkit.team.domain.repository.memberIntroduction.TeamMemberIntroductionRepository;
import liaison.linkit.team.dto.request.memberIntroduction.TeamMemberIntroductionCreateRequest;
import liaison.linkit.team.dto.response.TeamMemberIntroductionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static liaison.linkit.global.exception.ExceptionCode.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class TeamMemberIntroductionService {

    private final TeamProfileRepository teamProfileRepository;
    private final TeamMemberIntroductionRepository teamMemberIntroductionRepository;

    // 모든 "팀 소개서" 서비스 계층에 필요한 TeamProfile 조회 메서드
    private TeamProfile getTeamProfile(final Long memberId) {
        return teamProfileRepository.findByMemberId(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_TEAM_PROFILE_BY_MEMBER_ID));
    }

    private TeamMemberIntroduction getTeamMemberIntroduction(final Long teamMemberIntroductionId) {
        return teamMemberIntroductionRepository.findById(teamMemberIntroductionId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_TEAM_MEMBER_INTRODUCTION_ID));
    }

    public void validateTeamMemberIntroductionByMember(final Long memberId) {
        if (!teamMemberIntroductionRepository.existsByTeamProfileId(getTeamProfile(memberId).getId())) {
            throw new AuthException(INVALID_TEAM_INTRODUCTION_WITH_TEAM_PROFILE);
        }
    }

    public Long saveTeamMemberIntroduction(
            final Long memberId,
            final TeamMemberIntroductionCreateRequest teamMemberIntroductionCreateRequest
    ) {
        // 모든 입력 값이 존재하는 상태로 넘어옴
        final TeamProfile teamProfile = getTeamProfile(memberId);
        // 저장 메서드 실행
        if (teamProfile.getIsTeamMemberIntroduction()) {
            return saveTeamMemberIntroductionMethod(teamProfile, teamMemberIntroductionCreateRequest);
        } else {
            teamProfile.updateIsTeamMemberIntroduction(true);
            teamProfile.updateMemberTeamProfileTypeByCompletion();
            return saveTeamMemberIntroductionMethod(teamProfile, teamMemberIntroductionCreateRequest);
        }
    }

    // 팀원 소개 저장 메서드 실행부
    public void saveTeamMemberIntroductions(
            final Long memberId,
            final List<TeamMemberIntroductionCreateRequest> teamMemberIntroductionCreateRequests
    ) {
        final TeamProfile teamProfile = getTeamProfile(memberId);

        if (teamMemberIntroductionRepository.existsByTeamProfileId(teamProfile.getId())) {
            teamMemberIntroductionRepository.deleteAllByTeamProfileId(teamProfile.getId());
            teamProfile.updateIsTeamMemberIntroduction(false);
            teamProfile.updateMemberTeamProfileTypeByCompletion();
        }

        teamMemberIntroductionCreateRequests.forEach(request -> {
            saveTeamMemberIntroductionMethod(teamProfile, request);
        });

        teamProfile.updateIsTeamMemberIntroduction(true);
        teamProfile.updateMemberTeamProfileTypeByCompletion();
    }

    // 팀원 소개 저장 메서드
    private Long saveTeamMemberIntroductionMethod(
            final TeamProfile teamProfile,
            final TeamMemberIntroductionCreateRequest teamMemberIntroductionCreateRequest
    ) {
        final TeamMemberIntroduction newTeamMemberIntroduction = TeamMemberIntroduction.of(
                teamProfile,
                teamMemberIntroductionCreateRequest.getTeamMemberName(),
                teamMemberIntroductionCreateRequest.getTeamMemberRole(),
                teamMemberIntroductionCreateRequest.getTeamMemberIntroductionText()
        );

        return teamMemberIntroductionRepository.save(newTeamMemberIntroduction).getId();
    }

    // 팀원 소개 조회
    @Transactional(readOnly = true)
    public List<TeamMemberIntroductionResponse> getAllTeamMemberIntroduction(
            final Long memberId
    ) {
        final TeamProfile teamProfile = getTeamProfile(memberId);
        final List<TeamMemberIntroduction> teamMemberIntroductions = teamMemberIntroductionRepository.findAllByTeamProfileId(teamProfile.getId());
        return teamMemberIntroductions.stream()
                .map(this::getTeamMemberIntroductionResponse)
                .toList();
    }

    private TeamMemberIntroductionResponse getTeamMemberIntroductionResponse(final TeamMemberIntroduction teamMemberIntroduction) {
        return TeamMemberIntroductionResponse.of(teamMemberIntroduction);
    }

    public void deleteTeamMemberIntroduction(
            final Long memberId,
            final Long teamMemberIntroductionId
    ) {

        final TeamProfile teamProfile = getTeamProfile(memberId);
        final TeamMemberIntroduction teamMemberIntroduction = getTeamMemberIntroduction(teamMemberIntroductionId);

        // 해당 팀원 소개 삭제
        teamMemberIntroductionRepository.deleteById(teamMemberIntroduction.getId());

        // 존재하지 않는다면
        if (!teamMemberIntroductionRepository.existsByTeamProfileId(teamProfile.getId())) {
            teamProfile.updateIsTeamIntroduction(false);
            teamProfile.updateMemberTeamProfileTypeByCompletion();
        }
    }


    public Long updateTeamMemberIntroduction(
            final Long teamMemberIntroductionId,
            final TeamMemberIntroductionCreateRequest teamMemberIntroductionCreateRequest
    ) {
        final TeamMemberIntroduction teamMemberIntroduction = getTeamMemberIntroduction(teamMemberIntroductionId);
        teamMemberIntroduction.update(teamMemberIntroductionCreateRequest);
        return teamMemberIntroduction.getId();
    }
}
