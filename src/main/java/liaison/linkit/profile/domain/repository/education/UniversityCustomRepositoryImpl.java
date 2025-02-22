package liaison.linkit.profile.domain.repository.education;

import java.util.Optional;

import com.querydsl.jpa.impl.JPAQueryFactory;
import liaison.linkit.common.domain.QUniversity;
import liaison.linkit.common.domain.University;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UniversityCustomRepositoryImpl implements UniversityCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<University> findUniversityByUniversityName(final String universityName) {
        QUniversity qUniversity = QUniversity.university;

        University university =
                jpaQueryFactory
                        .selectFrom(qUniversity)
                        .where(qUniversity.universityName.eq(universityName))
                        .fetchOne();

        return Optional.ofNullable(university);
    }
}
