package liaison.linkit.matching.domain.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import liaison.linkit.matching.domain.QMatching;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class MatchingCustomRepositoryImpl implements MatchingCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;

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
