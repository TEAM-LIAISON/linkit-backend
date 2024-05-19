package liaison.linkit.team.service;

import liaison.linkit.profile.domain.repository.teambuilding.TeamBuildingFieldRepository;
import liaison.linkit.team.domain.repository.TeamProfileRepository;
import liaison.linkit.team.domain.repository.teambuilding.TeamProfileTeamBuildingFieldRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class TeamProfileTeamBuildingFieldService {
    private final TeamProfileRepository teamProfileRepository;
    private final TeamProfileTeamBuildingFieldRepository teamProfileTeamBuildingFieldRepository;
    private final TeamBuildingFieldRepository teamBuildingFieldRepository;


//    public void validateTeamProfileTeamBuildingFieldByMember(final Long memberId) {
//        final Long teamProfileId = teamProfileRepository.findByMemberId(memberId).getId();
//        if(!teamProfileTeamBuildingFieldRepository.ex)
//    }
}
