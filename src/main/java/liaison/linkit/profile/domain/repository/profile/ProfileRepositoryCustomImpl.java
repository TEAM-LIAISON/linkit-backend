package liaison.linkit.profile.domain.repository.profile;

import com.querydsl.jpa.impl.JPAQueryFactory;
import liaison.linkit.global.type.StatusType;
import liaison.linkit.profile.domain.Profile;
import liaison.linkit.profile.domain.QProfile;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class ProfileRepositoryCustomImpl implements ProfileRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<Profile> findByMemberId(Long memberId) {
        QProfile profile = QProfile.profile;

        Profile result = jpaQueryFactory
                .selectFrom(profile)
                .where(profile.member.id.eq(memberId))
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public boolean existsByMemberId(Long memberId) {
        QProfile profile = QProfile.profile;

        Integer count = jpaQueryFactory
                .selectOne()
                .from(profile)
                .where(profile.member.id.eq(memberId))
                .fetchFirst();

        return count != null;
    }

    @Override
    public void deleteByMemberId(Long memberId) {
        QProfile profile = QProfile.profile;

        jpaQueryFactory
                .update(profile)
                .set(profile.status, StatusType.DELETED)
                .where(profile.member.id.eq(memberId))
                .execute();
    }
}
