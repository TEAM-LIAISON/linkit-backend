package liaison.linkit.team.domain.repository.memberIntroduction;

import com.querydsl.jpa.impl.JPAQueryFactory;
import liaison.linkit.team.domain.memberIntroduction.QTeamMemberIntroduction;
import liaison.linkit.team.domain.memberIntroduction.TeamMemberIntroduction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class TeamMemberIntroductionRepositoryCustomImpl implements TeamMemberIntroductionRepositoryCustom{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public boolean existsByTeamProfileId(final Long teamProfileId) {
        QTeamMemberIntroduction teamMemberIntroduction = QTeamMemberIntroduction.teamMemberIntroduction;

        Integer count = jpaQueryFactory
                .selectOne()
                .from(teamMemberIntroduction)
                .where(teamMemberIntroduction.teamProfile.id.eq(teamProfileId))
                .fetchFirst();

        return count != null;
    }

    @Override
    @Transactional
    public void deleteAllByTeamProfileId(final Long teamProfileId) {
        QTeamMemberIntroduction teamMemberIntroduction = QTeamMemberIntroduction.teamMemberIntroduction;

        jpaQueryFactory
                .delete(teamMemberIntroduction)
                .where(teamMemberIntroduction.teamProfile.id.eq(teamProfileId))
                .execute();
    }

    @Override
    public List<TeamMemberIntroduction> findAllByTeamProfileId(final Long teamProfileId) {
        QTeamMemberIntroduction teamMemberIntroduction = QTeamMemberIntroduction.teamMemberIntroduction;

        return jpaQueryFactory
                .selectFrom(teamMemberIntroduction)
                .where(teamMemberIntroduction.teamProfile.id.eq(teamProfileId))
                .fetch();
    }
}
