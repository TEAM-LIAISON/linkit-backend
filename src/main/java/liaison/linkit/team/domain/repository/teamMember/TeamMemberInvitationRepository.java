package liaison.linkit.team.domain.repository.teamMember;

import java.util.List;
import liaison.linkit.team.domain.team.Team;
import liaison.linkit.team.domain.teamMember.TeamMemberInvitation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamMemberInvitationRepository extends JpaRepository<TeamMemberInvitation, Long>, TeamMemberInvitationCustomRepository {
    List<TeamMemberInvitation> team(Team team);
}
