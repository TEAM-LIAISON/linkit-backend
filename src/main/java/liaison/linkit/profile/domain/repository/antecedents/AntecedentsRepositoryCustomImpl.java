package liaison.linkit.profile.domain.repository.antecedents;

import com.querydsl.jpa.impl.JPAQueryFactory;
import liaison.linkit.profile.domain.antecedents.Antecedents;
import liaison.linkit.profile.domain.antecedents.QAntecedents;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
public class AntecedentsRepositoryCustomImpl implements AntecedentsRepositoryCustom{
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public boolean existsByProfileId(Long profileId) {
        QAntecedents antecedents = QAntecedents.antecedents;

        Integer count = jpaQueryFactory
                .selectOne()
                .from(antecedents)
                .where(antecedents.profile.id.eq(profileId))
                .fetchFirst();

        return count != null;
    }

    @Override
    public Optional<Antecedents> findByProfileId(Long profileId) {
        QAntecedents antecedents = QAntecedents.antecedents;

        Antecedents result = jpaQueryFactory
                .selectFrom(antecedents)
                .where(antecedents.profile.id.eq(profileId))
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public List<Antecedents> findAllByProfileId(Long profileId) {
        QAntecedents antecedents = QAntecedents.antecedents;

        return jpaQueryFactory
                .selectFrom(antecedents)
                .where(antecedents.profile.id.eq(profileId))
                .fetch();
    }

    @Override
    public void deleteAllByProfileId(Long profileId) {
        QAntecedents antecedents = QAntecedents.antecedents;

        jpaQueryFactory
                .delete(antecedents)
                .where(antecedents.profile.id.eq(profileId))
                .execute();
    }
}
