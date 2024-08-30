package liaison.linkit.profile.domain.repository.attach;

import com.querydsl.jpa.impl.JPAQueryFactory;
import liaison.linkit.profile.domain.attach.AttachUrl;
import liaison.linkit.profile.domain.attach.QAttachUrl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
public class AttachUrlRepositoryCustomImpl implements AttachUrlRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<AttachUrl> findByProfileId(Long profileId) {
        QAttachUrl attachUrl = QAttachUrl.attachUrl;

        AttachUrl result = jpaQueryFactory
                .selectFrom(attachUrl)
                .where(attachUrl.profile.id.eq(profileId))
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public boolean existsByProfileId(final Long profileId) {
        QAttachUrl attachUrl = QAttachUrl.attachUrl;

        long count = jpaQueryFactory
                .selectFrom(attachUrl)
                .where(attachUrl.profile.id.eq(profileId))
                .fetchCount(); // 레코드의 수를 반환

        return count > 0; // 0보다 크면 true, 그렇지 않으면 false
    }

    @Override
    public List<AttachUrl> findAllByProfileId(final Long profileId){
        QAttachUrl attachUrl = QAttachUrl.attachUrl;
        return jpaQueryFactory.selectFrom(attachUrl)
                .where(attachUrl.profile.id.eq(profileId))
                .fetch();
    }


    @Override
    @Transactional
    public void deleteAllByProfileId(final Long profileId){
        log.info("Deleting all AttachUrls for profileId: {}", profileId);
        QAttachUrl attachUrl = QAttachUrl.attachUrl;
        jpaQueryFactory.delete(attachUrl)
                .where(attachUrl.profile.id.eq(profileId))
                .execute();
    }
}
