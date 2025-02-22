package liaison.linkit.team.domain.repository.product;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import com.querydsl.jpa.impl.JPAQueryFactory;
import liaison.linkit.team.domain.product.QProductLink;
import liaison.linkit.team.domain.product.QProductSubImage;
import liaison.linkit.team.domain.product.QTeamProduct;
import liaison.linkit.team.domain.product.TeamProduct;
import liaison.linkit.team.presentation.product.dto.TeamProductRequestDTO.UpdateTeamProductRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class TeamProductCustomRepositoryImpl implements TeamProductCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @PersistenceContext private EntityManager entityManager; // EntityManager 주입

    @Override
    public List<TeamProduct> getTeamProducts(final Long teamId) {
        QTeamProduct qTeamProduct = QTeamProduct.teamProduct;

        return jpaQueryFactory
                .selectFrom(qTeamProduct)
                .where(qTeamProduct.team.id.eq(teamId))
                .fetch();
    }

    @Override
    public Optional<TeamProduct> getTeamProduct(final Long teamProductId) {
        QTeamProduct qTeamProduct = QTeamProduct.teamProduct;

        TeamProduct result =
                jpaQueryFactory
                        .selectFrom(qTeamProduct)
                        .where(qTeamProduct.id.eq(teamProductId))
                        .fetchOne();

        return Optional.ofNullable(result);
    }

    @Transactional
    @Override
    public TeamProduct updateTeamProduct(
            final TeamProduct teamProduct, final UpdateTeamProductRequest request) {
        QTeamProduct qTeamProduct = QTeamProduct.teamProduct;

        // 프로필 포트폴리오 업데이트
        long updatedCount =
                jpaQueryFactory
                        .update(qTeamProduct)
                        .set(qTeamProduct.productName, request.getProductName())
                        .set(
                                qTeamProduct.productLineDescription,
                                request.getProductLineDescription())
                        .set(qTeamProduct.productStartDate, request.getProductStartDate())
                        .set(qTeamProduct.productEndDate, request.getProductEndDate())
                        .set(qTeamProduct.isProductInProgress, request.getIsProductInProgress())
                        .set(qTeamProduct.productDescription, request.getProductDescription())
                        .where(qTeamProduct.id.eq(teamProduct.getId()))
                        .execute();

        entityManager.flush();
        entityManager.clear();

        if (updatedCount > 0) {
            return jpaQueryFactory
                    .selectFrom(qTeamProduct)
                    .where(qTeamProduct.id.eq(teamProduct.getId()))
                    .fetchOne();
        } else {
            return null;
        }
    }

    @Override
    public void deleteAllByTeamId(final Long teamId) {
        QTeamProduct qTeamProduct = QTeamProduct.teamProduct;
        QProductSubImage qSubImage = QProductSubImage.productSubImage; // 자식
        QProductLink qProductLink = QProductLink.productLink; // 자식

        // 1) teamId로 해당되는 TeamProduct ID 목록 조회
        List<Long> teamProductIds =
                jpaQueryFactory
                        .select(qTeamProduct.id)
                        .from(qTeamProduct)
                        .where(qTeamProduct.team.id.eq(teamId))
                        .fetch();

        if (teamProductIds.isEmpty()) {
            log.info("No TeamProduct found for teamId={}, skipping delete", teamId);
            return;
        }

        // 2) 자식 테이블1: ProductSubImage 삭제
        long subImageDeleteCount =
                jpaQueryFactory
                        .delete(qSubImage)
                        .where(qSubImage.teamProduct.id.in(teamProductIds))
                        .execute();
        log.info("Deleted {} ProductSubImage records for teamId={}", subImageDeleteCount, teamId);

        // 3) 자식 테이블2: ProductLink 삭제
        long linkDeleteCount =
                jpaQueryFactory
                        .delete(qProductLink)
                        .where(qProductLink.teamProduct.id.in(teamProductIds))
                        .execute();
        log.info("Deleted {} ProductLink records for teamId={}", linkDeleteCount, teamId);

        // 4) 이제 TeamProduct 삭제
        long deletedCount =
                jpaQueryFactory
                        .delete(qTeamProduct)
                        .where(qTeamProduct.team.id.eq(teamId))
                        .execute();

        log.info("Deleted {} TeamProduct records for teamId={}", deletedCount, teamId);

        entityManager.flush();
        entityManager.clear();
    }
}
