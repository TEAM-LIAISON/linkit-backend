package liaison.linkit.profile.domain.repository.jobRole;

import liaison.linkit.profile.domain.role.JobRole;

import java.util.List;
import java.util.Optional;

public interface JobRoleRepositoryCustom {
    List<JobRole> findJobRoleByJobRoleNames(final List<String> jobRoleNames);
    Optional<JobRole> findJobRoleByJobRoleName(final String jobRoleName);
}
