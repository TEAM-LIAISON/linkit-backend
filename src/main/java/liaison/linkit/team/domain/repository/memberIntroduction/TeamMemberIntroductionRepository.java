package liaison.linkit.team.domain.repository.memberIntroduction;

import liaison.linkit.team.domain.memberIntroduction.TeamMemberIntroduction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamMemberIntroductionRepository extends JpaRepository<TeamMemberIntroduction, Long>, TeamMemberIntroductionRepositoryCustom {

}
