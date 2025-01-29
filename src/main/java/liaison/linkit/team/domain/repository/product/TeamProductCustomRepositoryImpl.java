package liaison.linkit.team.domain.repository.product;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;
import liaison.linkit.team.domain.product.QTeamProduct;
import liaison.linkit.team.domain.product.TeamProduct;
import liaison.linkit.team.presentation.product.dto.TeamProductRequestDTO.UpdateTeamProductRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class TeamProductCustomRepositoryImpl implements TeamProductCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @PersistenceContext
    private EntityManager entityManager; // EntityManager 주입

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

        TeamProduct result = jpaQueryFactory
                .selectFrom(qTeamProduct)
                .where(qTeamProduct.id.eq(teamProductId))
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Transactional
    @Override
    public TeamProduct updateTeamProduct(final TeamProduct teamProduct, final UpdateTeamProductRequest request) {
        QTeamProduct qTeamProduct = QTeamProduct.teamProduct;

        // 프로필 포트폴리오 업데이트
        long updatedCount = jpaQueryFactory
                .update(qTeamProduct)
                .set(qTeamProduct.productName, request.getProductName())
                .set(qTeamProduct.productLineDescription, request.getProductLineDescription())
                .set(qTeamProduct.productStartDate, request.getProductStartDate())
                .set(qTeamProduct.productEndDate, request.getProductEndDate())
                .set(qTeamProduct.isProductInProgress, request.getIsProductInProgress())
                .set(qTeamProduct.productDescription, request.getProductDescription())
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

        long deletedCount = jpaQueryFactory
                .delete(qTeamProduct)
                .where(qTeamProduct.team.id.eq(teamId))
                .execute();

        entityManager.flush();
        entityManager.clear();
    }
}
