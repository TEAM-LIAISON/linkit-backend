package liaison.linkit.profile.domain.repository.attach;

import com.querydsl.jpa.impl.JPAQueryFactory;
import liaison.linkit.profile.domain.attach.AttachUrl;
import liaison.linkit.profile.domain.attach.QAttachUrl;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
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

        Integer count = jpaQueryFactory
                .selectOne()
                .from(attachUrl)
                .where(attachUrl.profile.id.eq(profileId))
                .fetchFirst(); // fetchFirst()는 레코드가 존재하는지 확인하기 위해 첫 번째 결과를 가져옵니다.

        // 존재하면 true, 그렇지 않으면 false 반환
        return count != null;
    }

    @Override
    public List<AttachUrl> findAllByProfileId(final Long profileId){
        QAttachUrl attachUrl = QAttachUrl.attachUrl;
        return jpaQueryFactory.selectFrom(attachUrl)
                .where(attachUrl.profile.id.eq(profileId))
                .fetch();
    }

    @Override
    public void deleteAllByProfileId(final Long profileId){
        QAttachUrl attachUrl = QAttachUrl.attachUrl;
        jpaQueryFactory.delete(attachUrl)
                .where(attachUrl.profile.id.eq(profileId))
                .execute();
    }
}
