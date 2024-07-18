package liaison.linkit.wish.domain.repository;

import liaison.linkit.wish.domain.PrivateWish;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PrivateWishRepository extends JpaRepository<PrivateWish, Long> {

    @Query("SELECT pw FROM PrivateWish pw WHERE pw.member.id = :memberId")
    List<PrivateWish> findAllByMemberId(@Param("memberId") final Long memberId);

    @Modifying
    @Transactional
    @Query("DELETE FROM PrivateWish pw WHERE pw.member.id = :memberId AND pw.profile.id = :profileId")
    void deleteByMemberIdAndProfileId(@Param("memberId") final Long memberId, @Param("profileId") final Long profileId);
}
