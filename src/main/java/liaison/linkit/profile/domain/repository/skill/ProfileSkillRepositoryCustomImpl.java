package liaison.linkit.profile.domain.repository.skill;

import com.querydsl.jpa.impl.JPAQueryFactory;
import liaison.linkit.profile.domain.skill.QProfileSkill;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
public class ProfileSkillRepositoryCustomImpl implements ProfileSkillRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public boolean existsByProfileId(Long profileId) {
        QProfileSkill profileSkill = QProfileSkill.profileSkill;

        Integer count = jpaQueryFactory
                .selectOne()
                .from(profileSkill)
                .where(profileSkill.profile.id.eq(profileId))
                .fetchFirst();

        return count != null;
    }

    @Override
    public List<ProfileSkill> findAllByProfileId(Long profileId) {
        QProfileSkill profileSkill = QProfileSkill.profileSkill;

        return jpaQueryFactory
                .selectFrom(profileSkill)
                .where(profileSkill.profile.id.eq(profileId))
                .fetch();
    }

    @Override
    public Optional<ProfileSkill> findByProfileId(Long profileId) {
        QProfileSkill profileSkill = QProfileSkill.profileSkill;

        ProfileSkill result = jpaQueryFactory
                .selectFrom(profileSkill)
                .where(profileSkill.profile.id.eq(profileId))
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public void deleteAllByProfileId(Long profileId) {
        QProfileSkill profileSkill = QProfileSkill.profileSkill;

        jpaQueryFactory
                .delete(profileSkill)
                .where(profileSkill.profile.id.eq(profileId))
                .execute();
    }

}
