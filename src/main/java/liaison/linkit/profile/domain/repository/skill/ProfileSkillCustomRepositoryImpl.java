package liaison.linkit.profile.domain.repository.skill;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import liaison.linkit.profile.domain.skill.ProfileSkill;
import liaison.linkit.profile.domain.skill.QProfileSkill;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class ProfileSkillCustomRepositoryImpl implements ProfileSkillCustomRepository {

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
    public List<ProfileSkill> getProfileSkills(final Long memberId) {
        QProfileSkill qProfileSkill = QProfileSkill.profileSkill;

        return jpaQueryFactory
                .selectFrom(qProfileSkill)
                .where(qProfileSkill.profile.member.id.eq(memberId))
                .fetch();
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
