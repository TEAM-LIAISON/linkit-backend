package liaison.linkit.profile.domain.repository.portfolio;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;
import liaison.linkit.profile.domain.portfolio.ProfilePortfolio;
import liaison.linkit.profile.domain.portfolio.QProfilePortfolio;
import liaison.linkit.profile.domain.portfolio.QProjectRoleContribution;
import liaison.linkit.profile.domain.portfolio.QProjectSkill;
import liaison.linkit.profile.domain.portfolio.QProjectSubImage;
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

    @PersistenceContext
    private EntityManager entityManager; // EntityManager 주입

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

    @Transactional
    @Override
    public ProfilePortfolio updateProfilePortfolio(final ProfilePortfolio profilePortfolio, final UpdateProfilePortfolioRequest updateProfilePortfolioRequest) {
        QProfilePortfolio qProfilePortfolio = QProfilePortfolio.profilePortfolio;

        // 프로필 포트폴리오 업데이트
        long updatedCount = jpaQueryFactory
            .update(qProfilePortfolio)
            .set(qProfilePortfolio.projectName, updateProfilePortfolioRequest.getProjectName())
            .set(qProfilePortfolio.projectLineDescription, updateProfilePortfolioRequest.getProjectLineDescription())
            .set(qProfilePortfolio.projectSize, updateProfilePortfolioRequest.getProjectSize())
            .set(qProfilePortfolio.projectHeadCount, updateProfilePortfolioRequest.getProjectHeadCount())
            .set(qProfilePortfolio.projectTeamComposition, updateProfilePortfolioRequest.getProjectTeamComposition())
            .set(qProfilePortfolio.projectStartDate, updateProfilePortfolioRequest.getProjectStartDate())
            .set(qProfilePortfolio.projectEndDate, updateProfilePortfolioRequest.getProjectEndDate())
            .set(qProfilePortfolio.isProjectInProgress, updateProfilePortfolioRequest.getIsProjectInProgress())
            .set(qProfilePortfolio.projectDescription, updateProfilePortfolioRequest.getProjectDescription())
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
            .fetchFirst() != null;
    }

    @Override
    public void removeProfilePortfoliosByProfileId(final Long profileId) {
        QProfilePortfolio qProfilePortfolio = QProfilePortfolio.profilePortfolio;
        QProjectRoleContribution qRoleContribution = QProjectRoleContribution.projectRoleContribution;
        QProjectSkill qProjectSkill = QProjectSkill.projectSkill;
        QProjectSubImage qProjectSubImage = QProjectSubImage.projectSubImage;

        // 1) profileId로 해당 ProfilePortfolio 목록의 ID를 조회
        List<Long> portfolioIds = jpaQueryFactory
            .select(qProfilePortfolio.id)
            .from(qProfilePortfolio)
            .where(qProfilePortfolio.profile.id.eq(profileId))
            .fetch();

        if (portfolioIds.isEmpty()) {
            log.info("No ProfilePortfolio found for profileId={}, skipping deletion", profileId);
            return;
        }

        // 2) ProjectRoleContribution 삭제 (자식)
        long deletedRoleCount = jpaQueryFactory
            .delete(qRoleContribution)
            .where(qRoleContribution.profilePortfolio.id.in(portfolioIds))
            .execute();
        log.info("Deleted {} ProjectRoleContribution records for profileId={}", deletedRoleCount, profileId);

        // 3) ProjectSkill 삭제 (자식)
        long deletedSkillCount = jpaQueryFactory
            .delete(qProjectSkill)
            .where(qProjectSkill.portfolio.id.in(portfolioIds))
            .execute();
        log.info("Deleted {} ProjectSkill records for profileId={}", deletedSkillCount, profileId);

        // 4) ProjectSubImage 삭제 (자식)
        long deletedSubImageCount = jpaQueryFactory
            .delete(qProjectSubImage)
            .where(qProjectSubImage.profilePortfolio.id.in(portfolioIds))
            .execute();
        log.info("Deleted {} ProjectSubImage records for profileId={}", deletedSubImageCount, profileId);

        // 5) ProfilePortfolio 삭제 (부모)
        long deletedPortfolioCount = jpaQueryFactory
            .delete(qProfilePortfolio)
            .where(qProfilePortfolio.profile.id.eq(profileId))
            .execute();
        log.info("Deleted {} ProfilePortfolio records for profileId={}", deletedPortfolioCount, profileId);

        entityManager.flush();
        entityManager.clear();
    }

}
