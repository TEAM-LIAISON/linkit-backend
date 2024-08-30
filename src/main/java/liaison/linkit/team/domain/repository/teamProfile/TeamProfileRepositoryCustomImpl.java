package liaison.linkit.team.domain.repository.teamProfile;

import com.querydsl.jpa.impl.JPAQueryFactory;
import liaison.linkit.global.type.StatusType;
import liaison.linkit.team.domain.QTeamProfile;
import liaison.linkit.team.domain.TeamProfile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
public class TeamProfileRepositoryCustomImpl implements TeamProfileRepositoryCustom{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<TeamProfile> findByMemberId(final Long memberId) {
        QTeamProfile teamProfile = QTeamProfile.teamProfile;

        TeamProfile result = jpaQueryFactory
                .selectFrom(teamProfile)
                .where(teamProfile.member.id.eq(memberId))
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public boolean existsByMemberId(final Long memberId) {
        QTeamProfile teamProfile = QTeamProfile.teamProfile;

        Integer count = jpaQueryFactory
                .selectOne()
                .from(teamProfile)
                .where(teamProfile.member.id.eq(memberId))
                .fetchFirst();

        return count != null;
    }

    @Override
    @Transactional
    public void deleteByMemberId(final Long memberId) {
        QTeamProfile teamProfile = QTeamProfile.teamProfile;

        jpaQueryFactory
                .update(teamProfile)
                .set(teamProfile.status, StatusType.DELETED)
                .where(teamProfile.member.id.eq(memberId))
                .execute();
    }
}
