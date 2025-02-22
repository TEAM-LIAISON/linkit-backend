package liaison.linkit.team.implement.product;

import java.util.List;

import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.team.domain.product.ProductSubImage;
import liaison.linkit.team.domain.repository.product.ProductSubImageRepository;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class ProductSubImageQueryAdapter {
    private final ProductSubImageRepository productSubImageRepository;

    public List<ProductSubImage> getProductSubImages(final Long teamProductId) {
        return productSubImageRepository.getProductSubImages(teamProductId);
    }

    public List<String> getProductSubImagePaths(final Long teamProductId) {
        return productSubImageRepository.getProductSubImagePaths(teamProductId);
    }
}
