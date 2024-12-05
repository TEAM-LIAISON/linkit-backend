package liaison.linkit.profile.domain.repository.log;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import liaison.linkit.profile.domain.ProfileLogImage;
import liaison.linkit.profile.domain.QProfileLogImage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProfileLogImageCustomRepositoryImpl implements ProfileLogImageCustomRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<ProfileLogImage> getUnusedProfileLogImages(final LocalDateTime threshold) {
        QProfileLogImage profileLogImage = QProfileLogImage.profileLogImage;

        return queryFactory.selectFrom(profileLogImage)
                .where(
                        profileLogImage.isTemporary.eq(true)
                                .and(profileLogImage.createdAt.loe(threshold))
                )
                .fetch();
    }
}
