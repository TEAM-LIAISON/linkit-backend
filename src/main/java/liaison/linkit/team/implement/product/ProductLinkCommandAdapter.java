package liaison.linkit.team.implement.product;

import java.util.List;
import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.team.domain.product.ProductLink;
import liaison.linkit.team.domain.repository.product.ProductLinkRepository;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class ProductLinkCommandAdapter {

    private final ProductLinkRepository productLinkRepository;

    public List<ProductLink> addProductLinks(final List<ProductLink> productLinks) {
        return productLinkRepository.saveAll(productLinks);
    }

    public void deleteAll(final List<ProductLink> productLinks) {
        productLinkRepository.deleteAll(productLinks);
    }
}
