package liaison.linkit.team.domain.repository.log;

import java.util.List;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import liaison.linkit.team.domain.log.QTeamLogComment;
import liaison.linkit.team.domain.log.TeamLogComment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TeamLogCommentCustomRepositoryImpl implements TeamLogCommentCustomRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<TeamLogComment> findRepliesByParentCommentId(Long parentCommentId) {
        QTeamLogComment teamLogComment = QTeamLogComment.teamLogComment;

        // 직접 엔티티 조회 (프로필 조인 없이)
        return queryFactory
                .selectFrom(teamLogComment)
                .where(parentCommentIdEq(parentCommentId), isNotDeleted())
                .orderBy(teamLogComment.createdAt.asc())
                .fetch();
    }

    @Override
    public List<TeamLogComment> findTopLevelCommentsByTeamLogIdWithCursor(
            Long teamLogId, Long cursorId, int size) {
        QTeamLogComment teamLogComment = QTeamLogComment.teamLogComment;

        // 커서 ID가 있는 경우 해당 ID보다 작은 ID의 댓글을 조회 (ID 내림차순으로 최신순)
        BooleanExpression cursorCondition =
                cursorId != null ? teamLogComment.id.lt(cursorId) : null;

        // 직접 엔티티 조회 (프로필 조인 없이)
        return queryFactory
                .selectFrom(teamLogComment)
                .where(teamLogIdEq(teamLogId), isTopLevelComment(), cursorCondition)
                .orderBy(teamLogComment.id.desc()) // ID 내림차순 정렬 (최신순)
                .limit(size)
                .fetch();
    }

    // 조건 표현식 메서드
    private BooleanExpression teamLogIdEq(Long teamLogId) {
        QTeamLogComment teamLogComment = QTeamLogComment.teamLogComment;
        return teamLogId != null ? teamLogComment.teamLog.id.eq(teamLogId) : null;
    }

    private BooleanExpression profileIdEq(Long profileId) {
        QTeamLogComment teamLogComment = QTeamLogComment.teamLogComment;
        return profileId != null ? teamLogComment.profile.id.eq(profileId) : null;
    }

    private BooleanExpression parentCommentIdEq(Long parentCommentId) {
        QTeamLogComment teamLogComment = QTeamLogComment.teamLogComment;
        return parentCommentId != null ? teamLogComment.parentComment.id.eq(parentCommentId) : null;
    }

    private BooleanExpression isTopLevelComment() {
        QTeamLogComment teamLogComment = QTeamLogComment.teamLogComment;
        return teamLogComment.parentComment.isNull();
    }

    private BooleanExpression isNotDeleted() {
        QTeamLogComment teamLogComment = QTeamLogComment.teamLogComment;
        return teamLogComment.isDeleted.eq(false);
    }
}
