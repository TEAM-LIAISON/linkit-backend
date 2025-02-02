package liaison.linkit.team.implement.product;

import java.util.List;
import liaison.linkit.common.annotation.Adapter;
import liaison.linkit.team.domain.product.ProductSubImage;
import liaison.linkit.team.domain.repository.product.ProductSubImageRepository;
import lombok.RequiredArgsConstructor;

@Adapter
@RequiredArgsConstructor
public class ProductSubImageCommandAdapter {
    private final ProductSubImageRepository productSubImageRepository;


    public void saveAll(final List<ProductSubImage> productSubImages) {
        productSubImageRepository.saveAll(productSubImages);
    }

    public void deleteAll(final List<ProductSubImage> productSubImages) {
        productSubImageRepository.deleteAll(productSubImages);
    }

    public void delete(final ProductSubImage productSubImage) {
        productSubImageRepository.delete(productSubImage);
    }
}
