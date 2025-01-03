package liaison.linkit.matching.domain.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import liaison.linkit.matching.domain.Matching;
import liaison.linkit.matching.domain.QMatching;
import liaison.linkit.team.domain.team.Team;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

@RequiredArgsConstructor
@Slf4j
public class MatchingCustomRepositoryImpl implements MatchingCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Matching> findAllByIds(final List<Long> matchingIds) {
        QMatching qMatching = QMatching.matching;

        return jpaQueryFactory
                .selectFrom(qMatching)
                .where(qMatching.id.in(matchingIds))
                .fetch();
    }

    @Override
    public Page<Matching> findRequestedByProfile(
            final String emailId,
            final Pageable pageable
    ) {
        QMatching qMatching = QMatching.matching;

        try {
            List<Matching> content = jpaQueryFactory
                    .selectFrom(qMatching)
                    .where(qMatching.senderEmailId.eq(emailId))
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .orderBy(qMatching.createdAt.desc())
                    .fetch();

            long total = Optional.ofNullable(
                    jpaQueryFactory
                            .select(qMatching.count())
                            .from(qMatching)
                            .where(qMatching.senderEmailId.eq(emailId))
                            .fetchOne()
            ).orElse(0L);

            return PageableExecutionUtils.getPage(content, pageable, () -> total);
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public Page<Matching> findRequestedByTeam(
            final List<Team> teams,
            final Pageable pageable
    ) {
        QMatching qMatching = QMatching.matching;

        if (teams == null || teams.isEmpty()) {
            return Page.empty(pageable);
        }

        List<String> teamCodes = teams.stream()
                .map(Team::getTeamCode)
                .toList();

        BooleanExpression condition = qMatching.senderTeamCode.in(teamCodes);

        List<Matching> content = jpaQueryFactory
                .selectFrom(qMatching)
                .where(condition)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(qMatching.createdAt.desc())
                .fetch();

        long total = Optional.ofNullable(
                jpaQueryFactory
                        .select(qMatching.count())
                        .from(qMatching)
                        .where(condition)
                        .fetchOne()
        ).orElse(0L);

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<Matching> findReceivedToProfile(
            final String emailId,
            final Pageable pageable
    ) {
        QMatching qMatching = QMatching.matching;

        try {
            List<Matching> content = jpaQueryFactory
                    .selectFrom(qMatching)
                    .where(qMatching.receiverEmailId.eq(emailId))
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .orderBy(qMatching.createdAt.desc())
                    .fetch();

            long total = Optional.ofNullable(
                    jpaQueryFactory
                            .select(qMatching.count())
                            .from(qMatching)
                            .where(qMatching.receiverEmailId.eq(emailId))
                            .fetchOne()
            ).orElse(0L);

            return PageableExecutionUtils.getPage(content, pageable, () -> total);
        } catch (Exception e) {
            log.error("Error fetching matchings for emailId: {}, with pageable: {}", emailId, pageable, e);
            throw e;
        }
    }


    @Override
    public Page<Matching> findReceivedToTeam(
            final List<Team> teams,
            final Pageable pageable
    ) {
        QMatching qMatching = QMatching.matching;

        if (teams == null || teams.isEmpty()) {
            return Page.empty(pageable);
        }

        List<String> teamCodes = teams.stream()
                .map(Team::getTeamCode)
                .toList();

        BooleanExpression condition = qMatching.receiverTeamCode.in(teamCodes);

        List<Matching> content = jpaQueryFactory
                .selectFrom(qMatching)
                .where(condition)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(qMatching.createdAt.desc())
                .fetch();

        long total = Optional.ofNullable(
                jpaQueryFactory
                        .select(qMatching.count())
                        .from(qMatching)
                        .where(condition)
                        .fetchOne()
        ).orElse(0L);

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<Matching> findReceivedToAnnouncement(
            final List<Long> announcementIds,
            final Pageable pageable
    ) {
        QMatching qMatching = QMatching.matching;

        if (announcementIds == null || announcementIds.isEmpty()) {
            return Page.empty(pageable);
        }

        BooleanExpression condition = qMatching.receiverAnnouncementId.in(announcementIds);

        List<Matching> content = jpaQueryFactory
                .selectFrom(qMatching)
                .where(condition)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(qMatching.createdAt.desc())
                .fetch();

        long total = Optional.ofNullable(
                jpaQueryFactory
                        .select(qMatching.count())
                        .from(qMatching)
                        .where(condition)
                        .fetchOne()
        ).orElse(0L);

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public int countByReceiverTeamCodes(final List<String> receiverTeamCodes) {
        // 만약 팀 코드 목록이 없으면 0 반환
        if (receiverTeamCodes == null || receiverTeamCodes.isEmpty()) {
            return 0;
        }

        QMatching qMatching = QMatching.matching;

        // QueryDSL로 count 조회
        Long count = jpaQueryFactory
                .select(qMatching.count())
                .from(qMatching)
                .where(qMatching.receiverTeamCode.in(receiverTeamCodes))
                .fetchOne();

        // null이 반환될 수 있으므로 null 체크 후 int 변환
        return count != null ? count.intValue() : 0;
    }

    @Override
    public int countByReceiverAnnouncementIds(final List<Long> receiverAnnouncementIds) {
        // 만약 팀 코드 목록이 없으면 0 반환
        if (receiverAnnouncementIds == null || receiverAnnouncementIds.isEmpty()) {
            return 0;
        }

        QMatching qMatching = QMatching.matching;

        // QueryDSL로 count 조회
        Long count = jpaQueryFactory
                .select(qMatching.count())
                .from(qMatching)
                .where(qMatching.receiverAnnouncementId.in(receiverAnnouncementIds))
                .fetchOne();

        // null이 반환될 수 있으므로 null 체크 후 int 변환
        return count != null ? count.intValue() : 0;
    }
}
