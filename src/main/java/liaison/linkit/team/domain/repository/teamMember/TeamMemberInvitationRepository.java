package liaison.linkit.team.domain.repository.teamMember;

import liaison.linkit.team.domain.teamMember.TeamMemberInvitation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamMemberInvitationRepository extends JpaRepository<TeamMemberInvitation, Long>, TeamMemberInvitationCustomRepository {
}
