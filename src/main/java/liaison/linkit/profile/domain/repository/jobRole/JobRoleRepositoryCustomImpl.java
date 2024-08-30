package liaison.linkit.profile.domain.repository.jobRole;

import com.querydsl.jpa.impl.JPAQueryFactory;
import liaison.linkit.profile.domain.role.JobRole;
import liaison.linkit.profile.domain.role.QJobRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
public class JobRoleRepositoryCustomImpl implements JobRoleRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<JobRole> findJobRoleByJobRoleNames(List<String> jobRoleNames) {
        QJobRole jobRole = QJobRole.jobRole;

        return jpaQueryFactory
                .selectFrom(jobRole)
                .where(jobRole.jobRoleName.in(jobRoleNames))
                .fetch();
    }


    @Override
    public Optional<JobRole> findJobRoleByJobRoleName(String jobRoleName) {
        QJobRole jobRole = QJobRole.jobRole;

        JobRole result = jpaQueryFactory
                .selectFrom(jobRole)
                .where(jobRole.jobRoleName.eq(jobRoleName))
                .fetchOne();

        return Optional.ofNullable(result);
    }
}
