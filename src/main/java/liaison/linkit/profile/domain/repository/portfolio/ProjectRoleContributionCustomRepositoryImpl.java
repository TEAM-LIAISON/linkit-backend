package liaison.linkit.profile.domain.repository.portfolio;

import com.querydsl.core.group.GroupBy;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Map;
import liaison.linkit.profile.domain.portfolio.ProjectRoleContribution;
import liaison.linkit.profile.domain.portfolio.QProfilePortfolio;
import liaison.linkit.profile.domain.portfolio.QProjectRoleContribution;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProjectRoleContributionCustomRepositoryImpl implements ProjectRoleContributionCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Map<Long, List<String>> getProjectRolesByProfileId(final Long profileId) {
        QProjectRoleContribution qProjectRoleContribution = QProjectRoleContribution.projectRoleContribution;
        QProfilePortfolio qProfilePortfolio = QProfilePortfolio.profilePortfolio;

        return jpaQueryFactory
                .select(qProfilePortfolio.id, qProjectRoleContribution.projectRole)
                .from(qProjectRoleContribution)
                .join(qProjectRoleContribution.portfolio, qProfilePortfolio)
                .where(qProfilePortfolio.profile.id.eq(profileId))
                .transform(GroupBy.groupBy(qProfilePortfolio.id).as(GroupBy.list(qProjectRoleContribution.projectRole)));
    }

    @Override
    public List<ProjectRoleContribution> getProjectRoleContributions(final Long profilePortfolioId) {
        QProjectRoleContribution qProjectRoleContribution = QProjectRoleContribution.projectRoleContribution;
        QProfilePortfolio qProfilePortfolio = QProfilePortfolio.profilePortfolio;

        return jpaQueryFactory
                .selectFrom(qProjectRoleContribution)
                .join(qProjectRoleContribution.portfolio, qProfilePortfolio)
                .where(qProfilePortfolio.id.eq(profilePortfolioId))
                .fetch();
    }

}
