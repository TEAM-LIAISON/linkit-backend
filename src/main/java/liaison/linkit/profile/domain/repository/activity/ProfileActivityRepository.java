package liaison.linkit.profile.domain.repository.activity;

import liaison.linkit.profile.domain.ProfileActivity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileActivityRepository extends JpaRepository<ProfileActivity, Long>, ProfileActivityCustomRepository {
}
