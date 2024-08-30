package liaison.linkit.team.domain.repository.attach.teamAttachUrl;

import liaison.linkit.team.domain.attach.TeamAttachUrl;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamAttachUrlRepository extends JpaRepository<TeamAttachUrl, Long>, TeamAttachUrlRepositoryCustom {

}
