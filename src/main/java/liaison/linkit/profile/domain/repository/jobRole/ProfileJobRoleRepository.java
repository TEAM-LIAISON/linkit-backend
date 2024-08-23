package liaison.linkit.profile.domain.repository.jobRole;

import liaison.linkit.profile.domain.role.ProfileJobRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileJobRoleRepository extends JpaRepository<ProfileJobRole, Long>, ProfileJobRoleRepositoryCustom {

}
