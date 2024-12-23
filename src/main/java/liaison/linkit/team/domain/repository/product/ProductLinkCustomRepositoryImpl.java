package liaison.linkit.team.domain.repository.product;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import liaison.linkit.team.domain.product.ProductLink;
import liaison.linkit.team.domain.product.QProductLink;
import liaison.linkit.team.domain.product.QTeamProduct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProductLinkCustomRepositoryImpl implements ProductLinkCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Map<Long, List<ProductLink>> getProductLinksMap(final Long teamId) {
        QProductLink qProductLink = QProductLink.productLink;
        QTeamProduct qTeamProduct = QTeamProduct.teamProduct;

        // TeamProduct와 ProductLink 간의 조인을 명확히 설정
        List<Tuple> results = jpaQueryFactory
                .select(qTeamProduct.id, qProductLink)
                .from(qTeamProduct)
                .leftJoin(qProductLink).on(qProductLink.teamProduct.id.eq(qTeamProduct.id))
                .where(qTeamProduct.team.id.eq(teamId))
                .fetch();

        // Java Stream을 사용한 그룹핑
        Map<Long, List<ProductLink>> resultMap = results.stream()
                .collect(Collectors.groupingBy(
                        tuple -> tuple.get(qTeamProduct.id),
                        Collectors.mapping(
                                tuple -> tuple.get(qProductLink),
                                Collectors.filtering(
                                        productLink -> productLink != null,
                                        Collectors.toList()
                                )
                        )
                ));

        // ProductLink가 없는 경우 빈 리스트를 유지하도록 처리
        for (Tuple tuple : results) {
            Long teamProductId = tuple.get(qTeamProduct.id);
            resultMap.putIfAbsent(teamProductId, new ArrayList<>());
        }

        return resultMap;
    }


    @Override
    public List<ProductLink> getProductLinks(final Long teamProductId) {
        QProductLink qProductLink = QProductLink.productLink;
        QTeamProduct qTeamProduct = QTeamProduct.teamProduct;

        return jpaQueryFactory
                .selectFrom(qProductLink)
                .join(qProductLink.teamProduct, qTeamProduct)
                .where(qTeamProduct.id.eq(teamProductId))
                .fetch();
    }
}
