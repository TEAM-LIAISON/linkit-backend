package liaison.linkit.team.domain.repository.teamScale;

import liaison.linkit.team.domain.scale.Scale;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScaleRepository extends JpaRepository<Scale, Long>, ScaleCustomRepository {

}
