package liaison.linkit.team.domain.repository.attach.teamAttachUrl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import liaison.linkit.team.domain.attach.QTeamAttachUrl;
import liaison.linkit.team.domain.attach.TeamAttachUrl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class TeamAttachUrlRepositoryCustomImpl implements TeamAttachUrlRepositoryCustom{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public boolean existsByTeamProfileId(Long teamProfileId) {
        QTeamAttachUrl teamAttachUrl = QTeamAttachUrl.teamAttachUrl;

        Integer count = jpaQueryFactory
                .selectOne()
                .from(teamAttachUrl)
                .where(teamAttachUrl.teamProfile.id.eq(teamProfileId))
                .fetchFirst();

        return count != null;
    }

    @Override
    public List<TeamAttachUrl> findAllByTeamProfileId(Long teamProfileId) {
        QTeamAttachUrl teamAttachUrl = QTeamAttachUrl.teamAttachUrl;

        return jpaQueryFactory
                .selectFrom(teamAttachUrl)
                .where(teamAttachUrl.teamProfile.id.eq(teamProfileId))
                .fetch();
    }

    @Override
    @Transactional
    public void deleteAllByTeamProfileId(Long teamProfileId) {
        QTeamAttachUrl teamAttachUrl = QTeamAttachUrl.teamAttachUrl;

        jpaQueryFactory
                .delete(teamAttachUrl)
                .where(teamAttachUrl.teamProfile.id.eq(teamProfileId))
                .execute();
    }
}
