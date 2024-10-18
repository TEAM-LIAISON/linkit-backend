package liaison.linkit.member.domain.repository.memberBasicInform;

import com.querydsl.jpa.impl.JPAQueryFactory;
import liaison.linkit.global.type.StatusType;
import liaison.linkit.member.domain.MemberBasicInform;
import liaison.linkit.member.domain.QMemberBasicInform;
import liaison.linkit.member.presentation.dto.request.memberBasicInform.MemberBasicInformRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
public class MemberBasicInformRepositoryCustomImpl implements MemberBasicInformRepositoryCustom {

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

    @Override
    public Optional<MemberBasicInform> update(
            final Long memberId,
            final MemberBasicInformRequestDTO.memberBasicInformRequest request
    ) {
        QMemberBasicInform memberBasicInform = QMemberBasicInform.memberBasicInform;

        long affectedRows = jpaQueryFactory
                .update(memberBasicInform)
                .set(memberBasicInform.memberName, request.getMemberName())
                .set(memberBasicInform.contact, request.getContact())
                .set(memberBasicInform.marketingAgree, request.isMarketingAgree())
                .where(memberBasicInform.member.id.eq(memberId))  // 조건: 특정 ID로 업데이트
                .execute();  // 실행

        // 업데이트가 성공했는지 여부에 따라 결과 반환
        if (affectedRows > 0) {
            // 업데이트된 객체를 다시 조회해서 반환
            MemberBasicInform updatedEntity = jpaQueryFactory
                    .selectFrom(memberBasicInform)
                    .where(memberBasicInform.member.id.eq(memberId))
                    .fetchOne();
            return Optional.ofNullable(updatedEntity);
        }

        return Optional.empty();  // 업데이트가 실패한 경우
    }
}
