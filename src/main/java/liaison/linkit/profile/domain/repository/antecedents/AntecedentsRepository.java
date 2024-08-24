package liaison.linkit.profile.domain.repository.antecedents;

import liaison.linkit.profile.domain.antecedents.Antecedents;
import org.springframework.data.jpa.repository.JpaRepository;


public interface AntecedentsRepository extends JpaRepository<Antecedents, Long>, AntecedentsRepositoryCustom{

}
