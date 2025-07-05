package liaison.linkit.team.implement.product;

import java.util.List;
import java.util.Map;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.team.domain.product.ProductLink;
import liaison.linkit.team.domain.repository.product.ProductLinkRepository;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class ProductLinkQueryAdapter {

    private final ProductLinkRepository productLinkRepository;

    public Map<Long, List<ProductLink>> getProductLinksMap(final Long teamId) {
        return productLinkRepository.getProductLinksMap(teamId);
    }

    public List<ProductLink> getProductLinks(final Long teamProductId) {
        return productLinkRepository.getProductLinks(teamProductId);
    }
}
