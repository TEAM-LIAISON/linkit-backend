package liaison.linkit.profile.domain.repository.portfolio;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import liaison.linkit.profile.domain.portfolio.ProjectLink;
import liaison.linkit.profile.domain.portfolio.QProfilePortfolio;
import liaison.linkit.profile.domain.portfolio.QProjectLink;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ProjectLinkCustomRepositoryImpl implements ProjectLinkCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<ProjectLink> getProjectLinks(final Long profilePortfolioId) {
        QProjectLink qProjectLink = QProjectLink.projectLink;
        QProfilePortfolio qProfilePortfolio = QProfilePortfolio.profilePortfolio;

        return jpaQueryFactory
            .selectFrom(qProjectLink)
            .join(qProjectLink.profilePortfolio, qProfilePortfolio)
            .where(qProfilePortfolio.id.eq(profilePortfolioId))
            .fetch();
    }
}
