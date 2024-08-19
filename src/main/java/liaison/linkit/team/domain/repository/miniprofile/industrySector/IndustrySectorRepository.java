package liaison.linkit.team.domain.repository.miniprofile.industrySector;

import liaison.linkit.team.domain.miniprofile.IndustrySector;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IndustrySectorRepository extends JpaRepository<IndustrySector, Long>, IndustrySectorRepositoryCustom {

}
