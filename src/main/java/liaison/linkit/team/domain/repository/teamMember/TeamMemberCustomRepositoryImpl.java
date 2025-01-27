package liaison.linkit.team.domain.repository.teamMember;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import liaison.linkit.global.type.StatusType;
import liaison.linkit.member.domain.Member;
import liaison.linkit.member.domain.QMember;
import liaison.linkit.team.domain.team.QTeam;
import liaison.linkit.team.domain.team.Team;
import liaison.linkit.team.domain.teamMember.QTeamMember;
import liaison.linkit.team.domain.teamMember.TeamMember;
import liaison.linkit.team.domain.teamMember.TeamMemberType;
import liaison.linkit.team.domain.teamMember.type.TeamMemberManagingTeamState;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class TeamMemberCustomRepositoryImpl implements TeamMemberCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @PersistenceContext
    private EntityManager entityManager; // EntityManager 주입

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
    public boolean isOwnerOrManagerOfTeam(final Long teamId, final Long memberId) {
        QTeamMember qTeamMember = QTeamMember.teamMember;

        return jpaQueryFactory.selectOne()
                .from(qTeamMember)
                .where(qTeamMember.team.id.eq(teamId)
                        .and(qTeamMember.member.id.eq(memberId))
                        .and(qTeamMember.teamMemberType.in(TeamMemberType.TEAM_OWNER, TeamMemberType.TEAM_MANAGER)))
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
                .where(qTeamMember.member.id.eq(memberId)
                        .and(qTeamMember.status.eq(StatusType.USABLE)))
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

    @Override
    public Member findTeamOwnerByTeamCode(final String teamCode) {
        QTeamMember qTeamMember = QTeamMember.teamMember;

        return jpaQueryFactory
                .select(qTeamMember.member)
                .from(qTeamMember)
                .where(qTeamMember.team.teamCode.eq(teamCode)
                        .and(qTeamMember.teamMemberType.eq(TeamMemberType.TEAM_OWNER)))
                .fetchOne();
    }

    @Override
    public boolean existsTeamMembersExceptOwnerByTeamCode(final String teamCode) {
        QTeamMember qTeamMember = QTeamMember.teamMember;

        return jpaQueryFactory
                .selectOne()
                .from(qTeamMember)
                .where(
                        qTeamMember.team.teamCode.eq(teamCode)
                                .and(qTeamMember.teamMemberType.eq(TeamMemberType.TEAM_MANAGER))
                )
                .fetchFirst() != null;
    }

    @Override
    public void removeTeamMemberInTeam(final TeamMember teamMember) {
        QTeamMember qTeamMember = QTeamMember.teamMember;

        jpaQueryFactory
                .delete(qTeamMember)
                .where(qTeamMember.id.eq(teamMember.getId()))
                .execute();
    }

    @Override
    public List<Long> getAllTeamMemberIds(final String teamCode) {
        QTeamMember qTeamMember = QTeamMember.teamMember;

        return jpaQueryFactory
                .select(qTeamMember.member.id)
                .from(qTeamMember)
                .where(qTeamMember.team.teamCode.eq(teamCode))
                .fetch();
    }

    @Override
    public void updateTeamMemberManagingTeamState(final TeamMember teamMember, final TeamMemberManagingTeamState teamMemberManagingTeamState) {
        QTeamMember qTeamMember = QTeamMember.teamMember;

        // QueryDSL을 사용하여 데이터베이스에서 ProfileLog 엔티티를 업데이트
        long updatedCount = jpaQueryFactory
                .update(qTeamMember)
                .set(qTeamMember.teamMemberManagingTeamState, teamMemberManagingTeamState)
                .where(qTeamMember.id.eq(teamMember.getId()))
                .execute();

        entityManager.flush();
        entityManager.clear();

        if (updatedCount > 0) { // 업데이트 성공 확인
            teamMember.setTeamMemberManagingTeamState(teamMemberManagingTeamState); // 메모리 내 객체 업데이트
        } else {
            throw new IllegalStateException("프로필 로그 업데이트 실패");
        }
    }

    @Override
    public boolean isMemberOfTeam(final String teamCode, final String emailId) {
        QTeamMember qTeamMember = QTeamMember.teamMember;
        // Query to check if a member with the given emailId is part of the team with the given teamCode
        return jpaQueryFactory
                .selectOne()
                .from(qTeamMember)
                .where(
                        qTeamMember.team.teamCode.eq(teamCode) // Team code matches
                                .and(qTeamMember.member.emailId.eq(emailId)) // Email ID matches
                                .and(qTeamMember.status.ne(StatusType.DELETED)) // Member is not in a deleted state
                )
                .fetchFirst() != null; // Return true if a result is found, false otherwise
    }

}
