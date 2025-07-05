package liaison.linkit.profile.domain.repository.currentState;

import java.util.List;

import com.querydsl.jpa.impl.JPAQueryFactory;
import liaison.linkit.profile.domain.state.ProfileCurrentState;
import liaison.linkit.profile.domain.state.QProfileCurrentState;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class ProfileCurrentStateCustomRepositoryImpl
        implements ProfileCurrentStateCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<ProfileCurrentState> findProfileCurrentStatesByProfileId(final Long profileId) {
        QProfileCurrentState qProfileCurrentState = QProfileCurrentState.profileCurrentState;

        return jpaQueryFactory
                .selectFrom(qProfileCurrentState)
                .where(qProfileCurrentState.profile.id.eq(profileId))
                .fetch();
    }

    @Override
    public boolean existsProfileCurrentStateByProfileId(final Long profileId) {
        QProfileCurrentState qProfileCurrentState = QProfileCurrentState.profileCurrentState;

        Integer count =
                jpaQueryFactory
                        .selectOne()
                        .from(qProfileCurrentState)
                        .where(qProfileCurrentState.profile.id.eq(profileId))
                        .fetchFirst();

        return count != null;
    }

    @Override
    public void deleteAllByProfileId(final Long profileId) {
        QProfileCurrentState qProfileCurrentState = QProfileCurrentState.profileCurrentState;

        jpaQueryFactory
                .delete(qProfileCurrentState)
                .where(qProfileCurrentState.profile.id.eq(profileId))
                .execute();
    }
}
