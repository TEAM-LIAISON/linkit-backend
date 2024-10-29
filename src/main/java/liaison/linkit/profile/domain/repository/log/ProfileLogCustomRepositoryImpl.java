package liaison.linkit.profile.domain.repository.log;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import liaison.linkit.profile.domain.ProfileLog;
import liaison.linkit.profile.domain.QProfileLog;
import liaison.linkit.profile.presentation.log.dto.ProfileLogResponseDTO.ProfileLogItem;
import liaison.linkit.profile.presentation.log.dto.ProfileLogResponseDTO.ProfileLogItems;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProfileLogCustomRepositoryImpl implements ProfileLogCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<ProfileLog> getProfileLogs(final Long memberId) {
        QProfileLog qProfileLog = QProfileLog.profileLog;

        return queryFactory
                .selectFrom(qProfileLog)
                .where(qProfileLog.profile.member.id.eq(memberId))
                .fetch();
    }
}
