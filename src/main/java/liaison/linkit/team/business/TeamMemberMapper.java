package liaison.linkit.team.business;


import liaison.linkit.common.annotation.Mapper;
import liaison.linkit.member.domain.Member;
import liaison.linkit.team.domain.Team;
import liaison.linkit.team.domain.TeamMember;

@Mapper
public class TeamMemberMapper {
    public TeamMember toTeamMember(final Member member, final Team team) {
        return TeamMember.builder()
                .member(member)
                .team(team)
                .build();
    }
}


