package liaison.linkit.team.domain.repository.activity.method;

import liaison.linkit.team.domain.activity.ActivityMethod;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityMethodRepository extends JpaRepository<ActivityMethod, Long>, ActivityMethodRepositoryCustom {

}
