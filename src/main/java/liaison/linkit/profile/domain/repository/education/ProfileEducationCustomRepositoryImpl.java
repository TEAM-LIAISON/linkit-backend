package liaison.linkit.profile.domain.repository.education;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import liaison.linkit.profile.domain.ProfileEducation;
import liaison.linkit.profile.domain.QProfileEducation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ProfileEducationCustomRepositoryImpl implements ProfileEducationCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<ProfileEducation> getProfileEducations(final Long memberId) {
        QProfileEducation qProfileEducation = QProfileEducation.profileEducation;

        return jpaQueryFactory
                .selectFrom(qProfileEducation)
                .where(qProfileEducation.profile.member.id.eq(memberId))
                .fetch();
    }
}
