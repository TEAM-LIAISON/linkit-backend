package liaison.linkit.team.service;

import liaison.linkit.global.exception.AuthException;
import liaison.linkit.global.exception.BadRequestException;
import liaison.linkit.team.domain.TeamProfile;
import liaison.linkit.team.domain.memberIntroduction.TeamMemberIntroduction;
import liaison.linkit.team.domain.repository.TeamProfileRepository;
import liaison.linkit.team.domain.repository.memberIntroduction.TeamMemberIntroductionRepository;
import liaison.linkit.team.dto.request.memberIntroduction.TeamMemberIntroductionCreateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static liaison.linkit.global.exception.ExceptionCode.INVALID_TEAM_INTRODUCTION_WITH_TEAM_PROFILE;
import static liaison.linkit.global.exception.ExceptionCode.NOT_FOUND_TEAM_PROFILE_BY_MEMBER_ID;

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

    public void validateTeamMemberIntroductionByMember(final Long memberId) {
        if (!teamMemberIntroductionRepository.existsByTeamProfileId(getTeamProfile(memberId).getId())) {
            throw new AuthException(INVALID_TEAM_INTRODUCTION_WITH_TEAM_PROFILE);
        }
    }

    public void saveTeamMember(
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
            saveTeamMemberIntroduction(teamProfile, request);
        });

        teamProfile.updateIsTeamMemberIntroduction(true);
        teamProfile.updateMemberTeamProfileTypeByCompletion();
    }

    private void saveTeamMemberIntroduction(
            final TeamProfile teamProfile,
            final TeamMemberIntroductionCreateRequest teamMemberIntroductionCreateRequest
    ) {
        final TeamMemberIntroduction newTeamMemberIntroduction = TeamMemberIntroduction.of(
                teamProfile,
                teamMemberIntroductionCreateRequest.getTeamMemberName(),
                teamMemberIntroductionCreateRequest.getTeamMemberRole(),
                teamMemberIntroductionCreateRequest.getTeamMemberIntroductionText()
        );

        teamMemberIntroductionRepository.save(newTeamMemberIntroduction);
    }
}
