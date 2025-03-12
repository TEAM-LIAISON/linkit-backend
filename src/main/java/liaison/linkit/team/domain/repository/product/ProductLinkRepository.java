package liaison.linkit.team.domain.repository.product;

import liaison.linkit.team.domain.product.ProductLink;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductLinkRepository
        extends JpaRepository<ProductLink, Long>, ProductLinkCustomRepository {}
