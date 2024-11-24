package liaison.linkit.member.domain.repository.memberBasicInform;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import liaison.linkit.member.domain.MemberBasicInform;
import liaison.linkit.member.domain.QMemberBasicInform;
import liaison.linkit.member.presentation.dto.request.memberBasicInform.MemberBasicInformRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
    public Optional<MemberBasicInform> updateMemberBasicInform(
            final Long memberId,
            final MemberBasicInformRequestDTO.UpdateMemberBasicInformRequest updateMemberBasicInformRequest
    ) {
        QMemberBasicInform qMemberBasicInform = QMemberBasicInform.memberBasicInform;

        long affectedRows = jpaQueryFactory.update(qMemberBasicInform)
                .set(qMemberBasicInform.memberName, updateMemberBasicInformRequest.getMemberName())
                .set(qMemberBasicInform.contact, updateMemberBasicInformRequest.getContact())
                .where(qMemberBasicInform.member.id.eq(memberId))
                .execute();

        if (affectedRows > 0) {
            // 업데이트된 객체를 다시 조회해서 반환
            MemberBasicInform updatedEntity = jpaQueryFactory
                    .selectFrom(qMemberBasicInform)
                    .where(qMemberBasicInform.member.id.eq(memberId))
                    .fetchOne();
            return Optional.ofNullable(updatedEntity);
        }

        return Optional.empty();  // 업데이트가 실패한 경우
    }

    @Override
    public Optional<MemberBasicInform> updateConsentServiceUse(
            final Long memberId,
            final MemberBasicInformRequestDTO.UpdateConsentServiceUseRequest updateConsentServiceUseRequest
    ) {
        QMemberBasicInform qMemberBasicInform = QMemberBasicInform.memberBasicInform;

        long affectedRows = jpaQueryFactory.update(qMemberBasicInform)
                .set(qMemberBasicInform.serviceUseAgree, updateConsentServiceUseRequest.getIsServiceUseAgree())
                .set(qMemberBasicInform.privateInformAgree, updateConsentServiceUseRequest.getIsPrivateInformAgree())
                .set(qMemberBasicInform.ageCheck, updateConsentServiceUseRequest.getIsAgeCheck())
                .set(qMemberBasicInform.marketingAgree, updateConsentServiceUseRequest.getIsMarketingAgree())
                .where(qMemberBasicInform.member.id.eq(memberId))
                .execute();

        // 업데이트가 성공했는지 여부에 따라 결과 반환
        if (affectedRows > 0) {
            // 업데이트된 객체를 다시 조회해서 반환
            MemberBasicInform updatedEntity = jpaQueryFactory
                    .selectFrom(qMemberBasicInform)
                    .where(qMemberBasicInform.member.id.eq(memberId))
                    .fetchOne();
            return Optional.ofNullable(updatedEntity);
        }

        return Optional.empty();  // 업데이트가 실패한 경우
    }

    @Override
    public Optional<MemberBasicInform> updateMemberName(
            final Long memberId,
            final MemberBasicInformRequestDTO.UpdateMemberNameRequest updateMemberNameRequest
    ) {
        QMemberBasicInform qMemberBasicInform = QMemberBasicInform.memberBasicInform;

        long affectedRows = jpaQueryFactory.update(qMemberBasicInform)
                .set(qMemberBasicInform.memberName, updateMemberNameRequest.getMemberName())
                .where(qMemberBasicInform.member.id.eq(memberId))
                .execute();

        if (affectedRows > 0) {
            // 업데이트된 엔티티를 다시 조회하여 반환
            MemberBasicInform updatedEntity = jpaQueryFactory
                    .selectFrom(qMemberBasicInform)
                    .where(qMemberBasicInform.member.id.eq(memberId))
                    .fetchOne();
            return Optional.ofNullable(updatedEntity);
        }

        return Optional.empty();  // 업데이트가 실패한 경우
    }

    @Override
    public Optional<MemberBasicInform> updateMemberContact(
            final Long memberId,
            final MemberBasicInformRequestDTO.UpdateMemberContactRequest updateMemberContactRequest
    ) {
        QMemberBasicInform qMemberBasicInform = QMemberBasicInform.memberBasicInform;

        long affectedRows = jpaQueryFactory.update(qMemberBasicInform)
                .set(qMemberBasicInform.contact, updateMemberContactRequest.getContact())
                .where(qMemberBasicInform.member.id.eq(memberId))
                .execute();

        if (affectedRows > 0) {
            // 업데이트된 엔티티를 다시 조회하여 반환
            MemberBasicInform updatedEntity = jpaQueryFactory
                    .selectFrom(qMemberBasicInform)
                    .where(qMemberBasicInform.member.id.eq(memberId))
                    .fetchOne();
            return Optional.ofNullable(updatedEntity);
        }

        return Optional.empty();  // 업데이트가 실패한 경우
    }
}
