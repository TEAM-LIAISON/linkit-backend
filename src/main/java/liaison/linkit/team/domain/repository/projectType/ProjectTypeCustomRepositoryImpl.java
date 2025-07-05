package liaison.linkit.team.domain.repository.projectType;

import java.util.Optional;

import com.querydsl.jpa.impl.JPAQueryFactory;
import liaison.linkit.team.domain.projectType.ProjectType;
import liaison.linkit.team.domain.projectType.QProjectType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ProjectTypeCustomRepositoryImpl implements ProjectTypeCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<ProjectType> findByProjectTypeName(final String projectTypeName) {
        final QProjectType qProjectType = QProjectType.projectType;

        return Optional.ofNullable(
                jpaQueryFactory
                        .selectFrom(qProjectType)
                        .where(qProjectType.projectTypeName.eq(projectTypeName))
                        .fetchOne());
    }
}
