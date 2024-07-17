package liaison.linkit.wish.domain.repository;

import liaison.linkit.wish.domain.TeamWish;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface TeamWishRepository extends JpaRepository<TeamWish, Long> {

    @Query("SELECT tw FROM TeamWish tw WHERE tw.member.id = : memberId")
    List<TeamWish> findAllByMemberId(final Long memberId);

    @Modifying
    @Transactional
    @Query("DELETE FROM TeamWish tw WHERE tw.member.id = :memberId AND tw.teamMemberAnnouncement.id = :teamMemberAnnouncementId")
    void deleteByMemberIdAndTeamMemberAnnouncementId(@Param("memberId") final Long memberId, @Param("teamMemberAnnouncementId") final Long teamMemberAnnouncementId);

    @Query("SELECT tw FROM TeamWish tw WHERE tw.teamMemberAnnouncement.id = : teamMemberAnnouncementId")
    Optional<TeamWish> findByTeamMemberAnnouncementId(@Param("teamMemberAnnouncementId") final Long teamMemberAnnouncementId);

}
