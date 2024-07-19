package liaison.linkit.profile.domain.repository.jobRole;

import liaison.linkit.profile.domain.role.JobRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface JobRoleRepository extends JpaRepository<JobRole, Long> {
    @Query("""
           SELECT jr
           FROM JobRole jr
           WHERE jr.jobRoleName in :jobRoleNames
           """)
    List<JobRole> findJobRoleByJobRoleNames(@Param("jobRoleNames") final List<String> jobRoleNames);

    @Query("""
           SELECT jr
           FROM JobRole jr
           WHERE jr.jobRoleName = :jobRoleName
           """)
    Optional<JobRole> findJobRoleByJobRoleName(@Param("jobRoleName") final String jobRoleName);
}
