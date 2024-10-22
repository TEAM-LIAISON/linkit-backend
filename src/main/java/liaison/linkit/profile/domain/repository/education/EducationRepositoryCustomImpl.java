package liaison.linkit.profile.domain.repository.education;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class EducationRepositoryCustomImpl implements EducationRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;
}
