package liaison.linkit.team.domain.repository.product;

import java.util.List;

import liaison.linkit.team.domain.product.ProductSubImage;

public interface ProductSubImageCustomRepository {
    List<String> getProductSubImagePaths(final Long teamProductId);

    List<ProductSubImage> getProductSubImages(final Long teamProductId);
}
