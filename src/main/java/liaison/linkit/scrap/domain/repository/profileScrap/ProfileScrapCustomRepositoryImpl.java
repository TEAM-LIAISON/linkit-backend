package liaison.linkit.scrap.domain.repository.profileScrap;

import java.util.List;

import com.querydsl.jpa.impl.JPAQueryFactory;
import liaison.linkit.member.domain.QMember;
import liaison.linkit.scrap.domain.ProfileScrap;
import liaison.linkit.scrap.domain.QProfileScrap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class ProfileScrapCustomRepositoryImpl implements ProfileScrapCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<ProfileScrap> getAllProfileScrapByMemberId(final Long memberId) {
        QProfileScrap qProfileScrap = QProfileScrap.profileScrap;

        return jpaQueryFactory
                .selectFrom(qProfileScrap)
                .join(qProfileScrap.member, QMember.member)
                .where(qProfileScrap.member.id.eq(memberId))
                .fetch();
    }

    // 스크랩 주체 회원이 스크랩한 모든 프로필 스크랩 객체를 조회한다.
    @Override
    public void deleteByMemberId(final Long memberId) {
        QProfileScrap qProfileScrap = QProfileScrap.profileScrap;

        jpaQueryFactory.delete(qProfileScrap).where(qProfileScrap.member.id.eq(memberId)).execute();
    }

    @Override
    public void deleteByMemberIdAndEmailId(final Long memberId, final String emailId) {

        QProfileScrap qProfileScrap = QProfileScrap.profileScrap;

        jpaQueryFactory
                .delete(qProfileScrap)
                .where(
                        qProfileScrap
                                .member
                                .id
                                .eq(memberId)
                                .and(qProfileScrap.profile.member.emailId.eq(emailId)))
                .execute();
    }

    @Override
    public void deleteAllByMemberId(final Long memberId) {
        QProfileScrap qProfileScrap = QProfileScrap.profileScrap;

        long deletedCount =
                jpaQueryFactory
                        .delete(qProfileScrap)
                        .where(qProfileScrap.member.id.eq(memberId))
                        .execute();
    }

    @Override
    public void deleteAllByProfileId(final Long profileId) {
        QProfileScrap qProfileScrap = QProfileScrap.profileScrap;

        long deletedCount =
                jpaQueryFactory
                        .delete(qProfileScrap)
                        .where(qProfileScrap.profile.id.eq(profileId))
                        .execute();
    }

    @Override
    public boolean existsByMemberId(final Long memberId) {
        QProfileScrap qProfileScrap = QProfileScrap.profileScrap;

        Integer count =
                jpaQueryFactory
                        .selectOne()
                        .from(qProfileScrap)
                        .where(qProfileScrap.member.id.eq(memberId))
                        .fetchFirst();

        return count != null;
    }

    @Override
    public boolean existsByProfileId(final Long profileId) {
        QProfileScrap qProfileScrap = QProfileScrap.profileScrap;

        Integer count =
                jpaQueryFactory
                        .selectOne()
                        .from(qProfileScrap)
                        .where(qProfileScrap.profile.id.eq(profileId))
                        .fetchFirst();

        return count != null;
    }

    @Override
    public boolean existsByMemberIdAndEmailId(final Long memberId, final String emailId) {

        QProfileScrap qProfileScrap = QProfileScrap.profileScrap;

        Integer count =
                jpaQueryFactory
                        .selectOne()
                        .from(qProfileScrap)
                        .where(
                                qProfileScrap
                                        .member
                                        .id
                                        .eq(memberId)
                                        .and(qProfileScrap.profile.member.emailId.eq(emailId)))
                        .fetchFirst();

        return count != null;
    }

    @Override
    public int countTotalProfileScrapByEmailId(final String emailId) {
        QProfileScrap qProfileScrap = QProfileScrap.profileScrap;

        Long count =
                jpaQueryFactory
                        .select(qProfileScrap.count())
                        .from(qProfileScrap)
                        .where(qProfileScrap.profile.member.emailId.eq(emailId))
                        .fetchOne();

        return count != null ? count.intValue() : 0;
    }
}
