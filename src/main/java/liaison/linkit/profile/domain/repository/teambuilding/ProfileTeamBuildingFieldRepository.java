package liaison.linkit.profile.domain.repository.teambuilding;

import liaison.linkit.profile.domain.teambuilding.ProfileTeamBuildingField;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


public interface ProfileTeamBuildingFieldRepository extends JpaRepository<ProfileTeamBuildingField, Long> {

    boolean existsByProfileId(final Long profileId);

    @Query("SELECT profileTeamBuildingField FROM ProfileTeamBuildingField profileTeamBuildingField WHERE profileTeamBuildingField.profile.id = :profileId")
    List<ProfileTeamBuildingField> findAllByProfileId(@Param("profileId") final Long profileId);

    // 쿼리가 데이터베이스의 상태를 변경하는 (업데이트, 삭제) 메서드라는 것을 JPA에게 알려준다
    @Modifying
    @Transactional  // 메서드가 트랜잭션 내에서 실행되어야 함을 나타낸다.
    @Query("DELETE FROM ProfileTeamBuildingField profileTeamBuildingField WHERE profileTeamBuildingField.profile.id = :profileId")
    void deleteAllByProfileId(@Param("profileId") final Long profileId);
}
