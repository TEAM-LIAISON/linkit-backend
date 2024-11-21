package liaison.linkit.profile.domain.repository.activity;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import liaison.linkit.profile.domain.activity.ProfileActivity;
import liaison.linkit.profile.domain.activity.QProfileActivity;
import liaison.linkit.profile.presentation.activity.dto.ProfileActivityRequestDTO.UpdateProfileActivityRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProfileActivityCustomRepositoryImpl implements ProfileActivityCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<ProfileActivity> getProfileActivities(final Long memberId) {
        QProfileActivity qProfileActivity = QProfileActivity.profileActivity;

        return queryFactory
                .selectFrom(qProfileActivity)
                .where(qProfileActivity.profile.member.id.eq(memberId))
                .fetch();
    }

    @Override
    public ProfileActivity updateProfileActivity(final Long profileActivityId, final UpdateProfileActivityRequest updateProfileActivity) {
        QProfileActivity qProfileActivity = QProfileActivity.profileActivity;

        // 프로필 활동 업데이트
        long updatedCount = queryFactory
                .update(qProfileActivity)
                .set(qProfileActivity.activityName, updateProfileActivity.getActivityName())
                .set(qProfileActivity.activityRole, updateProfileActivity.getActivityRole())
                .set(qProfileActivity.activityStartDate, updateProfileActivity.getActivityStartDate())
                .set(qProfileActivity.activityEndDate, updateProfileActivity.getActivityEndDate())
                .set(qProfileActivity.isActivityInProgress, updateProfileActivity.getIsActivityInProgress())
                .set(qProfileActivity.activityDescription, updateProfileActivity.getActivityDescription())
                .where(qProfileActivity.id.eq(profileActivityId))
                .execute();

        if (updatedCount > 0) {
            // 업데이트된 ProfileActivity 조회 및 반환
            return queryFactory
                    .selectFrom(qProfileActivity)
                    .where(qProfileActivity.id.eq(profileActivityId))
                    .fetchOne();
        } else {
            // 업데이트된 행이 없다면 null 또는 예외 처리
            return null;
        }
    }

    @Override
    public void deleteAllByProfileId(Long profileId) {
        final QProfileActivity qProfileActivity = QProfileActivity.profileActivity;

        queryFactory
                .delete(qProfileActivity)
                .where(qProfileActivity.profile.id.eq(profileId))
                .execute();
    }
}
