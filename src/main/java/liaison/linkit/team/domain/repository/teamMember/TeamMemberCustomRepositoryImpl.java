package liaison.linkit.team.domain.repository.teamMember;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import liaison.linkit.member.domain.Member;
import liaison.linkit.member.domain.QMember;

import liaison.linkit.team.domain.team.QTeam;
import liaison.linkit.team.domain.team.Team;
import liaison.linkit.team.domain.teamMember.QTeamMember;
import liaison.linkit.team.domain.teamMember.TeamMember;
import liaison.linkit.team.domain.teamMember.TeamMemberType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class TeamMemberCustomRepositoryImpl implements TeamMemberCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<TeamMember> getTeamMembers(final Long teamId) {
        QTeamMember qTeamMember = QTeamMember.teamMember;

        return jpaQueryFactory
                .selectFrom(qTeamMember)
                .where(qTeamMember.team.id.eq(teamId))
                .fetch();
    }

    @Override
    public Long getTeamOwnerMemberId(final Team team) {
        QTeamMember qTeamMember = QTeamMember.teamMember;

        return jpaQueryFactory
                .select(qTeamMember.member.id)
                .from(qTeamMember)
                .where(
                        qTeamMember.team.eq(team) // 팀 조건
                                .and(qTeamMember.teamMemberType.eq(TeamMemberType.TEAM_OWNER)) // 오너 조건
                )
                .fetchOne(); // 단일 결과 반환
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
    public boolean existsTeamOwnerByMemberId(final Long memberId) {
        QTeamMember qTeamMember = QTeamMember.teamMember;

        return jpaQueryFactory
                .selectOne()
                .from(qTeamMember)
                .where(qTeamMember.member.id.eq(memberId)
                        .and(qTeamMember.teamMemberType.eq(TeamMemberType.TEAM_OWNER)))
                .fetchFirst() != null;
    }

    @Override
    public List<Team> getAllTeamsByMemberId(final Long memberId) {
        QTeamMember qTeamMember = QTeamMember.teamMember;
        QTeam qTeam = QTeam.team;

        return jpaQueryFactory
                .select(qTeam).distinct()
                .from(qTeamMember)
                .join(qTeamMember.team, qTeam)
                .where(qTeamMember.member.id.eq(memberId))
                .fetch();
    }

    @Override
    public List<Team> getAllTeamsInOwnerStateByMemberId(final Long memberId) {
        QTeamMember qTeamMember = QTeamMember.teamMember;
        QTeam qTeam = QTeam.team;

        return jpaQueryFactory
                .select(qTeam)
                .from(qTeamMember)
                .join(qTeamMember.team, qTeam)
                .where(qTeamMember.member.id.eq(memberId)
                        .and(qTeamMember.teamMemberType.eq(TeamMemberType.TEAM_OWNER))
                )
                .fetch();
    }

    @Override
    public TeamMember getTeamMemberByTeamCodeAndEmailId(
            final String teamCode,
            final String emailId
    ) {
        QTeamMember qTeamMember = QTeamMember.teamMember;
        QMember qMember = QMember.member;

        return jpaQueryFactory
                .selectFrom(qTeamMember)
                .join(qTeamMember.member, qMember)
                .where(
                        qTeamMember.team.teamCode.eq(teamCode),
                        qMember.emailId.eq(emailId)
                )
                .fetchOne();
    }

    @Override
    public List<Member> findMembersByTeamCode(final String teamCode) {
        QTeamMember qTeamMember = QTeamMember.teamMember;
        QMember qMember = QMember.member;

        return jpaQueryFactory
                .select(qMember)
                .from(qTeamMember)
                .join(qTeamMember.member, qMember)
                .where(qTeamMember.team.teamCode.eq(teamCode))
                .fetch();
    }
}
