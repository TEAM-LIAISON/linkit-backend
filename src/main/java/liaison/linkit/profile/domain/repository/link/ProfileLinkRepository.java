package liaison.linkit.profile.domain.repository.link;

import liaison.linkit.profile.domain.link.ProfileLink;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileLinkRepository extends JpaRepository<ProfileLink, Long>, ProfileLinkCustomRepository {
}
