package liaison.linkit.profile.domain.repository.miniProfile;

import liaison.linkit.profile.domain.miniProfile.MiniProfileKeyword;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MiniProfileKeywordRepository extends JpaRepository<MiniProfileKeyword, Long>, MiniProfileKeywordRepositoryCustom {

}
