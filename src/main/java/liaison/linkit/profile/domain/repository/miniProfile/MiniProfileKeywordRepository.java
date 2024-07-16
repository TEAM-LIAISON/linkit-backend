package liaison.linkit.profile.domain.repository.miniProfile;

import liaison.linkit.profile.domain.miniProfile.MiniProfileKeyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface MiniProfileKeywordRepository extends JpaRepository<MiniProfileKeyword, Long> {

    @Query("SELECT miniProfileKeyword FROM MiniProfileKeyword miniProfileKeyword WHERE miniProfileKeyword.miniProfile.id = :miniProfileId")
    List<MiniProfileKeyword> findAllByMiniProfileId(@Param("miniProfileId") final Long miniProfileId);

    @Modifying
    @Transactional // 메서드가 트랜잭션 내에서 실행되어야 함을 나타낸다.
    @Query("DELETE FROM MiniProfileKeyword mk WHERE mk.miniProfile.id = :miniProfileId")
    void deleteAllByMiniProfileId(@Param("miniProfileId") final Long miniProfileId);
}
