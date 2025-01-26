package liaison.linkit.profile.domain.repository.log;

import liaison.linkit.profile.domain.log.ProfileLogImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileLogImageRepository extends JpaRepository<ProfileLogImage, Long>, ProfileLogImageCustomRepository {

}
