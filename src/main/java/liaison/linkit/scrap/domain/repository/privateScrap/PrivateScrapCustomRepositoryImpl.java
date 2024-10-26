package liaison.linkit.scrap.domain.repository.privateScrap;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import liaison.linkit.scrap.domain.PrivateScrap;
import liaison.linkit.scrap.domain.QPrivateScrap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Slf4j
public class PrivateScrapCustomRepositoryImpl implements PrivateScrapCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<PrivateScrap> findAllByMemberId(final Long memberId) {
        QPrivateScrap qPrivateScrap = QPrivateScrap.privateScrap;

        return jpaQueryFactory
                .selectFrom(qPrivateScrap)
                .where(qPrivateScrap.member.id.eq(memberId))
                .fetch();
    }

    @Override
    @Transactional
    public void deleteByMemberId(final Long memberId) {
        QPrivateScrap qPrivateScrap = QPrivateScrap.privateScrap;

        jpaQueryFactory
                .delete(qPrivateScrap)
                .where(qPrivateScrap.member.id.eq(memberId))
                .execute();
    }

    @Override
    @Transactional
    public void deleteByProfileId(final Long profileId) {
        QPrivateScrap qPrivateScrap = QPrivateScrap.privateScrap;

        jpaQueryFactory
                .delete(qPrivateScrap)
                .where(qPrivateScrap.profile.id.eq(profileId))
                .execute();
    }

    @Override
    @Transactional
    public void deleteByMemberIdAndProfileId(final Long memberId, final Long profileId) {

        QPrivateScrap qPrivateScrap = QPrivateScrap.privateScrap;

        jpaQueryFactory
                .delete(qPrivateScrap)
                .where(qPrivateScrap.member.id.eq(memberId)
                        .and(qPrivateScrap.profile.id.eq(profileId)))
                .execute();

    }

    @Override
    public boolean existsByMemberId(final Long memberId) {
        QPrivateScrap qPrivateScrap = QPrivateScrap.privateScrap;

        Integer count = jpaQueryFactory
                .selectOne()
                .from(qPrivateScrap)
                .where(qPrivateScrap.member.id.eq(memberId))
                .fetchFirst();

        return count != null;
    }

    @Override
    public boolean existsByProfileId(final Long profileId) {
        QPrivateScrap qPrivateScrap = QPrivateScrap.privateScrap;

        Integer count = jpaQueryFactory
                .selectOne()
                .from(qPrivateScrap)
                .where(qPrivateScrap.profile.id.eq(profileId))
                .fetchFirst();

        return count != null;
    }

    @Override
    public boolean existsByMemberIdAndProfileId(final Long memberId, final Long profileId) {

        QPrivateScrap qPrivateScrap = QPrivateScrap.privateScrap;

        Integer count = jpaQueryFactory
                .selectOne()
                .from(qPrivateScrap)
                .where(qPrivateScrap.member.id.eq(memberId)
                        .and(qPrivateScrap.profile.id.eq(profileId)))
                .fetchFirst();

        return count != null;

    }
}
