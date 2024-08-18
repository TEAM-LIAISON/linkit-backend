package liaison.linkit.matching.domain.repository.teamMatching;

import com.querydsl.jpa.impl.JPAQueryFactory;
import liaison.linkit.global.type.StatusType;
import liaison.linkit.matching.domain.QTeamMatching;
import liaison.linkit.matching.domain.TeamMatching;
import liaison.linkit.matching.domain.type.MatchingStatusType;
import liaison.linkit.matching.domain.type.ReceiverDeleteStatusType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class TeamMatchingRepositoryCustomImpl implements TeamMatchingRepositoryCustom{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<TeamMatching> findAllByTeamMemberAnnouncementIds(final List<Long> teamMemberAnnouncementIds) {
        QTeamMatching teamMatching = QTeamMatching.teamMatching;

        return jpaQueryFactory
                .selectFrom(teamMatching)
                .where(teamMatching.teamMemberAnnouncement.id.in(teamMemberAnnouncementIds)
                        .and(teamMatching.matchingStatusType.eq(MatchingStatusType.REQUESTED))
                        .and(teamMatching.receiverDeleteStatusType.eq(ReceiverDeleteStatusType.REMAINED)))
                .fetch();
    }

    @Override
    public List<TeamMatching> findByMemberIdAndMatchingStatus(final Long memberId) {
        QTeamMatching teamMatching = QTeamMatching.teamMatching;

        return jpaQueryFactory
                .selectFrom(teamMatching)
                .where(teamMatching.member.id.eq(memberId)
                        .and(teamMatching.matchingStatusType.eq(MatchingStatusType.REQUESTED)))
                .fetch();
    }

    @Override
    public List<TeamMatching> findSuccessReceivedMatching(final List<Long> teamMemberAnnouncementIds) {
        QTeamMatching teamMatching = QTeamMatching.teamMatching;

        return jpaQueryFactory
                .selectFrom(teamMatching)
                .where(teamMatching.matchingStatusType.eq(MatchingStatusType.SUCCESSFUL)
                        .and(teamMatching.teamMemberAnnouncement.id.in(teamMemberAnnouncementIds)))
                .fetch();
    }

    @Override
    public List<TeamMatching> findSuccessRequestMatching(final Long memberId) {
        QTeamMatching teamMatching = QTeamMatching.teamMatching;

        return jpaQueryFactory
                .selectFrom(teamMatching)
                .where(teamMatching.matchingStatusType.eq(MatchingStatusType.SUCCESSFUL)
                        .and(teamMatching.member.id.eq(memberId)))
                .fetch();
    }

    @Override
    @Transactional
    public void deleteByMemberId(final Long memberId) {
        QTeamMatching teamMatching = QTeamMatching.teamMatching;

        jpaQueryFactory
                .update(teamMatching)
                .set(teamMatching.status, StatusType.DELETED)
                .where(teamMatching.member.id.eq(memberId))
                .execute();
    }

    @Override
    public boolean existsByMemberId(final Long memberId){
        QTeamMatching teamMatching = QTeamMatching.teamMatching;

        return jpaQueryFactory
                .selectOne()
                .from(teamMatching)
                .where(teamMatching.member.id.eq(memberId))
                .fetchFirst() != null;
    }

    @Override
    public boolean existsByTeamMemberAnnouncementIds(final List<Long> teamMemberAnnouncementIds) {
        QTeamMatching teamMatching = QTeamMatching.teamMatching;

        return jpaQueryFactory
                .selectOne()
                .from(teamMatching)
                .where(teamMatching.teamMemberAnnouncement.id.in(teamMemberAnnouncementIds))
                .fetchFirst() != null;
    }

    @Override
    @Transactional
    public void deleteByTeamMemberAnnouncementIds(final List<Long> teamMemberAnnouncementIds) {
        QTeamMatching teamMatching = QTeamMatching.teamMatching;

        jpaQueryFactory
                .update(teamMatching)
                .set(teamMatching.status, StatusType.DELETED)
                .where(teamMatching.teamMemberAnnouncement.id.in(teamMemberAnnouncementIds))
                .execute();
    }

    @Override
    public boolean existsNonCheckByMemberId(final Long memberId, final List<Long> teamMemberAnnouncementIds) {
        QTeamMatching teamMatching = QTeamMatching.teamMatching;

        return jpaQueryFactory
                .selectOne()
                .from(teamMatching)
                .where(
                        (teamMatching.member.id.eq(memberId).and(teamMatching.isSenderCheck.eq(false)))
                                .or(teamMatching.teamMemberAnnouncement.id.in(teamMemberAnnouncementIds).and(teamMatching.isReceiverCheck.eq(false)))
                )
                .fetchFirst() != null;
    }
}
