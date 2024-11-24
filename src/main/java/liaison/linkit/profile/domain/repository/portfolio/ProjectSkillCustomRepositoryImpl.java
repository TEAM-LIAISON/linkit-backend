package liaison.linkit.profile.domain.repository.portfolio;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import liaison.linkit.profile.domain.portfolio.ProjectSkill;
import liaison.linkit.profile.domain.portfolio.QProfilePortfolio;
import liaison.linkit.profile.domain.portfolio.QProjectSkill;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProjectSkillCustomRepositoryImpl implements ProjectSkillCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<ProjectSkill> getProjectSkills(final Long profilePortfolioId) {
        QProjectSkill qProjectSkill = QProjectSkill.projectSkill;
        QProfilePortfolio qProfilePortfolio = QProfilePortfolio.profilePortfolio;

        return jpaQueryFactory
                .selectFrom(qProjectSkill)
                .join(qProjectSkill.portfolio, qProfilePortfolio)
                .where(qProfilePortfolio.id.eq(profilePortfolioId))
                .fetch();
    }
}
