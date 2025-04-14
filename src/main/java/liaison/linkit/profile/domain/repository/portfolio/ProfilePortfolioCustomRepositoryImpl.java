package liaison.linkit.profile.domain.repository.portfolio;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import com.querydsl.jpa.impl.JPAQueryFactory;
import liaison.linkit.profile.domain.portfolio.ProfilePortfolio;
import liaison.linkit.profile.domain.portfolio.QProfilePortfolio;
import liaison.linkit.profile.presentation.portfolio.dto.ProfilePortfolioRequestDTO.UpdateProfilePortfolioRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ProfilePortfolioCustomRepositoryImpl implements ProfilePortfolioCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @PersistenceContext private EntityManager entityManager; // EntityManager 주입

    @Override
    public List<ProfilePortfolio> getProfilePortfolios(final Long profileId) {
        QProfilePortfolio qProfilePortfolio = QProfilePortfolio.profilePortfolio;

        return jpaQueryFactory
                .selectFrom(qProfilePortfolio)
                .where(qProfilePortfolio.profile.id.eq(profileId))
                .orderBy(qProfilePortfolio.projectStartDate.desc())
                .fetch();
    }

    @Override
    public Optional<ProfilePortfolio> getProfilePortfolio(final Long profilePortfolioId) {
        QProfilePortfolio qProfilePortfolio = QProfilePortfolio.profilePortfolio;

        ProfilePortfolio result =
                jpaQueryFactory
                        .selectFrom(qProfilePortfolio)
                        .where(qProfilePortfolio.id.eq(profilePortfolioId))
                        .fetchOne();

        return Optional.ofNullable(result);
    }

    @Transactional
    @Override
    public ProfilePortfolio updateProfilePortfolio(
            final ProfilePortfolio profilePortfolio,
            final UpdateProfilePortfolioRequest updateProfilePortfolioRequest) {
        QProfilePortfolio qProfilePortfolio = QProfilePortfolio.profilePortfolio;

        // 프로필 포트폴리오 업데이트
        long updatedCount =
                jpaQueryFactory
                        .update(qProfilePortfolio)
                        .set(
                                qProfilePortfolio.projectName,
                                updateProfilePortfolioRequest.getProjectName())
                        .set(
                                qProfilePortfolio.projectLineDescription,
                                updateProfilePortfolioRequest.getProjectLineDescription())
                        .set(
                                qProfilePortfolio.projectSize,
                                updateProfilePortfolioRequest.getProjectSize())
                        .set(
                                qProfilePortfolio.projectHeadCount,
                                updateProfilePortfolioRequest.getProjectHeadCount())
                        .set(
                                qProfilePortfolio.projectTeamComposition,
                                updateProfilePortfolioRequest.getProjectTeamComposition())
                        .set(
                                qProfilePortfolio.projectStartDate,
                                updateProfilePortfolioRequest.getProjectStartDate())
                        .set(
                                qProfilePortfolio.projectEndDate,
                                updateProfilePortfolioRequest.getProjectEndDate())
                        .set(
                                qProfilePortfolio.isProjectInProgress,
                                updateProfilePortfolioRequest.getIsProjectInProgress())
                        .set(
                                qProfilePortfolio.projectDescription,
                                updateProfilePortfolioRequest.getProjectDescription())
                        .where(qProfilePortfolio.id.eq(profilePortfolio.getId()))
                        .execute();

        entityManager.flush();
        entityManager.clear();

        if (updatedCount > 0) {
            return jpaQueryFactory
                    .selectFrom(qProfilePortfolio)
                    .where(qProfilePortfolio.id.eq(profilePortfolio.getId()))
                    .fetchOne();
        } else {
            return null;
        }
    }

    @Override
    public boolean existsByProfileId(final Long profileId) {
        QProfilePortfolio qProfilePortfolio = QProfilePortfolio.profilePortfolio;

        return jpaQueryFactory
                        .selectOne()
                        .from(qProfilePortfolio)
                        .where(qProfilePortfolio.profile.id.eq(profileId))
                        .fetchFirst()
                != null;
    }
}
