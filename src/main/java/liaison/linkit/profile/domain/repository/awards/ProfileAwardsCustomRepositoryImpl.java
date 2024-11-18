package liaison.linkit.profile.domain.repository.awards;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import liaison.linkit.profile.domain.ProfileAwards;
import liaison.linkit.profile.domain.QProfileAwards;
import liaison.linkit.profile.presentation.awards.dto.ProfileAwardsRequestDTO.UpdateProfileAwardsRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProfileAwardsCustomRepositoryImpl implements ProfileAwardsCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;

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

}
