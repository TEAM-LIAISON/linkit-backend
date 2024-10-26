package liaison.linkit.profile.domain.repository.log;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProfileLogCustomRepositoryImpl implements ProfileLogCustomRepository {

    private final JPAQueryFactory queryFactory;
    
}
