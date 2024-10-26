package liaison.linkit.profile.domain.repository.log;

import liaison.linkit.profile.domain.ProfileLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileLogRepository extends JpaRepository<ProfileLog, Long>, ProfileLogCustomRepository {
}
