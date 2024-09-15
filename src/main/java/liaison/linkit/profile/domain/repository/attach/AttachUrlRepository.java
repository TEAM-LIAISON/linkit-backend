package liaison.linkit.profile.domain.repository.attach;

import liaison.linkit.profile.domain.attach.AttachUrl;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttachUrlRepository extends JpaRepository<AttachUrl, Long>, AttachUrlRepositoryCustom{
    
}
