package liaison.linkit.profile.domain.repository.miniProfile;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import liaison.linkit.profile.domain.QProfile;
import liaison.linkit.profile.domain.miniProfile.QMiniProfile;
import liaison.linkit.profile.domain.region.QProfileRegion;
import liaison.linkit.profile.domain.region.QRegion;
import liaison.linkit.profile.domain.role.QJobRole;
import liaison.linkit.profile.domain.role.QProfileJobRole;
import liaison.linkit.profile.domain.skill.QProfileSkill;
import liaison.linkit.profile.domain.skill.QSkill;
import liaison.linkit.profile.domain.teambuilding.QProfileTeamBuildingField;
import liaison.linkit.profile.domain.teambuilding.QTeamBuildingField;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
public class MiniProfileRepositoryCustomImpl implements MiniProfileRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public boolean existsByProfileId(final Long profileId) {
        QMiniProfile miniProfile = QMiniProfile.miniProfile;

        Integer count = jpaQueryFactory
                .selectOne()
                .from(miniProfile)
                .where(miniProfile.profile.id.eq(profileId))
                .fetchFirst(); // fetchFirst()는 존재 여부를 확인하기 위해 사용됩니다.

        return count != null;
    }

    @Override
    public Optional<MiniProfile> findByProfileId(final Long profileId) {
        QMiniProfile miniProfile = QMiniProfile.miniProfile;

        MiniProfile result = jpaQueryFactory
                .selectFrom(miniProfile)
                .where(miniProfile.profile.id.eq(profileId))
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    @Transactional
    public void deleteByProfileId(final Long profileId) {
        QMiniProfile miniProfile = QMiniProfile.miniProfile;

        jpaQueryFactory
                .delete(miniProfile)
                .where(miniProfile.profile.id.eq(profileId))
                .execute();
    }

//    @Override
//    public Page<MiniProfile> findAll(
//            final List<String> teamBuildingFieldName,
//            final Long teamBuildingFieldCount,
//            final List<String> jobRoleName,
//            final Long jobRoleCount,
//            final List<String> skillName,
//            final Long skillCount,
//            final String cityName,
//            final String divisionName,
//            final Pageable pageable) {
//
//        QMiniProfile miniProfile = QMiniProfile.miniProfile;
//        QTeamBuildingField teamBuildingField = QTeamBuildingField.teamBuildingField;
//        QJobRole jobRole = QJobRole.jobRole;
//        QSkill skill = QSkill.skill;
//        QRegion region = QRegion.region;
//
//        JPQLQuery<MiniProfile> query = jpaQueryFactory.selectFrom(miniProfile)
//                .leftJoin(miniProfile.teamBuildingFields, teamBuildingField)
//                .leftJoin(miniProfile.jobRoles, jobRole)
//                .leftJoin(miniProfile.skills, skill)
//                .leftJoin(miniProfile.region, region)
//                .where(
//                        teamBuildingFieldName != null ? teamBuildingField.teamBuildingFieldName.in(teamBuildingFieldName) : null,
//                        jobRoleName != null ? jobRole.jobRoleName.in(jobRoleName) : null,
//                        skillName != null ? skill.skillName.in(skillName) : null,
//                        cityName != null ? region.cityName.eq(cityName) : null,
//                        divisionName != null ? region.divisionName.eq(divisionName) : null
//                )
//                .distinct();  // 중복 결과를 피하기 위해 필요
//
//        long total = query.fetchCount(); // 페이징을 위한 총 카운트
//        List<MiniProfile> results = query
//                .offset(pageable.getOffset())
//                .limit(pageable.getPageSize())
//                .fetch();
//
//        return new PageImpl<>(results, pageable, total);
//    }

    @Override
    public Slice<MiniProfile> searchAll(
            Long lastIndex,
            Pageable pageable,
            List<String> teamBuildingFieldName,
            List<String> jobRoleName,
            List<String> skillName,
            String cityName,
            String divisionName) {

        QMiniProfile mp = QMiniProfile.miniProfile;
        QProfile p = QProfile.profile;
        QProfileTeamBuildingField ptbf = QProfileTeamBuildingField.profileTeamBuildingField;
        QTeamBuildingField tbf = QTeamBuildingField.teamBuildingField;
        QProfileJobRole pjr = QProfileJobRole.profileJobRole;
        QJobRole jr = QJobRole.jobRole;
        QProfileSkill ps = QProfileSkill.profileSkill;
        QSkill s = QSkill.skill;
        QProfileRegion pr = QProfileRegion.profileRegion;
        QRegion r = QRegion.region;

        // 기본 쿼리 생성
        JPAQuery<MiniProfile> query = jpaQueryFactory.selectFrom(mp)
                .join(mp.profile, p)
                .leftJoin(ptbf).on(p.id.eq(ptbf.profile.id))
                .leftJoin(tbf).on(ptbf.teamBuildingField.id.eq(tbf.id))
                .leftJoin(pjr).on(p.id.eq(pjr.profile.id))
                .leftJoin(jr).on(pjr.jobRole.id.eq(jr.id))
                .leftJoin(ps).on(p.id.eq(ps.profile.id))
                .leftJoin(s).on(ps.skill.id.eq(s.id))
                .leftJoin(pr).on(p.id.eq(pr.profile.id))
                .leftJoin(r).on(pr.region.id.eq(r.id))
                .where(
                        mp.isActivate.isTrue(),
                        lastIndex != null ? mp.id.lt(lastIndex) : null, // lastIndex를 사용하여 페이지네이션 처리
                        isTeamBuildingFieldNameMatch(tbf, teamBuildingFieldName),
                        isJobRoleNameMatch(jr, jobRoleName),
                        isSkillNameMatch(s, skillName),
                        isCityNameMatch(r, cityName),
                        isDivisionNameMatch(r, divisionName)
                )
                .orderBy(mp.createdDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1); // Slice를 위한 limit + 1 설정

        List<MiniProfile> miniProfiles = query.fetch();

        boolean hasNext = miniProfiles.size() > pageable.getPageSize();

        if (hasNext) {
            miniProfiles.remove(pageable.getPageSize());
        }

        return new SliceImpl<>(miniProfiles, pageable, hasNext);
    }

    // 필터 조건 메서드
    private BooleanExpression isTeamBuildingFieldNameMatch(QTeamBuildingField tbf, List<String> teamBuildingFieldName) {
        if (teamBuildingFieldName == null || teamBuildingFieldName.isEmpty()) {
            return null;
        }
        return tbf.teamBuildingFieldName.in(teamBuildingFieldName);
    }

    private BooleanExpression isJobRoleNameMatch(QJobRole jr, List<String> jobRoleName) {
        if (jobRoleName == null || jobRoleName.isEmpty()) {
            return null;
        }
        return jr.jobRoleName.in(jobRoleName);
    }

    private BooleanExpression isSkillNameMatch(QSkill s, List<String> skillName) {
        if (skillName == null || skillName.isEmpty()) {
            return null;
        }
        return s.skillName.in(skillName);
    }

    private BooleanExpression isCityNameMatch(QRegion r, String cityName) {
        if (cityName == null) {
            return null;
        }
        return r.cityName.eq(cityName);
    }

    private BooleanExpression isDivisionNameMatch(QRegion r, String divisionName) {
        if (divisionName == null) {
            return null;
        }
        return r.divisionName.eq(divisionName);
    }

}
