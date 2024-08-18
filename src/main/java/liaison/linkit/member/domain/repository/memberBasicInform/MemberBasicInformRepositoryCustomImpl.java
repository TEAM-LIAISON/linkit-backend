package liaison.linkit.member.domain.repository.memberBasicInform;

import com.querydsl.jpa.impl.JPAQueryFactory;
import liaison.linkit.global.type.StatusType;
import liaison.linkit.member.domain.MemberBasicInform;
import liaison.linkit.member.domain.QMemberBasicInform;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
public class MemberBasicInformRepositoryCustomImpl implements MemberBasicInformRepositoryCustom{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public boolean existsByMemberId(final Long memberId) {
        QMemberBasicInform memberBasicInform = QMemberBasicInform.memberBasicInform;

        return jpaQueryFactory
                .selectOne()
                .from(memberBasicInform)
                .where(memberBasicInform.member.id.eq(memberId))
                .fetchFirst() != null;
    }

    @Override
    public Optional<MemberBasicInform> findByMemberId(final Long memberId) {
        QMemberBasicInform memberBasicInform = QMemberBasicInform.memberBasicInform;

        MemberBasicInform result = jpaQueryFactory
                .selectFrom(memberBasicInform)
                .where(memberBasicInform.member.id.eq(memberId))
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    @Transactional
    public void deleteByMemberId(final Long memberId) {
        QMemberBasicInform memberBasicInform = QMemberBasicInform.memberBasicInform;

        jpaQueryFactory.update(memberBasicInform)
                .set(memberBasicInform.status, StatusType.DELETED)
                .where(memberBasicInform.member.id.eq(memberId))
                .execute();
    }
}
