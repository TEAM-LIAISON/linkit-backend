package liaison.linkit.matching.domain.repository.privateMatching;

import com.querydsl.jpa.impl.JPAQueryFactory;
import liaison.linkit.global.type.StatusType;
import liaison.linkit.matching.domain.PrivateMatching;
import liaison.linkit.matching.domain.QPrivateMatching;
import liaison.linkit.matching.domain.type.MatchingStatusType;
import liaison.linkit.matching.domain.type.ReceiverDeleteStatusType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class PrivateMatchingRepositoryCustomImpl implements PrivateMatchingRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<PrivateMatching> findByProfileIdAndMatchingStatus(final Long profileId) {
        QPrivateMatching privateMatching = QPrivateMatching.privateMatching;

        return jpaQueryFactory
                .selectFrom(privateMatching)
                .where(privateMatching.profile.id.eq(profileId)
                        .and(privateMatching.matchingStatusType.eq(MatchingStatusType.REQUESTED)))
                .fetch();
    }

    @Override
    public List<PrivateMatching> findByMemberIdAndMatchingStatus(final Long memberId) {
        QPrivateMatching privateMatching = QPrivateMatching.privateMatching;

        return jpaQueryFactory
                .selectFrom(privateMatching)
                .where(privateMatching.member.id.eq(memberId)
                        .and(privateMatching.matchingStatusType.eq(MatchingStatusType.REQUESTED))
                        .and(privateMatching.receiverDeleteStatusType.eq(ReceiverDeleteStatusType.REMAINED)))
                .fetch();
    }

    @Override
    public List<PrivateMatching> findSuccessReceivedMatching(final Long profileId) {
        QPrivateMatching privateMatching = QPrivateMatching.privateMatching;

        return jpaQueryFactory
                .selectFrom(privateMatching)
                .where(privateMatching.profile.id.eq(profileId)
                        .and(privateMatching.matchingStatusType.eq(MatchingStatusType.SUCCESSFUL)))
                .fetch();
    }

    @Override
    public List<PrivateMatching> findSuccessRequestMatching(final Long memberId) {
        QPrivateMatching privateMatching = QPrivateMatching.privateMatching;

        return jpaQueryFactory
                .selectFrom(privateMatching)
                .where(privateMatching.member.id.eq(memberId)
                        .and(privateMatching.matchingStatusType.eq(MatchingStatusType.SUCCESSFUL)))
                .fetch();
    }

    @Override
    public boolean existsByProfileId(final Long profileId) {
        QPrivateMatching privateMatching = QPrivateMatching.privateMatching;

        return jpaQueryFactory
                .selectOne()
                .from(privateMatching)
                .where(privateMatching.profile.id.eq(profileId))
                .fetchFirst() != null;
    }

    @Override
    @Transactional
    public void deleteByMemberId(final Long memberId) {
        QPrivateMatching privateMatching = QPrivateMatching.privateMatching;

        jpaQueryFactory.update(privateMatching)
                .set(privateMatching.status, StatusType.DELETED)
                .where(privateMatching.member.id.eq(memberId))
                .execute();
    }

    @Override
    public boolean existsByMemberId(final Long memberId) {
        QPrivateMatching privateMatching = QPrivateMatching.privateMatching;

        return jpaQueryFactory
                .selectOne()
                .from(privateMatching)
                .where(privateMatching.member.id.eq(memberId))
                .fetchFirst() != null;
    }

    @Override
    @Transactional
    public void deleteByProfileId(final Long profileId) {
        QPrivateMatching privateMatching = QPrivateMatching.privateMatching;

        jpaQueryFactory.update(privateMatching)
                .set(privateMatching.status, StatusType.DELETED)
                .where(privateMatching.profile.id.eq(profileId))
                .execute();
    }

    @Override
    public boolean existsNonCheckByMemberId(final Long memberId, final Long profileId) {
        QPrivateMatching privateMatching = QPrivateMatching.privateMatching;

        return jpaQueryFactory
                .selectOne()
                .from(privateMatching)
                .where(privateMatching.member.id.eq(memberId).and(privateMatching.isSenderCheck.eq(false))
                        .or(privateMatching.profile.id.eq(profileId).and(privateMatching.isReceiverCheck.eq(false))))
                .fetchFirst() != null;
    }
}
