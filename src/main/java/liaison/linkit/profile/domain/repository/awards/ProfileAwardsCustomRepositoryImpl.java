package liaison.linkit.profile.domain.repository.awards;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import liaison.linkit.profile.domain.awards.ProfileAwards;
import liaison.linkit.profile.domain.awards.QProfileAwards;
import liaison.linkit.profile.presentation.awards.dto.ProfileAwardsRequestDTO.UpdateProfileAwardsRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProfileAwardsCustomRepositoryImpl implements ProfileAwardsCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @PersistenceContext
    private EntityManager entityManager; // EntityManager 주입

    @Override
    public List<ProfileAwards> getProfileAwardsGroup(final Long memberId) {
        QProfileAwards qProfileAwards = QProfileAwards.profileAwards;

        return jpaQueryFactory
                .selectFrom(qProfileAwards)
                .where(qProfileAwards.profile.member.id.eq(memberId))
                .fetch();
    }

    @Override
    public ProfileAwards updateProfileAwards(final Long profileAwardsId, final UpdateProfileAwardsRequest updateProfileAwardsRequest) {
        QProfileAwards qProfileAwards = QProfileAwards.profileAwards;

        // 프로필 활동 업데이트
        long updatedCount = jpaQueryFactory
                .update(qProfileAwards)
                .set(qProfileAwards.awardsName, updateProfileAwardsRequest.getAwardsName())
                .set(qProfileAwards.awardsRanking, updateProfileAwardsRequest.getAwardsRanking())
                .set(qProfileAwards.awardsDate, updateProfileAwardsRequest.getAwardsDate())
                .set(qProfileAwards.awardsOrganizer, updateProfileAwardsRequest.getAwardsOrganizer())
                .set(qProfileAwards.awardsDescription, updateProfileAwardsRequest.getAwardsDescription())
                .where(qProfileAwards.id.eq(profileAwardsId))
                .execute();

        entityManager.flush();
        entityManager.clear();

        if (updatedCount > 0) {
            // 업데이트된 ProfileActivity 조회 및 반환
            return jpaQueryFactory
                    .selectFrom(qProfileAwards)
                    .where(qProfileAwards.id.eq(profileAwardsId))
                    .fetchOne();
        } else {
            return null;
        }
    }

    @Override
    public boolean existsByProfileId(final Long profileId) {
        QProfileAwards qProfileAwards = QProfileAwards.profileAwards;

        return jpaQueryFactory
                .selectOne()
                .from(qProfileAwards)
                .where(qProfileAwards.profile.id.eq(profileId))
                .fetchFirst() != null;
    }

    @Override
    public void removeProfileAwardsByProfileId(final Long profileId) {
        QProfileAwards qProfileAwards = QProfileAwards.profileAwards;

        jpaQueryFactory
                .delete(qProfileAwards)
                .where(qProfileAwards.profile.id.eq(profileId))
                .execute();

        entityManager.flush();
        entityManager.clear();
    }
}
