package liaison.linkit.profile.domain.repository.region;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class ProfileRegionRepositoryCustomImpl implements ProfileRegionRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

}
