package liaison.linkit.member.domain.repository.member;

import com.querydsl.jpa.impl.JPAQueryFactory;
import liaison.linkit.member.domain.Member;
import liaison.linkit.member.domain.MemberBasicInform;
import liaison.linkit.member.domain.type.MemberState;
import liaison.linkit.member.domain.QMember;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
public class MemberRepositoryCustomImpl implements MemberRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<Member> findBySocialLoginId(String socialLoginId) {
        QMember member = QMember.member;

        Member result = jpaQueryFactory
                .selectFrom(member)
                .where(member.socialLoginId.eq(socialLoginId))
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public Optional<Member> updateEmailId(final Long memberId, final String emailId) {
        QMember qMember = QMember.member;

        long affectedRows = jpaQueryFactory.update(qMember)
                .set(qMember.emailId, emailId)
                .where(qMember.id.eq(memberId))
                .execute();

        if (affectedRows > 0) {
            // 업데이트된 객체를 다시 조회해서 반환
            Member updatedEntity = jpaQueryFactory
                    .selectFrom(qMember)
                    .where(qMember.id.eq(memberId))
                    .fetchOne();
            return Optional.ofNullable(updatedEntity);
        }

        return Optional.empty();  // 업데이트가 실패한 경우
    }

    @Override
    public boolean existsByEmail(String email) {
        QMember member = QMember.member;

        return jpaQueryFactory
                .selectOne()
                .from(member)
                .where(member.email.eq(email))
                .fetchFirst() != null;
    }

    @Override
    public String findEmailById(final Long memberId) {
        QMember member = QMember.member;

        return jpaQueryFactory
                .select(member.email)
                .from(member)
                .where(member.id.eq(memberId))
                .fetchOne(); // 하나의 결과를 반환
    }

    @Override
    public void deleteByMemberId(final Long memberId) {
        QMember member = QMember.member;

        jpaQueryFactory.update(member)
                .set(member.memberState, MemberState.DELETED)
                .where(member.id.eq(memberId))
                .execute();
    }

    @Override
    public String findEmailIdById(final Long memberId) {
        QMember member = QMember.member;

        return jpaQueryFactory
                .select(member.emailId)
                .from(member)
                .where(member.id.eq(memberId))
                .fetchOne();
    }
}
