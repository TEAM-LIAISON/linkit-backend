package liaison.linkit.profile.domain.repository.log;

import java.util.List;

import com.querydsl.jpa.impl.JPAQueryFactory;
import liaison.linkit.profile.domain.log.ProfileLog;
import liaison.linkit.profile.domain.log.ProfileLogImage;
import liaison.linkit.profile.domain.log.QProfileLogImage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProfileLogImageCustomRepositoryImpl implements ProfileLogImageCustomRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<ProfileLogImage> findByProfileLog(final ProfileLog profileLog) {
        QProfileLogImage qProfileLogImage = QProfileLogImage.profileLogImage;

        return queryFactory
                .selectFrom(qProfileLogImage)
                .where(qProfileLogImage.profileLog.eq(profileLog))
                .fetch();
    }
}
