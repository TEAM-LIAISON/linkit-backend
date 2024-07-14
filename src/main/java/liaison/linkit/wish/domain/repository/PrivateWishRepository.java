package liaison.linkit.wish.domain.repository;

import liaison.linkit.wish.domain.PrivateWish;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PrivateWishRepository extends JpaRepository<PrivateWish, Long> {

    @Query("SELECT pw FROM PrivateWish pw WHERE pw.member.id = :memberId")
    List<PrivateWish> findAllByMemberId(@Param("memberId") final Long memberId);
}
