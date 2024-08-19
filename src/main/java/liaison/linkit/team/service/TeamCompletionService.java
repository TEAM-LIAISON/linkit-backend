package liaison.linkit.team.service;

import liaison.linkit.global.exception.BadRequestException;
import liaison.linkit.team.domain.TeamProfile;
import liaison.linkit.team.domain.repository.teamProfile.TeamProfileRepository;
import liaison.linkit.team.dto.response.completion.TeamCompletionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static liaison.linkit.global.exception.ExceptionCode.NOT_FOUND_TEAM_PROFILE_ID;

@Service
@RequiredArgsConstructor
@Transactional
public class TeamCompletionService {

    private final TeamProfileRepository teamProfileRepository;

    private TeamProfile getTeamProfile(final Long memberId) {
        return teamProfileRepository.findByMemberId(memberId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_TEAM_PROFILE_ID));
    }

    @Transactional(readOnly = true)
    public TeamCompletionResponse getTeamCompletion(final Long memberId) {
        final TeamProfile teamProfile = getTeamProfile(memberId);
        return TeamCompletionResponse.teamProfileCompletion(teamProfile);
    }
}
