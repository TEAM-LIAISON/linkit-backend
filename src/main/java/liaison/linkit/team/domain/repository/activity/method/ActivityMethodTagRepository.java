package liaison.linkit.team.domain.repository.activity.method;

import liaison.linkit.team.domain.activity.ActivityMethodTag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityMethodTagRepository extends JpaRepository<ActivityMethodTag, Long>, ActivityMethodTagRepositoryCustom {

}
