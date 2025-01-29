package liaison.linkit.profile.domain.repository.link;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import liaison.linkit.profile.domain.link.ProfileLink;
import liaison.linkit.profile.domain.link.QProfileLink;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProfileLinkCustomRepositoryImpl implements ProfileLinkCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @PersistenceContext
    private EntityManager entityManager; // EntityManager 주입

    @Override
    public boolean existsByProfileId(Long profileId) {
        QProfileLink profileLink = QProfileLink.profileLink;

        Integer count = jpaQueryFactory
                .selectOne()
                .from(profileLink)
                .where(profileLink.profile.id.eq(profileId))
                .fetchFirst();

        return count != null;
    }

    @Override
    public List<ProfileLink> getProfileLinks(final Long profileId) {
        QProfileLink qProfileLink = QProfileLink.profileLink;

        return jpaQueryFactory
                .selectFrom(qProfileLink)
                .where(qProfileLink.profile.id.eq(profileId))
                .fetch();
    }

    @Override
    public void deleteAllByProfileId(Long profileId) {
        QProfileLink profileLink = QProfileLink.profileLink;

        jpaQueryFactory
                .delete(profileLink)
                .where(profileLink.profile.id.eq(profileId))
                .execute();

        entityManager.flush();
        entityManager.clear();
    }
}
