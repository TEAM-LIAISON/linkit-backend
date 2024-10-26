package liaison.linkit.profile.domain.repository.url;

import liaison.linkit.profile.domain.ProfileUrl;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileUrlRepository extends JpaRepository<ProfileUrl, Long>, ProfileUrlCustomRepository {
}
