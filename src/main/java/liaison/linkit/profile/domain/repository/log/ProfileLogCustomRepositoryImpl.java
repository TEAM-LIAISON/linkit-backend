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
    public boolean existsByMemberIdAndProfileLogId(final Long memberId, final Long profileLogId) {
        QProfileLog qProfileLog = QProfileLog.profileLog;

        long exists = queryFactory
                .selectFrom(qProfileLog)
                .where(qProfileLog.id.eq(profileLogId)
                        .and(qProfileLog.profile.member.id.eq(memberId)))
                .fetchCount();

        return exists > 0;
    }

    @Override
    public ProfileLogItems getProfileLogItems(final Long memberId) {
        QProfileLog qProfileLog = QProfileLog.profileLog;

        List<ProfileLogItem> profileLogItems =
                queryFactory
                        .select(
                                Projections.constructor(
                                        ProfileLogItem.class,
                                        qProfileLog.id
                                )
                        )
                        .from(qProfileLog)
                        .where(qProfileLog.profile.member.id.eq(memberId))
                        .fetch();

        return ProfileLogItems
                .builder()
                .profileLogItems(profileLogItems)
                .build();
    }

    @Override
    public ProfileLogItem getProfileLogItem(final Long profileLogId) {
        QProfileLog qProfileLog = QProfileLog.profileLog;

        ProfileLog profileLog =
                queryFactory
                        .selectFrom(qProfileLog)
                        .where(qProfileLog.id.eq(profileLogId))
                        .fetchOne();

        return ProfileLogItem
                .builder()
                .profileLogId(profileLog.getId())
                .build();
    }
}
