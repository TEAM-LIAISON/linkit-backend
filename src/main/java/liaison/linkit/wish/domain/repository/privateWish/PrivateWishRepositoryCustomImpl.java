package liaison.linkit.wish.domain.repository.privateWish;

import com.querydsl.jpa.impl.JPAQueryFactory;
import liaison.linkit.global.type.StatusType;
import liaison.linkit.wish.domain.PrivateWish;
import liaison.linkit.wish.domain.QPrivateWish;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class PrivateWishRepositoryCustomImpl implements PrivateWishRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<PrivateWish> findAllByMemberId(final Long memberId) {
        QPrivateWish privateWish = QPrivateWish.privateWish;

        return jpaQueryFactory
                .selectFrom(privateWish)
                .where(privateWish.member.id.eq(memberId))
                .fetch();
    }

    @Override
    @Transactional
    public void deleteByMemberId(final Long memberId) {
        QPrivateWish privateWish = QPrivateWish.privateWish;

        jpaQueryFactory
                .update(privateWish)
                .set(privateWish.status, StatusType.DELETED)
                .where(privateWish.member.id.eq(memberId))
                .execute();
    }

    @Override
    @Transactional
    public void deleteByProfileId(final Long profileId) {
        QPrivateWish privateWish = QPrivateWish.privateWish;

        jpaQueryFactory
                .update(privateWish)
                .set(privateWish.status, StatusType.DELETED)
                .where(privateWish.profile.id.eq(profileId))
                .execute();
    }

    @Override
    @Transactional
    public void deleteByMemberIdAndProfileId(final Long memberId, final Long profileId) {
        QPrivateWish privateWish = QPrivateWish.privateWish;

        jpaQueryFactory
                .delete(privateWish)
                .where(privateWish.member.id.eq(memberId)
                        .and(privateWish.profile.id.eq(profileId)))
                .execute();
    }

    @Override
    public boolean existsByMemberId(final Long memberId) {
        QPrivateWish privateWish = QPrivateWish.privateWish;

        Integer count = jpaQueryFactory
                .selectOne()
                .from(privateWish)
                .where(privateWish.member.id.eq(memberId))
                .fetchFirst();

        return count != null;
    }

    @Override
    public boolean existsByProfileId(final Long profileId) {
        QPrivateWish privateWish = QPrivateWish.privateWish;

        Integer count = jpaQueryFactory
                .selectOne()
                .from(privateWish)
                .where(privateWish.profile.id.eq(profileId))
                .fetchFirst();

        return count != null;
    }

    @Override
    public boolean existsByMemberIdAndProfileId(final Long memberId, final Long profileId) {
        QPrivateWish privateWish = QPrivateWish.privateWish;

        Integer count = jpaQueryFactory
                .selectOne()
                .from(privateWish)
                .where(privateWish.member.id.eq(memberId)
                        .and(privateWish.profile.id.eq(profileId)))
                .fetchFirst();

        return count != null;
    }
}
