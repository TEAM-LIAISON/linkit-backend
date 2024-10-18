package liaison.linkit.profile.domain.repository.education;

import com.querydsl.jpa.impl.JPAQueryFactory;
import liaison.linkit.profile.domain.ProfileEducation;
import liaison.linkit.profile.domain.education.QEducation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class EducationRepositoryCustomImpl implements EducationRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public boolean existsByProfileId(Long profileId) {
        QEducation education = QEducation.education;

        Integer count = jpaQueryFactory
                .selectOne()
                .from(education)
                .where(education.profile.id.eq(profileId))
                .fetchFirst();

        return count != null;
    }

    @Override
    public ProfileEducation findByProfileId(Long profileId) {
        QEducation education = QEducation.education;

        return jpaQueryFactory
                .selectFrom(education)
                .where(education.profile.id.eq(profileId))
                .fetchOne();
    }

    @Override
    public List<ProfileEducation> findAllByProfileId(Long profileId) {
        QEducation education = QEducation.education;

        return jpaQueryFactory
                .selectFrom(education)
                .where(education.profile.id.eq(profileId))
                .fetch();
    }

    @Override
    @Transactional
    public void deleteAllByProfileId(Long profileId) {
        QEducation education = QEducation.education;

        jpaQueryFactory
                .delete(education)
                .where(education.profile.id.eq(profileId))
                .execute();
    }
}
