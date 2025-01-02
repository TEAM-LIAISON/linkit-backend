package liaison.linkit.matching.domain.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import liaison.linkit.matching.domain.Matching;
import liaison.linkit.matching.domain.QMatching;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

@RequiredArgsConstructor
@Slf4j
public class MatchingCustomRepositoryImpl implements MatchingCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<Matching> findReceivedToProfile(
            final String emailId,
            final Pageable pageable
    ) {
        QMatching qMatching = QMatching.matching;

        try {
            // 1. Matching 데이터 조회
            List<Matching> content = jpaQueryFactory
                    .selectFrom(qMatching)
                    .where(qMatching.receiverEmailId.eq(emailId))   // receiverProfile의 emailId 조건
                    .offset(pageable.getOffset())                   // 페이지의 시작 위치
                    .limit(pageable.getPageSize())                  // 페이지 크기
                    .orderBy(qMatching.createdAt.desc())            // 최신순 정렬
                    .fetch();

            // 2. Matching 데이터 개수 조회
            Long totalLong = jpaQueryFactory
                    .select(qMatching.count())
                    .from(qMatching)
                    .where(qMatching.receiverEmailId.eq(emailId)) // 동일 조건
                    .fetchOne();

            long total = (totalLong == null) ? 0L : totalLong;

            // 3. Page 생성
            return PageableExecutionUtils.getPage(content, pageable, () -> total);
        } catch (Exception e) {
            // 예외 로깅 및 다시 던지기
            log.error("Error while fetching received matchings for emailId: {}", emailId, e);
            throw e;
        }
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
