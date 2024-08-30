package liaison.linkit.member.domain.repository.member;

import com.querydsl.jpa.impl.JPAQueryFactory;
import liaison.linkit.member.domain.Member;
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
    public boolean existsByEmail(String email) {
        QMember member = QMember.member;

        return jpaQueryFactory
                .selectOne()
                .from(member)
                .where(member.email.eq(email))
                .fetchFirst() != null;
    }

    @Override
    public void deleteByMemberId(final Long memberId) {
        QMember member = QMember.member;

        jpaQueryFactory.update(member)
                .set(member.status, MemberState.DELETED)
                .where(member.id.eq(memberId))
                .execute();
    }
}
