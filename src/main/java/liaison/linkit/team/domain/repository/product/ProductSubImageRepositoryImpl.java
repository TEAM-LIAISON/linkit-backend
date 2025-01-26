package liaison.linkit.team.domain.repository.product;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import liaison.linkit.team.domain.product.ProductSubImage;
import liaison.linkit.team.domain.product.QProductSubImage;
import liaison.linkit.team.domain.product.QTeamProduct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProductSubImageRepositoryImpl implements ProductSubImageCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<String> getProductSubImagePaths(final Long teamProductId) {
        QProductSubImage qProductSubImage = QProductSubImage.productSubImage;

        return jpaQueryFactory
                .select(qProductSubImage.productSubImagePath)
                .from(qProductSubImage)
                .where(qProductSubImage.teamProduct.id.eq(teamProductId))
                .fetch();
    }

    @Override
    public List<ProductSubImage> getProductSubImages(final Long teamProductId) {
        QProductSubImage qProductSubImage = QProductSubImage.productSubImage;
        QTeamProduct qTeamProduct = QTeamProduct.teamProduct;

        return jpaQueryFactory
                .selectFrom(qProductSubImage)
                .join(qProductSubImage.teamProduct, qTeamProduct)
                .where(qTeamProduct.id.eq(teamProductId))
                .fetch();
    }
}
