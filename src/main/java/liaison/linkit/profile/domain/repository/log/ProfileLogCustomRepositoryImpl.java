package liaison.linkit.profile.domain.repository.log;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import liaison.linkit.profile.domain.ProfileLog;
import liaison.linkit.profile.domain.QProfileLog;
import liaison.linkit.profile.presentation.log.dto.ProfileLogRequestDTO.UpdateProfileLogType;
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

    @Override
    public ProfileLog updateProfileLogType(final ProfileLog profileLog, final UpdateProfileLogType updateProfileLogType) {
        QProfileLog qProfileLog = QProfileLog.profileLog;

        // QueryDSL을 사용하여 데이터베이스에서 ProfileLog 엔티티를 업데이트
        long updatedCount = queryFactory
                .update(qProfileLog)
                .set(qProfileLog.profileLogType, updateProfileLogType.getProfileLogType())
                .where(qProfileLog.id.eq(profileLog.getId()))
                .execute();

        if (updatedCount > 0) { // 업데이트 성공 확인
            profileLog.setProfileLogType(updateProfileLogType.getProfileLogType()); // 메모리 내 객체 업데이트
            return profileLog;
        } else {
            throw new IllegalStateException("프로필 로그 업데이트 실패");
        }
    }
}
