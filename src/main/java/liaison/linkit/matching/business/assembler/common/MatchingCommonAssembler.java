package liaison.linkit.matching.business.assembler.common;

import java.util.ArrayList;
import java.util.List;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.SenderTeamInformation;
import liaison.linkit.profile.business.mapper.ProfilePositionMapper;
import liaison.linkit.profile.domain.position.ProfilePosition;
import liaison.linkit.profile.domain.profile.Profile;
import liaison.linkit.profile.implement.position.ProfilePositionQueryAdapter;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO.ProfilePositionDetail;
import liaison.linkit.team.domain.team.Team;
import liaison.linkit.team.implement.teamMember.TeamMemberQueryAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MatchingCommonAssembler {

    private final ProfilePositionQueryAdapter profilePositionQueryAdapter;

    private final ProfilePositionMapper profilePositionMapper;
    private final TeamMemberQueryAdapter teamMemberQueryAdapter;


    private List<SenderTeamInformation> getSenderTeamInformationList(
        final Long memberId
    ) {

    }
}
