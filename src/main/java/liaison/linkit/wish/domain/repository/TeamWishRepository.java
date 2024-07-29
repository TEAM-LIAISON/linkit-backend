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

    @Query("SELECT tw FROM TeamWish tw WHERE tw.member.id = :memberId")
    List<TeamWish> findAllByMemberId(@Param("memberId") final Long memberId);

    @Modifying
    @Transactional
    @Query("DELETE FROM TeamWish tw WHERE tw.member.id = :memberId AND tw.teamMemberAnnouncement.id = :teamMemberAnnouncementId")
    void deleteByMemberIdAndTeamMemberAnnouncementId(@Param("memberId") final Long memberId, @Param("teamMemberAnnouncementId") final Long teamMemberAnnouncementId);

    @Query("SELECT tw FROM TeamWish tw WHERE tw.teamMemberAnnouncement.id = :teamMemberAnnouncementId")
    Optional<TeamWish> findByTeamMemberAnnouncementId(@Param("teamMemberAnnouncementId") final Long teamMemberAnnouncementId);

    @Query("SELECT COUNT(tw) > 0 FROM TeamWish tw WHERE tw.teamMemberAnnouncement.id = :teamMemberAnnouncementId AND tw.member.id = :memberId")
    boolean findByTeamMemberAnnouncementIdAndMemberId(@Param("teamMemberAnnouncementId") final Long teamMemberAnnouncementId, @Param("memberId") final Long memberId);

    @Modifying
    @Transactional
    @Query("""
           UPDATE TeamWish teamWish
           SET teamWish.status = 'DELETED'
           WHERE teamWish.member.id = :memberId
           """)
    void deleteByMemberId(@Param("memberId") final Long memberId);

    boolean existsByMemberId(@Param("memberId") final Long memberId);

    @Query("SELECT tw FROM TeamWish tw WHERE tw.teamMemberAnnouncement.id = :teamMemberAnnouncementId AND tw.member.id = :memberId")
    Optional<TeamWish> findByMemberIdAndTeamMemberAnnouncementId(final Long teamMemberAnnouncementId, final Long memberId);


    boolean existsByTeamMemberAnnouncementId(@Param("teamMemberAnnouncementId") final Long teamMemberAnnouncementId);

    @Modifying
    @Transactional
    @Query("""
           UPDATE TeamWish teamWish
           SET teamWish.status = 'DELETED'
           WHERE teamWish.teamMemberAnnouncement.id = :teamMemberAnnouncementId
           """)
    void deleteByTeamMemberAnnouncementId(@Param("teamMemberAnnouncementId") final Long teamMemberAnnouncementId);


    @Query("SELECT COUNT(tw) > 0 FROM TeamWish tw WHERE tw.teamMemberAnnouncement.id IN :teamMemberAnnouncementIds")
    boolean existsByTeamMemberAnnouncementIds(@Param("teamMemberAnnouncementIds") final List<Long> teamMemberAnnouncementIds);


    @Modifying
    @Transactional
    @Query("""
           UPDATE TeamWish teamWish
           SET teamWish.status = 'DELETED'
           WHERE teamWish.teamMemberAnnouncement.id IN :teamMemberAnnouncementIds
           """)
    void deleteByTeamMemberAnnouncementIds(@Param("teamMemberAnnouncementIds") final List<Long> teamMemberAnnouncementIds);

}
