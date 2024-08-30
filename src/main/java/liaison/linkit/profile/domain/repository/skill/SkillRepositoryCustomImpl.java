package liaison.linkit.profile.domain.repository.skill;

import com.querydsl.jpa.impl.JPAQueryFactory;
import liaison.linkit.profile.domain.skill.QSkill;
import liaison.linkit.profile.domain.skill.Skill;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class SkillRepositoryCustomImpl implements SkillRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Skill> findSkillsBySkillNames(List<String> skillNames) {
        QSkill skill = QSkill.skill;

        return jpaQueryFactory
                .selectFrom(skill)
                .where(skill.skillName.in(skillNames))
                .fetch();
    }
}
