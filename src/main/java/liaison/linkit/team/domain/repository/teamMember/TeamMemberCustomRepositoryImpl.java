package liaison.linkit.team.domain.repository.teamMember;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import liaison.linkit.global.type.StatusType;
import liaison.linkit.member.domain.Member;
import liaison.linkit.member.domain.QMember;
import liaison.linkit.profile.domain.profile.QProfile;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO.ProfileTeamInform;
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

    @PersistenceContext private EntityManager entityManager; // EntityManager 주입

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
                        qTeamMember
                                .team
                                .eq(team) // 팀 조건
                                .and(
                                        qTeamMember.teamMemberType.eq(
                                                TeamMemberType.TEAM_OWNER)) // 오너 조건
                        )
                .fetchOne(); // 단일 결과 반환
    }

    @Override
    public boolean isOwnerOrManagerOfTeam(final Long teamId, final Long memberId) {
        QTeamMember qTeamMember = QTeamMember.teamMember;

        return jpaQueryFactory
                        .selectOne()
                        .from(qTeamMember)
                        .where(
                                qTeamMember
                                        .team
                                        .id
                                        .eq(teamId)
                                        .and(qTeamMember.member.id.eq(memberId))
                                        .and(
                                                qTeamMember.teamMemberType.in(
                                                        TeamMemberType.TEAM_OWNER,
                                                        TeamMemberType.TEAM_MANAGER)))
                        .fetchFirst()
                != null;
    }

    @Override
    public boolean existsTeamByMemberId(final Long memberId) {
        QTeamMember qTeamMember = QTeamMember.teamMember;

        return jpaQueryFactory
                        .selectOne()
                        .from(qTeamMember)
                        .where(qTeamMember.member.id.eq(memberId))
                        .fetchFirst()
                != null;
    }

    @Override
    public boolean existsTeamOwnerByMemberId(final Long memberId) {
        QTeamMember qTeamMember = QTeamMember.teamMember;

        return jpaQueryFactory
                        .selectOne()
                        .from(qTeamMember)
                        .where(
                                qTeamMember
                                        .member
                                        .id
                                        .eq(memberId)
                                        .and(
                                                qTeamMember.teamMemberType.eq(
                                                        TeamMemberType.TEAM_OWNER)))
                        .fetchFirst()
                != null;
    }

    @Override
    public boolean existsTeamOwnerAndOtherManagerByMemberId(final Long memberId) {
        QTeamMember qTeamMember = QTeamMember.teamMember;

        // 해당 memberId가 OWNER인지 확인
        Boolean isTeamOwner =
                jpaQueryFactory
                                .selectOne()
                                .from(qTeamMember)
                                .where(
                                        qTeamMember
                                                .member
                                                .id
                                                .eq(memberId)
                                                .and(
                                                        qTeamMember.teamMemberType.eq(
                                                                TeamMemberType.TEAM_OWNER)))
                                .fetchFirst()
                        != null;

        if (!isTeamOwner) {
            // OWNER가 아니면 바로 false 반환
            return false;
        }

        // 소유한 팀을 조회 (팀이 없을 경우 null 체크)
        Team ownerTeam =
                jpaQueryFactory
                        .select(qTeamMember.team)
                        .from(qTeamMember)
                        .where(
                                qTeamMember
                                        .member
                                        .id
                                        .eq(memberId)
                                        .and(
                                                qTeamMember.teamMemberType.eq(
                                                        TeamMemberType.TEAM_OWNER)))
                        .fetchFirst();

        if (ownerTeam == null) {
            return false; // 팀이 없으면 false 반환
        }

        // 같은 팀에 다른 매니저가 존재하는지 확인
        Boolean hasOtherManager =
                jpaQueryFactory
                                .selectOne()
                                .from(qTeamMember)
                                .where(
                                        qTeamMember
                                                .team
                                                .eq(ownerTeam)
                                                .and(
                                                        qTeamMember.teamMemberType.eq(
                                                                TeamMemberType.TEAM_MANAGER))
                                                .and(
                                                        qTeamMember.member.id.ne(
                                                                memberId))) // OWNER 본인은 제외
                                .fetchFirst()
                        != null;

        return hasOtherManager;
    }

    @Override
    public List<Team> getAllTeamsByMemberId(final Long memberId) {
        QTeamMember qTeamMember = QTeamMember.teamMember;
        QTeam qTeam = QTeam.team;

        return jpaQueryFactory
                .select(qTeam)
                .distinct()
                .from(qTeamMember)
                .join(qTeamMember.team, qTeam)
                .where(
                        qTeamMember
                                .member
                                .id
                                .eq(memberId)
                                .and(qTeamMember.status.eq(StatusType.USABLE)))
                .fetch();
    }

    @Override
    public List<Team> getAllPublicTeamsByMemberId(final Long memberId) {
        QTeamMember qTeamMember = QTeamMember.teamMember;
        QTeam qTeam = QTeam.team;

        return jpaQueryFactory
                .select(qTeam)
                .distinct()
                .from(qTeamMember)
                .join(qTeamMember.team, qTeam)
                .where(
                        qTeamMember
                                .member
                                .id
                                .eq(memberId)
                                .and(qTeamMember.status.eq(StatusType.USABLE))
                                .and(qTeam.isTeamPublic.eq(true)))
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
                .where(
                        qTeamMember
                                .member
                                .id
                                .eq(memberId)
                                .and(qTeamMember.teamMemberType.eq(TeamMemberType.TEAM_OWNER)))
                .fetch();
    }

    @Override
    public TeamMember getTeamMemberByTeamCodeAndEmailId(
            final String teamCode, final String emailId) {
        QTeamMember qTeamMember = QTeamMember.teamMember;
        QMember qMember = QMember.member;

        return jpaQueryFactory
                .selectFrom(qTeamMember)
                .join(qTeamMember.member, qMember)
                .where(qTeamMember.team.teamCode.eq(teamCode), qMember.emailId.eq(emailId))
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
                .where(
                        qTeamMember
                                .team
                                .teamCode
                                .eq(teamCode)
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
                                qTeamMember
                                        .team
                                        .teamCode
                                        .eq(teamCode)
                                        .and(
                                                qTeamMember.teamMemberType.eq(
                                                        TeamMemberType.TEAM_MANAGER)))
                        .fetchFirst()
                != null;
    }

    @Override
    public void removeTeamMemberInTeam(final TeamMember teamMember) {
        QTeamMember qTeamMember = QTeamMember.teamMember;

        jpaQueryFactory.delete(qTeamMember).where(qTeamMember.id.eq(teamMember.getId())).execute();
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
    public void updateTeamMemberManagingTeamState(
            final TeamMember teamMember,
            final TeamMemberManagingTeamState teamMemberManagingTeamState) {
        QTeamMember qTeamMember = QTeamMember.teamMember;

        // QueryDSL을 사용하여 데이터베이스에서 ProfileLog 엔티티를 업데이트
        long updatedCount =
                jpaQueryFactory
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
        // Query to check if a member with the given emailId is part of the team with the given
        // teamCode
        return jpaQueryFactory
                        .selectOne()
                        .from(qTeamMember)
                        .where(
                                qTeamMember
                                        .team
                                        .teamCode
                                        .eq(teamCode) // Team code matches
                                        .and(
                                                qTeamMember.member.emailId.eq(
                                                        emailId)) // Email ID matches
                                        .and(qTeamMember.status.ne(StatusType.DELETED))
                                // Member is not in a deleted state
                                )
                        .fetchFirst()
                != null; // Return true if a result is found, false otherwise
    }

    @Override
    public List<TeamMember> getAllTeamManagers(final Team team) {
        QTeamMember qTeamMember = QTeamMember.teamMember;

        return jpaQueryFactory
                .selectFrom(qTeamMember)
                .where(
                        qTeamMember
                                .team
                                .eq(team)
                                .and(
                                        qTeamMember.teamMemberType.in(
                                                TeamMemberType.TEAM_MANAGER,
                                                TeamMemberType.TEAM_OWNER)))
                .fetch();
    }

    @Override
    public Set<Team> getAllDeletableTeamsByMemberId(final Long memberId) {
        QTeam qTeam = QTeam.team;
        QTeamMember qTeamMember = QTeamMember.teamMember;

        // 팀에서 관리자가 없어야 하므로 서브쿼리로 관리자 확인
        BooleanExpression noOtherManagers =
                jpaQueryFactory
                        .selectOne()
                        .from(qTeamMember)
                        .where(
                                qTeamMember
                                        .team
                                        .eq(qTeam)
                                        .and(
                                                qTeamMember.teamMemberType.eq(
                                                        TeamMemberType.TEAM_MANAGER)))
                        .notExists();

        return new HashSet<>(
                jpaQueryFactory
                        .select(qTeam)
                        .from(qTeamMember)
                        .join(qTeamMember.team, qTeam)
                        .where(
                                qTeamMember
                                        .member
                                        .id
                                        .eq(memberId)
                                        .and(
                                                qTeamMember.teamMemberType.eq(
                                                        TeamMemberType.TEAM_OWNER))
                                        .and(qTeam.status.eq(StatusType.USABLE))
                                        .and(noOtherManagers) // 추가 조건: 다른 팀 관리자가 없어야 함
                                )
                        .fetch());
    }

    // 내가 속한 다른 모든 팀에서 나간다.
    @Override
    public void deleteAllTeamMemberByMember(final Long memberId) {
        QTeamMember qTeamMember = QTeamMember.teamMember;

        long deletedCount =
                jpaQueryFactory
                        .delete(qTeamMember)
                        .where(
                                qTeamMember
                                        .member
                                        .id
                                        .eq(memberId)
                                        .and(
                                                qTeamMember
                                                        .teamMemberType
                                                        .eq(TeamMemberType.TEAM_MANAGER)
                                                        .or(
                                                                qTeamMember.teamMemberType.eq(
                                                                        TeamMemberType
                                                                                .TEAM_VIEWER))))
                        .execute();
    }

    @Override
    public void deleteAllTeamMemberByTeam(final Long teamId) {
        QTeamMember qTeamMember = QTeamMember.teamMember;

        long deletedCount =
                jpaQueryFactory.delete(qTeamMember).where(qTeamMember.team.id.eq(teamId)).execute();

        entityManager.flush();
        entityManager.clear();
    }

    @Override
    public boolean isTeamMembersAllowDelete(final Team team) {
        QTeamMember qTeamMember = QTeamMember.teamMember;

        // Query to count all members in the team that are NOT in the required states
        Long count =
                jpaQueryFactory
                        .select(qTeamMember.id.count())
                        .from(qTeamMember)
                        .where(
                                qTeamMember
                                        .team
                                        .eq(team) // Matches the given team
                                        .and(
                                                qTeamMember.status.ne(
                                                        StatusType.DELETED)) // Exclude deleted
                                        // members
                                        .and(
                                                qTeamMember.teamMemberManagingTeamState.ne(
                                                        TeamMemberManagingTeamState
                                                                .REQUEST_DELETE)) // Not
                                        // REQUEST_DELETE
                                        .and(
                                                qTeamMember.teamMemberManagingTeamState.ne(
                                                        TeamMemberManagingTeamState
                                                                .ALLOW_DELETE)) // Not ALLOW_DELETE
                                )
                        .fetchOne();

        // If count is 0, it means all team members are either REQUEST_DELETE or ALLOW_DELETE
        return count == 0;
    }

    @Override
    public boolean isTeamDeleteRequester(final Long memberId, final Long teamId) {
        QTeamMember qTeamMember = QTeamMember.teamMember;

        return jpaQueryFactory
                        .selectOne()
                        .from(qTeamMember)
                        .where(
                                qTeamMember
                                        .member
                                        .id
                                        .eq(memberId) // Team code matches
                                        .and(qTeamMember.team.id.eq(teamId)) // Email ID matches
                                        .and(
                                                qTeamMember.teamMemberManagingTeamState.eq(
                                                        TeamMemberManagingTeamState.REQUEST_DELETE))
                                        .and(qTeamMember.status.eq(StatusType.USABLE)))
                        .fetchFirst()
                != null;
    }

    @Override
    public Map<Long, List<ProfileTeamInform>> findTeamInformsGroupedByProfile(
            List<Long> profileIds) {
        if (profileIds == null || profileIds.isEmpty()) {
            return Map.of();
        }

        QProfile qProfile = QProfile.profile;
        QTeamMember qTeamMember = QTeamMember.teamMember;
        QTeam qTeam = QTeam.team;
        QMember qMember = QMember.member;

        List<Tuple> results =
                jpaQueryFactory
                        .select(
                                qProfile.id,
                                qTeam.teamName,
                                qTeam.teamCode,
                                qTeam.teamLogoImagePath)
                        .from(qTeamMember)
                        .join(qTeamMember.member, qMember)
                        .join(qTeamMember.team, qTeam)
                        .join(qProfile)
                        .on(qProfile.member.eq(qTeamMember.member))
                        .where(qProfile.id.in(profileIds))
                        .fetch();

        Map<Long, List<ProfileTeamInform>> resultMap = new HashMap<>();
        for (Tuple t : results) {
            Long profileId = t.get(qProfile.id);
            if (profileId == null) {
                continue;
            }
            ProfileTeamInform inform =
                    new ProfileTeamInform(
                            t.get(qTeam.teamName),
                            t.get(qTeam.teamCode),
                            t.get(qTeam.teamLogoImagePath));
            resultMap.computeIfAbsent(profileId, k -> new ArrayList<>()).add(inform);
        }
        return resultMap;
    }
}
