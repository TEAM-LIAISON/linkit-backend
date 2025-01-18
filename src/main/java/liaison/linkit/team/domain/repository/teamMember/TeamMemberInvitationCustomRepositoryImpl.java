package liaison.linkit.team.domain.repository.teamMember;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import liaison.linkit.team.domain.team.Team;
import liaison.linkit.team.domain.teamMember.QTeamMemberInvitation;
import liaison.linkit.team.domain.teamMember.TeamMemberInvitation;
import liaison.linkit.team.domain.teamMember.TeamMemberInviteState;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class TeamMemberInvitationCustomRepositoryImpl implements TeamMemberInvitationCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;


    @Override
    public boolean existsByEmail(final String email) {
        QTeamMemberInvitation qTeamMemberInvitation = QTeamMemberInvitation.teamMemberInvitation;

        return jpaQueryFactory
                .selectOne()
                .from(qTeamMemberInvitation)
                .where(qTeamMemberInvitation.teamMemberInvitationEmail.eq(email))
                .fetchFirst() != null;
    }

    @Override
    public boolean existsByEmailAndTeam(final String email, final Team team) {
        QTeamMemberInvitation qTeamMemberInvitation = QTeamMemberInvitation.teamMemberInvitation;

        return jpaQueryFactory
                .selectOne()
                .from(qTeamMemberInvitation)
                .where(qTeamMemberInvitation.teamMemberInvitationEmail.eq(email)
                        .and(qTeamMemberInvitation.team.id.eq(team.getId())))
                .fetchFirst() != null;
    }

    @Override
    public List<TeamMemberInvitation> getTeamMemberInvitations(final Long teamId) {
        QTeamMemberInvitation qTeamMemberInvitation = QTeamMemberInvitation.teamMemberInvitation;

        return jpaQueryFactory
                .selectFrom(qTeamMemberInvitation)
                .where(qTeamMemberInvitation.team.id.eq(teamId))
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
    public TeamMemberInvitation getTeamMemberInvitationInPendingState(final String email, final Team team) {
        QTeamMemberInvitation qTeamMemberInvitation = QTeamMemberInvitation.teamMemberInvitation;

        return jpaQueryFactory
                .selectFrom(qTeamMemberInvitation)
                .where(
                        qTeamMemberInvitation.teamMemberInvitationEmail.eq(email),
                        qTeamMemberInvitation.team.id.eq(team.getId())
                                .and(qTeamMemberInvitation.teamMemberInviteState.eq(TeamMemberInviteState.PENDING))
                )
                .fetchOne();
    }
}
