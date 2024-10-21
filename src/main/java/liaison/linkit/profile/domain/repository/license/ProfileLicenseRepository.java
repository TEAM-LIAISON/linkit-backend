package liaison.linkit.profile.domain.repository.license;

import liaison.linkit.profile.domain.ProfileLicense;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileLicenseRepository extends JpaRepository<ProfileLicense, Long>, ProfileLicenseCustomRepository {

}
