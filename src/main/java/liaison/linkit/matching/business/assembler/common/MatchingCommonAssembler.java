package liaison.linkit.matching.business.assembler.common;

import liaison.linkit.profile.business.mapper.ProfilePositionMapper;
import liaison.linkit.profile.implement.position.ProfilePositionQueryAdapter;
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
    
}
