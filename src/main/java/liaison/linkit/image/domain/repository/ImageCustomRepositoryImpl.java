package liaison.linkit.image.domain.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import liaison.linkit.image.domain.Image;
import liaison.linkit.image.domain.QImage;
import liaison.linkit.profile.domain.log.QProfileLogImage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ImageCustomRepositoryImpl implements ImageCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Image> getUnusedImages(final LocalDateTime threshold) {
        QImage image = QImage.image;
        QProfileLogImage profileLogImage = QProfileLogImage.profileLogImage;

        return jpaQueryFactory.selectFrom(image)
                .leftJoin(profileLogImage).on(profileLogImage.image.eq(image))
                .where(
                        image.isTemporary.eq(true)
                                .and(image.createdAt.loe(threshold))
                                .and(profileLogImage.id.isNull())
                )
                .fetch();
    }

    @Override
    public List<Image> findByImageUrls(final List<String> imagePaths) {
        if (imagePaths == null || imagePaths.isEmpty()) {
            return Collections.emptyList();
        }

        QImage qImage = QImage.image;

        return jpaQueryFactory.selectFrom(qImage)
                .where(qImage.imageUrl.in(imagePaths))
                .fetch();
    }
}
