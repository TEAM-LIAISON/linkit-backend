package liaison.linkit.wish.domain.repository;

import liaison.linkit.wish.domain.Wish;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WishRepository extends JpaRepository<Wish, Long> {

}
