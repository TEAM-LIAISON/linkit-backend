package liaison.linkit.matching.domain.repository;

import liaison.linkit.matching.domain.PrivateMatching;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PrivateMatchingRepository extends JpaRepository<PrivateMatching, Long> {

    @Query("SELECT pm FROM PrivateMatching pm WHERE pm.profile.id = :profileId AND pm.matchingStatusType = 'REQUESTED'")
    List<PrivateMatching> findByProfileIdAndMatchingStatus(@Param("profileId") final Long profileId);

    @Query("SELECT pm FROM PrivateMatching pm WHERE pm.member.id = :memberId AND pm.matchingStatusType = 'REQUESTED' AND pm.receiverDeleteStatusType = 'REMAINED'")
    List<PrivateMatching> findByMemberIdAndMatchingStatus(@Param("memberId") final Long memberId);

    @Query("SELECT pm FROM PrivateMatching pm WHERE pm.matchingStatusType = 'SUCCESSFUL' AND pm.profile.id = :profileId")
    List<PrivateMatching> findSuccessReceivedMatching(@Param("profileId") final Long profileId);

    @Query("SELECT pm FROM PrivateMatching pm WHERE pm.matchingStatusType = 'SUCCESSFUL' AND pm.member.id = :memberId")
    List<PrivateMatching> findSuccessRequestMatching(@Param("memberId") final Long memberId);

    boolean existsByProfileId(@Param("profileId") final Long profileId);

    @Modifying
    @Transactional
    @Query("""
           UPDATE PrivateMatching privateMatching
           SET privateMatching.status = 'DELETED'
           WHERE privateMatching.member.id = :memberId
           """)
    void deleteByMemberId(@Param("memberId") final Long memberId);

    boolean existsByMemberId(@Param("memberId") final Long memberId);

    @Modifying
    @Transactional
    @Query("""
           UPDATE PrivateMatching privateMatching
           SET privateMatching.status = 'DELETED'
           WHERE privateMatching.profile.id = :profileId
           """)
    void deleteByProfileId(@Param("profileId") final Long profileId);


    @Query("""
           SELECT COUNT(pm) > 0 FROM PrivateMatching pm
           WHERE (pm.member.id = :memberId AND pm.isSenderCheck = false)
           OR (pm.profile.id = :profileId AND pm.isReceiverCheck = false)
           """)
    boolean existsNonCheckByMemberId(final Long memberId, final Long profileId);

}
