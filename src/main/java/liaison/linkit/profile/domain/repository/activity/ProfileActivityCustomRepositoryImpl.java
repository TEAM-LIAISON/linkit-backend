package liaison.linkit.profile.domain.repository.activity;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import liaison.linkit.profile.domain.activity.ProfileActivity;
import liaison.linkit.profile.domain.activity.QProfileActivity;
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
}
