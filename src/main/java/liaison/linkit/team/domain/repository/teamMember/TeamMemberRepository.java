package liaison.linkit.team.domain.repository.teamMember;

import liaison.linkit.team.domain.teamMember.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Long>, TeamMemberCustomRepository {
    
}
