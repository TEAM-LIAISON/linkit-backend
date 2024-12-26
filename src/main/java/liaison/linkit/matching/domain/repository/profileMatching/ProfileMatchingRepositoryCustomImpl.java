package liaison.linkit.matching.domain.repository.profileMatching;

import com.querydsl.jpa.impl.JPAQueryFactory;
import liaison.linkit.global.type.StatusType;
import liaison.linkit.matching.domain.ProfileMatching;

import liaison.linkit.matching.domain.QProfileMatching;
import liaison.linkit.matching.domain.type.MatchingStatusType;
import liaison.linkit.matching.domain.type.RequestSenderDeleteStatusType;
import liaison.linkit.matching.domain.type.SuccessReceiverDeleteStatusType;
import liaison.linkit.matching.domain.type.SuccessSenderDeleteStatusType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class ProfileMatchingRepositoryCustomImpl implements ProfileMatchingRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<ProfileMatching> findByProfileIdAndMatchingStatus(final Long profileId) {
        QProfileMatching profileMatching = QProfileMatching.profileMatching;

        return jpaQueryFactory
                .selectFrom(profileMatching)
                .where(profileMatching.profile.id.eq(profileId)
                        .and(profileMatching.matchingStatusType.eq(MatchingStatusType.REQUESTED)))
                .fetch();
    }

    @Override
    public List<ProfileMatching> findByMemberIdAndMatchingStatus(final Long memberId) {
        QProfileMatching profileMatching = QProfileMatching.profileMatching;

        return jpaQueryFactory
                .selectFrom(profileMatching)
                .where(profileMatching.member.id.eq(memberId)
                        .and(profileMatching.matchingStatusType.eq(MatchingStatusType.REQUESTED))
                        .and(profileMatching.requestSenderDeleteStatusType.eq(RequestSenderDeleteStatusType.REMAINED)))
                .fetch();
    }

    @Override
    public List<ProfileMatching> findSuccessReceivedMatching(final Long profileId) {
        QProfileMatching profileMatching = QProfileMatching.profileMatching;

        return jpaQueryFactory
                .selectFrom(profileMatching)
                .where(profileMatching.profile.id.eq(profileId)
                        .and(profileMatching.matchingStatusType.eq(MatchingStatusType.SUCCESSFUL))
                        .and(profileMatching.successReceiverDeleteStatusType.eq(SuccessReceiverDeleteStatusType.REMAINED)))
                .fetch();
    }

    @Override
    public List<ProfileMatching> findSuccessRequestMatching(final Long memberId) {
        QProfileMatching profileMatching = QProfileMatching.profileMatching;

        return jpaQueryFactory
                .selectFrom(profileMatching)
                .where(profileMatching.member.id.eq(memberId)
                        .and(profileMatching.matchingStatusType.eq(MatchingStatusType.SUCCESSFUL))
                        .and(profileMatching.successSenderDeleteStatusType.eq(SuccessSenderDeleteStatusType.REMAINED)))
                .fetch();
    }

    @Override
    public boolean existsByProfileId(final Long profileId) {
        QProfileMatching profileMatching = QProfileMatching.profileMatching;

        return jpaQueryFactory
                .selectOne()
                .from(profileMatching)
                .where(profileMatching.profile.id.eq(profileId))
                .fetchFirst() != null;
    }

    @Override
    @Transactional
    public void deleteByMemberId(final Long memberId) {
        QProfileMatching profileMatching = QProfileMatching.profileMatching;

        jpaQueryFactory.update(profileMatching)
                .set(profileMatching.status, StatusType.DELETED)
                .where(profileMatching.member.id.eq(memberId))
                .execute();
    }

    @Override
    public boolean existsByMemberId(final Long memberId) {
        QProfileMatching profileMatching = QProfileMatching.profileMatching;

        return jpaQueryFactory
                .selectOne()
                .from(profileMatching)
                .where(profileMatching.member.id.eq(memberId))
                .fetchFirst() != null;
    }

    @Override
    @Transactional
    public void deleteByProfileId(final Long profileId) {
        QProfileMatching profileMatching = QProfileMatching.profileMatching;

        jpaQueryFactory.update(profileMatching)
                .set(profileMatching.status, StatusType.DELETED)
                .where(profileMatching.profile.id.eq(profileId))
                .execute();
    }

    @Override
    public boolean existsNonCheckByMemberId(final Long memberId, final Long profileId) {
        QProfileMatching profileMatching = QProfileMatching.profileMatching;

        return jpaQueryFactory
                .selectOne()
                .from(profileMatching)
                .where(profileMatching.member.id.eq(memberId).and(profileMatching.isReceiverCheck.eq(false))
                        .or(profileMatching.profile.id.eq(profileId).and(profileMatching.isReceiverCheck.eq(false))))
                .fetchFirst() != null;
    }
}
