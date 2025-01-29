package liaison.linkit.team.domain.repository.product;

import java.util.List;
import liaison.linkit.team.domain.product.TeamProduct;
import liaison.linkit.team.domain.team.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamProductRepository extends JpaRepository<TeamProduct, Long>, TeamProductCustomRepository {
    List<TeamProduct> team(Team team);
}
