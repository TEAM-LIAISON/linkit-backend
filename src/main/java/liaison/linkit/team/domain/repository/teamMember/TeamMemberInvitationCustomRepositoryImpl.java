package liaison.linkit.team.domain.repository.teamMember;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import com.querydsl.jpa.impl.JPAQueryFactory;
import liaison.linkit.team.domain.team.Team;
import liaison.linkit.team.domain.teamMember.QTeamMemberInvitation;
import liaison.linkit.team.domain.teamMember.TeamMemberInvitation;
import liaison.linkit.team.domain.teamMember.TeamMemberInviteState;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class TeamMemberInvitationCustomRepositoryImpl
        implements TeamMemberInvitationCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @PersistenceContext private EntityManager entityManager; // EntityManager 주입

    @Override
    public boolean existsByEmail(final String email) {
        QTeamMemberInvitation qTeamMemberInvitation = QTeamMemberInvitation.teamMemberInvitation;

        return jpaQueryFactory
                        .selectOne()
                        .from(qTeamMemberInvitation)
                        .where(qTeamMemberInvitation.teamMemberInvitationEmail.eq(email))
                        .fetchFirst()
                != null;
    }

    @Override
    public boolean existsByEmailAndTeam(final String email, final Team team) {
        QTeamMemberInvitation qTeamMemberInvitation = QTeamMemberInvitation.teamMemberInvitation;

        return jpaQueryFactory
                        .selectOne()
                        .from(qTeamMemberInvitation)
                        .where(
                                qTeamMemberInvitation
                                        .teamMemberInvitationEmail
                                        .eq(email)
                                        .and(qTeamMemberInvitation.team.id.eq(team.getId())))
                        .fetchFirst()
                != null;
    }

    @Override
    public List<TeamMemberInvitation> getTeamMemberInvitationsInPending(final Long teamId) {
        QTeamMemberInvitation qTeamMemberInvitation = QTeamMemberInvitation.teamMemberInvitation;

        return jpaQueryFactory
                .selectFrom(qTeamMemberInvitation)
                .where(
                        qTeamMemberInvitation
                                .team
                                .id
                                .eq(teamId)
                                .and(
                                        qTeamMemberInvitation.teamMemberInviteState.eq(
                                                TeamMemberInviteState.PENDING)))
                .fetch();
    }

    @Override
    public List<Team> getTeamsByEmail(final String email) {
        QTeamMemberInvitation qTeamMemberInvitation = QTeamMemberInvitation.teamMemberInvitation;

        return jpaQueryFactory
                .select(qTeamMemberInvitation.team) // Team 객체를 선택
                .from(qTeamMemberInvitation)
                .where(qTeamMemberInvitation.teamMemberInvitationEmail.eq(email)) // 이메일 조건
                .fetch();
    }

    @Override
    public TeamMemberInvitation getTeamMemberInvitationInPendingState(
            final String email, final Team team) {
        QTeamMemberInvitation qTeamMemberInvitation = QTeamMemberInvitation.teamMemberInvitation;

        return jpaQueryFactory
                .selectFrom(qTeamMemberInvitation)
                .where(
                        qTeamMemberInvitation.teamMemberInvitationEmail.eq(email),
                        qTeamMemberInvitation
                                .team
                                .id
                                .eq(team.getId())
                                .and(
                                        qTeamMemberInvitation.teamMemberInviteState.eq(
                                                TeamMemberInviteState.PENDING)))
                .fetchOne();
    }

    @Override
    public TeamMemberInvitation getTeamMemberInvitationByTeamCodeAndEmail(
            final String teamCode, final String email) {
        QTeamMemberInvitation qTeamMemberInvitation = QTeamMemberInvitation.teamMemberInvitation;

        return jpaQueryFactory
                .selectFrom(qTeamMemberInvitation)
                .where(
                        qTeamMemberInvitation.teamMemberInvitationEmail.eq(email),
                        qTeamMemberInvitation.team.teamCode.eq(teamCode))
                .fetchOne();
    }

    @Override
    public void removeTeamMemberInvitation(final TeamMemberInvitation teamMemberInvitation) {
        QTeamMemberInvitation qTeamMemberInvitation = QTeamMemberInvitation.teamMemberInvitation;

        jpaQueryFactory
                .delete(qTeamMemberInvitation)
                .where(qTeamMemberInvitation.id.eq(teamMemberInvitation.getId()))
                .execute();
    }

    @Override
    public void deleteAllByTeamIds(final List<Long> teamIds) {
        QTeamMemberInvitation qTeamMemberInvitation = QTeamMemberInvitation.teamMemberInvitation;

        long deletedCount =
                jpaQueryFactory
                        .delete(qTeamMemberInvitation)
                        .where(qTeamMemberInvitation.team.id.in(teamIds))
                        .execute();

        entityManager.flush();
        entityManager.clear();
    }

    @Override
    public void deleteAllByTeamId(final Long teamId) {
        QTeamMemberInvitation qTeamMemberInvitation = QTeamMemberInvitation.teamMemberInvitation;

        long deletedCount =
                jpaQueryFactory
                        .delete(qTeamMemberInvitation)
                        .where(qTeamMemberInvitation.team.id.eq(teamId))
                        .execute();

        entityManager.flush();
        entityManager.clear();
    }
}
