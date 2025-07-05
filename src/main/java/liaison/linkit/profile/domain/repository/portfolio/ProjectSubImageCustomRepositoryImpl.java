package liaison.linkit.profile.domain.repository.portfolio;

import java.util.List;

import com.querydsl.jpa.impl.JPAQueryFactory;
import liaison.linkit.profile.domain.portfolio.ProjectSubImage;
import liaison.linkit.profile.domain.portfolio.QProfilePortfolio;
import liaison.linkit.profile.domain.portfolio.QProjectSubImage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProjectSubImageCustomRepositoryImpl implements ProjectSubImageCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<String> getProjectSubImagePaths(final Long profilePortfolioId) {
        QProjectSubImage qProjectSubImage = QProjectSubImage.projectSubImage;

        return jpaQueryFactory
                .select(qProjectSubImage.projectSubImagePath)
                .from(qProjectSubImage)
                .where(qProjectSubImage.profilePortfolio.id.eq(profilePortfolioId))
                .fetch();
    }

    @Override
    public List<ProjectSubImage> getProjectSubImages(final Long profilePortfolioId) {
        QProjectSubImage qProjectSubImage = QProjectSubImage.projectSubImage;
        QProfilePortfolio qProfilePortfolio = QProfilePortfolio.profilePortfolio;

        return jpaQueryFactory
                .selectFrom(qProjectSubImage)
                .join(qProjectSubImage.profilePortfolio, qProfilePortfolio)
                .where(qProfilePortfolio.id.eq(profilePortfolioId))
                .fetch();
    }
}
