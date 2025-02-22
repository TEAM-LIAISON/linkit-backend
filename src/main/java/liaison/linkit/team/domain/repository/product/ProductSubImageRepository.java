package liaison.linkit.team.domain.repository.product;

import liaison.linkit.team.domain.product.ProductSubImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductSubImageRepository
        extends JpaRepository<ProductSubImage, Long>, ProductSubImageCustomRepository {}
