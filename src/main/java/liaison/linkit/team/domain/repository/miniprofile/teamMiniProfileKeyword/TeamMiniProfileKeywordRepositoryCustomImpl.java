package liaison.linkit.team.domain.repository.miniprofile.teamMiniProfileKeyword;

import com.querydsl.jpa.impl.JPAQueryFactory;
import liaison.linkit.team.domain.miniprofile.QTeamMiniProfileKeyword;
import liaison.linkit.team.domain.miniprofile.TeamMiniProfileKeyword;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class TeamMiniProfileKeywordRepositoryCustomImpl implements TeamMiniProfileKeywordRepositoryCustom{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<TeamMiniProfileKeyword> findAllByTeamMiniProfileId(final Long teamMiniProfileId) {
        QTeamMiniProfileKeyword teamMiniProfileKeyword = QTeamMiniProfileKeyword.teamMiniProfileKeyword;

        return jpaQueryFactory
                .selectFrom(teamMiniProfileKeyword)
                .where(teamMiniProfileKeyword.teamMiniProfile.id.eq(teamMiniProfileId))
                .fetch();
    }

    @Override
    @Transactional
    public void deleteAllByTeamMiniProfileId(final Long teamMiniProfileId) {
        QTeamMiniProfileKeyword teamMiniProfileKeyword = QTeamMiniProfileKeyword.teamMiniProfileKeyword;

        jpaQueryFactory
                .delete(teamMiniProfileKeyword)
                .where(teamMiniProfileKeyword.teamMiniProfile.id.eq(teamMiniProfileId))
                .execute();
    }
}
