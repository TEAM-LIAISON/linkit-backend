package liaison.linkit.team.domain.repository.teamMember;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import liaison.linkit.team.domain.QTeam;

import liaison.linkit.team.domain.Team;
import liaison.linkit.team.domain.teamMember.QTeamMember;
import liaison.linkit.team.domain.teamMember.TeamMember;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class TeamMemberCustomRepositoryImpl implements TeamMemberCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public boolean existsTeamMemberByMemberIdAndTeamId(final Long memberId, final Long teamId) {
        QTeamMember qTeamMember = QTeamMember.teamMember;

        return jpaQueryFactory
                .selectOne()
                .from(qTeamMember)
                .where(qTeamMember.member.id.eq(memberId)
                        .and(qTeamMember.team.id.eq(teamId)))
                .fetchFirst() != null;
    }

    @Override
    public List<TeamMember> getTeamMembers(final Long teamId) {
        QTeamMember qTeamMember = QTeamMember.teamMember;

        return jpaQueryFactory
                .selectFrom(qTeamMember)
                .where(qTeamMember.team.id.eq(teamId))
                .fetch();
    }

    @Override
    public boolean isMemberOfTeam(Long teamId, Long memberId) {
        QTeamMember qTeamMember = QTeamMember.teamMember;

        return jpaQueryFactory.selectOne()
                .from(qTeamMember)
                .where(qTeamMember.team.id.eq(teamId)
                        .and(qTeamMember.member.id.eq(memberId)))
                .fetchFirst() != null;
    }

    @Override
    public boolean existsTeamByMemberId(final Long memberId) {
        QTeamMember qTeamMember = QTeamMember.teamMember;

        return jpaQueryFactory
                .selectOne()
                .from(qTeamMember)
                .where(qTeamMember.member.id.eq(memberId))
                .fetchFirst() != null;
    }

    @Override
    public List<Team> getAllTeamByMemberId(final Long memberId) {
        QTeamMember qTeamMember = QTeamMember.teamMember;
        QTeam qTeam = QTeam.team;

        return jpaQueryFactory
                .select(qTeam)
                .from(qTeamMember)
                .join(qTeamMember.team, qTeam)
                .where(qTeamMember.member.id.eq(memberId))
                .fetch();
    }

    @Override
    public List<Team> getAllTeamsByMemberId(final Long memberId) {
        QTeamMember qTeamMember = QTeamMember.teamMember;
        QTeam qTeam = QTeam.team;

        return jpaQueryFactory
                .select(qTeam)
                .from(qTeamMember)
                .join(qTeamMember.team, qTeam)
                .where(qTeamMember.member.id.eq(memberId))
                .fetch();
    }
}
