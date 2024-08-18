package liaison.linkit.wish.domain.repository.teamWish;

import liaison.linkit.wish.domain.TeamWish;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamWishRepository extends JpaRepository<TeamWish, Long>, TeamWishRepositoryCustom {

}
