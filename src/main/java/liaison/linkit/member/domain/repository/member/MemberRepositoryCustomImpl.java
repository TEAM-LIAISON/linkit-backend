package liaison.linkit.member.domain.repository.member;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import liaison.linkit.member.domain.Member;
import liaison.linkit.member.domain.QMember;
import liaison.linkit.member.domain.type.MemberState;
import liaison.linkit.member.presentation.dto.MemberDynamicResponse;
import liaison.linkit.profile.domain.profile.QProfile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class MemberRepositoryCustomImpl implements MemberRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @PersistenceContext private EntityManager entityManager; // EntityManager 주입

    @Override
    public Optional<Member> findBySocialLoginId(String socialLoginId) {
        QMember member = QMember.member;

        Member result =
                jpaQueryFactory
                        .selectFrom(member)
                        .where(member.socialLoginId.eq(socialLoginId))
                        .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public Optional<Member> findByEmailId(final String emailId) {
        QMember member = QMember.member;

        Member result =
                jpaQueryFactory.selectFrom(member).where(member.emailId.eq(emailId)).fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public Optional<Member> updateEmailId(final Long memberId, final String emailId) {
        QMember qMember = QMember.member;

        long affectedRows =
                jpaQueryFactory
                        .update(qMember)
                        .set(qMember.emailId, emailId)
                        .where(qMember.id.eq(memberId))
                        .execute();

        // 변경 사항을 데이터베이스에서 다시 조회
        if (affectedRows > 0) {
            entityManager.flush(); // 동기화
            entityManager.clear(); // 캐시 무효화

            Member updatedEntity =
                    jpaQueryFactory.selectFrom(qMember).where(qMember.id.eq(memberId)).fetchOne();
            return Optional.ofNullable(updatedEntity);
        }

        return Optional.empty(); // 업데이트가 실패한 경우
    }

    @Override
    public boolean existsByEmail(String email) {
        QMember member = QMember.member;

        return jpaQueryFactory.selectOne().from(member).where(member.email.eq(email)).fetchFirst()
                != null;
    }

    @Override
    public boolean existsByEmailId(final String emailId) {
        QMember member = QMember.member;

        return jpaQueryFactory
                        .selectOne()
                        .from(member)
                        .where(member.emailId.eq(emailId))
                        .fetchFirst()
                != null;
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

        jpaQueryFactory
                .update(member)
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

    @Override
    public Optional<Member> findByEmail(final String email) {
        QMember member = QMember.member;

        Member result = jpaQueryFactory.selectFrom(member).where(member.email.eq(email)).fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public List<MemberDynamicResponse> findAllDynamicVariablesWithMember() {
        QMember qMember = QMember.member;
        QProfile qProfile = QProfile.profile;

        return jpaQueryFactory
                .select(
                        Projections.constructor(
                                MemberDynamicResponse.class,
                                qMember.emailId,
                                qMember.memberBasicInform.memberName,
                                qMember.createdAt))
                .from(qMember)
                .leftJoin(qMember.memberBasicInform)
                .leftJoin(qMember.profile, qProfile)
                .where(
                        qMember.memberState
                                .eq(MemberState.ACTIVE)
                                .and(qProfile.isProfilePublic.eq(true)))
                .orderBy(qMember.id.desc())
                .fetch();
    }
}
