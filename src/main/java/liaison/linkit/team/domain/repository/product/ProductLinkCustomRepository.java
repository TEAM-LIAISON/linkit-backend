package liaison.linkit.team.domain.repository.product;

import java.util.List;
import java.util.Map;
import liaison.linkit.team.domain.product.ProductLink;

public interface ProductLinkCustomRepository {
    Map<Long, List<ProductLink>> getProductLinksMap(final Long teamId);

    List<ProductLink> getProductLinks(final Long teamProductId);
}
