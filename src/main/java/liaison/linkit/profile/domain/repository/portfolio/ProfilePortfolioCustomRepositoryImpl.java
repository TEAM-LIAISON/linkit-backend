package liaison.linkit.profile.domain.repository.portfolio;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import liaison.linkit.profile.domain.portfolio.ProfilePortfolio;
import liaison.linkit.profile.domain.portfolio.QProfilePortfolio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProfilePortfolioCustomRepositoryImpl implements ProfilePortfolioCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<ProfilePortfolio> getProfilePortfolios(final Long profileId) {
        QProfilePortfolio qProfilePortfolio = QProfilePortfolio.profilePortfolio;

        return jpaQueryFactory
                .selectFrom(qProfilePortfolio)
                .where(qProfilePortfolio.profile.id.eq(profileId))
                .fetch();
    }

    @Override
    public Optional<ProfilePortfolio> getProfilePortfolio(final Long profilePortfolioId) {
        QProfilePortfolio qProfilePortfolio = QProfilePortfolio.profilePortfolio;

        ProfilePortfolio result = jpaQueryFactory
                .selectFrom(qProfilePortfolio)
                .where(qProfilePortfolio.id.eq(profilePortfolioId))
                .fetchOne();

        return Optional.ofNullable(result);
    }
}
