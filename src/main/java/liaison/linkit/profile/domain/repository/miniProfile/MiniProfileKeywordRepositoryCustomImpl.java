package liaison.linkit.profile.domain.repository.miniProfile;

import com.querydsl.jpa.impl.JPAQueryFactory;
import liaison.linkit.profile.domain.miniProfile.MiniProfileKeyword;
import liaison.linkit.profile.domain.miniProfile.QMiniProfileKeyword;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class MiniProfileKeywordRepositoryCustomImpl implements MiniProfileKeywordRepositoryCustom{
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<MiniProfileKeyword> findAllByMiniProfileId(Long miniProfileId) {
        QMiniProfileKeyword miniProfileKeyword = QMiniProfileKeyword.miniProfileKeyword;

        return jpaQueryFactory
                .selectFrom(miniProfileKeyword)
                .where(miniProfileKeyword.miniProfile.id.eq(miniProfileId))
                .fetch();
    }

    @Override
    @Transactional
    public void deleteAllByMiniProfileId(Long miniProfileId) {
        QMiniProfileKeyword miniProfileKeyword = QMiniProfileKeyword.miniProfileKeyword;

        jpaQueryFactory
                .delete(miniProfileKeyword)
                .where(miniProfileKeyword.miniProfile.id.eq(miniProfileId))
                .execute();
    }

}
