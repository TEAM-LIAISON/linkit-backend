package liaison.linkit.wish.domain.repository;

import liaison.linkit.wish.domain.TeamWish;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TeamWishRepository extends JpaRepository<TeamWish, Long> {

    @Query("SELECT tw FROM TeamWish tw WHERE tw.member.id = : memberId")
    List<TeamWish> findAllByMemberId(final Long memberId);

}
