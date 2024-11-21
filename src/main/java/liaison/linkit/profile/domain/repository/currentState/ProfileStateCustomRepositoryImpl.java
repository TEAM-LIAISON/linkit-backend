package liaison.linkit.profile.domain.repository.currentState;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import liaison.linkit.common.domain.ProfileState;
import liaison.linkit.common.domain.QProfileState;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class ProfileStateCustomRepositoryImpl implements ProfileStateCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<ProfileState> findByStateName(final String profileStateName) {
        QProfileState qProfileState = QProfileState.profileState;

        ProfileState profileState = jpaQueryFactory
                .selectFrom(qProfileState)
                .where(qProfileState.profileStateName.eq(profileStateName))
                .fetchOne();

        return Optional.ofNullable(profileState);
    }

}
