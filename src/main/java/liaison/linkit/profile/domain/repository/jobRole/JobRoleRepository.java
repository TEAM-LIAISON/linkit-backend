package liaison.linkit.profile.domain.repository.jobRole;

import liaison.linkit.profile.domain.role.JobRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRoleRepository extends JpaRepository<JobRole, Long>, JobRoleRepositoryCustom {

}
