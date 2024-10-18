package liaison.linkit.profile.domain.repository.miniProfile;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MiniProfileRepository extends JpaRepository<MiniProfile, Long>, MiniProfileRepositoryCustom {

    //    boolean existsByProfileId(final Long profileId);
//
//    Optional<MiniProfile> findByProfileId(@Param("profileId") final Long profileId);
//
//    @Modifying
//    @Transactional // 메서드가 트랜잭션 내에서 실행되어야 함을 나타낸다.
//    @Query("DELETE FROM MiniProfile miniProfile WHERE miniProfile.profile.id = :profileId")
//    void deleteByProfileId(@Param("profileId") final Long profileId);
//
//
    @Query(value = """
            SELECT mp FROM MiniProfile mp
            JOIN mp.profile p

            LEFT JOIN ProfileTeamBuildingField ptbf ON p.id = ptbf.profile.id
            LEFT JOIN TeamBuildingField tbf ON ptbf.teamBuildingField.id = tbf.id

            LEFT JOIN ProfileJobRole pjr ON p.id = pjr.profile.id
            LEFT JOIN JobRole jr ON pjr.jobRole.id = jr.id

            LEFT JOIN ProfileSkill ps ON p.id = ps.profile.id
            LEFT JOIN Skill s ON ps.skill.id = s.id

            LEFT JOIN ProfileRegion pr ON p.id = pr.profile.id
            LEFT JOIN Region r ON pr.region.id = r.id

            WHERE (:teamBuildingFieldName IS NULL OR
                  (SELECT COUNT(tbf.teamBuildingFieldName) FROM TeamBuildingField tbf
                   JOIN ProfileTeamBuildingField ptbf2 ON tbf.id = ptbf2.teamBuildingField.id
                   WHERE ptbf2.profile.id = p.id AND tbf.teamBuildingFieldName IN :teamBuildingFieldName) = :teamBuildingFieldCount)
            AND (:jobRoleName IS NULL OR
                 (SELECT COUNT(jr.jobRoleName) FROM JobRole jr
                  JOIN ProfileJobRole pjr2 ON jr.id = pjr2.jobRole.id
                  WHERE pjr2.profile.id = p.id AND jr.jobRoleName IN :jobRoleName) = :jobRoleCount)
            AND (:skillName IS NULL OR
                 (SELECT COUNT(s.skillName) FROM Skill s
                  JOIN ProfileSkill ps2 ON s.id = ps2.skill.id
                  WHERE ps2.profile.id = p.id AND s.skillName IN :skillName) = :skillCount)
            AND (:cityName IS NULL OR r.cityName = :cityName)
            AND (:divisionName IS NULL OR r.divisionName = :divisionName)
            AND (mp.isActivate = true)

            ORDER BY mp.createdDate DESC
            """)
    Page<MiniProfile> findAll(
            @Param("teamBuildingFieldName") final List<String> teamBuildingFieldName,
            @Param("teamBuildingFieldCount") final Long teamBuildingFieldCount,
            @Param("jobRoleName") final List<String> jobRoleName,
            @Param("jobRoleCount") final Long jobRoleCount,
            @Param("skillName") final List<String> skillName,
            @Param("skillCount") final Long skillCount,
            @Param("cityName") final String cityName,
            @Param("divisionName") final String divisionName,
            final Pageable pageable
    );
}
