package liaison.linkit.team.domain.repository.product;

import liaison.linkit.team.domain.product.TeamProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamProductRepository extends JpaRepository<TeamProduct, Long>, TeamProductCustomRepository {
}
