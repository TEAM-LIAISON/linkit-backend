package liaison.linkit.profile.domain.repository.attach;

import com.querydsl.jpa.impl.JPAQueryFactory;
import liaison.linkit.profile.domain.attach.AttachUrl;
import liaison.linkit.profile.domain.attach.QAttachUrl;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class AttachUrlRepositoryCustomImpl implements AttachUrlRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<AttachUrl> findAllByProfileId(final Long profileId){
        QAttachUrl attachUrl = QAttachUrl.attachUrl;
        return jpaQueryFactory.selectFrom(attachUrl)
                .where(attachUrl.profile.id.eq(profileId))
                .fetch();
    }
}
