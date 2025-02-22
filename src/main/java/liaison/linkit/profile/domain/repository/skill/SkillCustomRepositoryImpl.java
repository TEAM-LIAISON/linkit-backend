package liaison.linkit.profile.domain.repository.skill;

import java.util.List;
import java.util.Optional;

import com.querydsl.jpa.impl.JPAQueryFactory;
import liaison.linkit.profile.domain.skill.QSkill;
import liaison.linkit.profile.domain.skill.Skill;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class SkillCustomRepositoryImpl implements SkillCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Skill> findSkillsBySkillNames(List<String> skillNames) {
        QSkill qSkill = QSkill.skill;

        return jpaQueryFactory.selectFrom(qSkill).where(qSkill.skillName.in(skillNames)).fetch();
    }

    @Override
    public Optional<Skill> getSkillBySkillName(final String skillName) {
        QSkill qSkill = QSkill.skill;

        Skill skill =
                jpaQueryFactory.selectFrom(qSkill).where(qSkill.skillName.eq(skillName)).fetchOne();

        return Optional.ofNullable(skill);
    }
}
