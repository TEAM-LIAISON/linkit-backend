package liaison.linkit.profile.domain.repository.jobRole;


import com.querydsl.jpa.impl.JPAQueryFactory;
import liaison.linkit.profile.domain.role.ProfileJobRole;
import liaison.linkit.profile.domain.role.QProfileJobRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class ProfileJobRoleRepositoryCustomImpl implements ProfileJobRoleRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public boolean existsByProfileId(Long profileId) {
        QProfileJobRole profileJobRole = QProfileJobRole.profileJobRole;

        Integer count = jpaQueryFactory
                .selectOne()
                .from(profileJobRole)
                .where(profileJobRole.profile.id.eq(profileId))
                .fetchFirst();

        return count != null;
    }

    @Override
    @Transactional
    public void deleteAllByProfileId(Long profileId) {
        QProfileJobRole profileJobRole = QProfileJobRole.profileJobRole;

        jpaQueryFactory.delete(profileJobRole)
                .where(profileJobRole.profile.id.eq(profileId))
                .execute();
    }

    @Override
    public List<ProfileJobRole> findAllByProfileId(Long profileId) {
        QProfileJobRole profileJobRole = QProfileJobRole.profileJobRole;

        return jpaQueryFactory
                .selectFrom(profileJobRole)
                .where(profileJobRole.profile.id.eq(profileId))
                .fetch();
    }
}
