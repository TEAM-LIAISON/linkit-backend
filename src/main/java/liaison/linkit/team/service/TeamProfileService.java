package liaison.linkit.team.service;

import liaison.linkit.global.exception.AuthException;
import liaison.linkit.global.exception.BadRequestException;
import liaison.linkit.team.domain.TeamProfile;
import liaison.linkit.team.domain.repository.TeamProfileRepository;
import liaison.linkit.team.dto.response.OnBoardingTeamProfileResponse;
import liaison.linkit.team.dto.response.TeamProfileOnBoardingIsValueResponse;
import liaison.linkit.team.dto.response.activity.ActivityResponse;
import liaison.linkit.team.dto.response.miniProfile.TeamMiniProfileResponse;
import liaison.linkit.team.dto.response.onBoarding.OnBoardingFirstResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static liaison.linkit.global.exception.ExceptionCode.INVALID_TEAM_PROFILE_WITH_MEMBER;
import static liaison.linkit.global.exception.ExceptionCode.NOT_FOUND_TEAM_PROFILE_ID;

@Service
@RequiredArgsConstructor
@Transactional
public class TeamProfileService {

    private final TeamProfileRepository teamProfileRepository;

    public Long validateTeamProfileByMember(final Long memberId) {
        if (!teamProfileRepository.existsByMemberId(memberId)) {
            throw new AuthException(INVALID_TEAM_PROFILE_WITH_MEMBER);
        } else {
            return teamProfileRepository.findByMemberId(memberId).getId();
        }
    }

    public TeamProfileOnBoardingIsValueResponse getTeamProfileOnBoardingIsValue(final Long teamProfileId) {
        final TeamProfile teamProfile = teamProfileRepository.findById(teamProfileId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_TEAM_PROFILE_ID));
        return TeamProfileOnBoardingIsValueResponse.teamProfileOnBoardingIsValue(teamProfile);
    }

    public OnBoardingTeamProfileResponse getOnBoardingTeamProfile(
            final OnBoardingFirstResponse onBoardingFirstResponse,
            final ActivityResponse activityResponse,
            final TeamMiniProfileResponse teamMiniProfileResponse
    ) {
        return OnBoardingTeamProfileResponse.onBoardingTeamProfileItems(
                onBoardingFirstResponse,
                activityResponse,
                teamMiniProfileResponse
        );
    }
}
