package liaison.linkit.profile.domain.repository.log;

import java.util.List;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import liaison.linkit.profile.domain.log.ProfileLogComment;
import liaison.linkit.profile.domain.log.QProfileLogComment;
import liaison.linkit.profile.domain.profile.QProfile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ProfileLogCommentCustomRepositoryImpl implements ProfileLogCommentCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<ProfileLogComment> findTopLevelCommentsByProfileLogId(
            Long profileLogId, Pageable pageable) {
        QProfileLogComment profileLogComment = QProfileLogComment.profileLogComment;
        QProfile profile = QProfile.profile;

        // 1. 상위 레벨 댓글 ID 목록 조회 (프로필 조인 없이)
        List<Long> commentIds =
                queryFactory
                        .select(profileLogComment.id)
                        .from(profileLogComment)
                        .where(profileLogIdEq(profileLogId), isTopLevelComment())
                        .orderBy(profileLogComment.createdAt.asc())
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .fetch();

        if (commentIds.isEmpty()) {
            return Page.empty(pageable);
        }

        // 2. ID로 댓글 엔티티 조회 (프로필 조인 포함)
        List<ProfileLogComment> content =
                queryFactory
                        .selectFrom(profileLogComment)
                        .leftJoin(profileLogComment.profile, profile)
                        .fetchJoin()
                        .where(profileLogComment.id.in(commentIds))
                        .orderBy(profileLogComment.createdAt.asc())
                        .fetch();

        // 3. 총 개수 쿼리
        JPAQuery<Long> countQuery =
                queryFactory
                        .select(profileLogComment.count())
                        .from(profileLogComment)
                        .where(profileLogIdEq(profileLogId), isTopLevelComment());

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    @Override
    public List<ProfileLogComment> findRepliesByParentCommentId(Long parentCommentId) {
        QProfileLogComment profileLogComment = QProfileLogComment.profileLogComment;
        QProfile profile = QProfile.profile;

        // 1. 대댓글 ID 목록 조회 (프로필 조인 없이)
        List<Long> replyIds =
                queryFactory
                        .select(profileLogComment.id)
                        .from(profileLogComment)
                        .where(parentCommentIdEq(parentCommentId), isNotDeleted())
                        .orderBy(profileLogComment.createdAt.asc())
                        .fetch();

        if (replyIds.isEmpty()) {
            return List.of();
        }

        // 2. ID로 대댓글 엔티티 조회 (프로필 조인 포함)
        return queryFactory
                .selectFrom(profileLogComment)
                .leftJoin(profileLogComment.profile, profile)
                .fetchJoin()
                .where(profileLogComment.id.in(replyIds))
                .orderBy(profileLogComment.createdAt.asc())
                .fetch();
    }

    @Override
    public Page<ProfileLogComment> findCommentsByProfileId(Long profileId, Pageable pageable) {
        QProfileLogComment profileLogComment = QProfileLogComment.profileLogComment;

        List<ProfileLogComment> content =
                queryFactory
                        .selectFrom(profileLogComment)
                        .join(profileLogComment.profileLog)
                        .fetchJoin()
                        .where(profileIdEq(profileId), isNotDeleted())
                        .orderBy(profileLogComment.createdAt.desc())
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .fetch();

        JPAQuery<Long> countQuery =
                queryFactory
                        .select(profileLogComment.count())
                        .from(profileLogComment)
                        .where(profileIdEq(profileId), isNotDeleted());

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    @Override
    public Long countCommentsByProfileLogId(Long profileLogId) {
        QProfileLogComment profileLogComment = QProfileLogComment.profileLogComment;

        return queryFactory
                .select(profileLogComment.count())
                .from(profileLogComment)
                .where(profileLogIdEq(profileLogId), isNotDeleted())
                .fetchOne();
    }

    // 조건 표현식 메서드
    private BooleanExpression profileLogIdEq(Long profileLogId) {
        QProfileLogComment profileLogComment = QProfileLogComment.profileLogComment;
        return profileLogId != null ? profileLogComment.profileLog.id.eq(profileLogId) : null;
    }

    private BooleanExpression profileIdEq(Long profileId) {
        QProfileLogComment profileLogComment = QProfileLogComment.profileLogComment;
        return profileId != null ? profileLogComment.profile.id.eq(profileId) : null;
    }

    private BooleanExpression parentCommentIdEq(Long parentCommentId) {
        QProfileLogComment profileLogComment = QProfileLogComment.profileLogComment;
        return parentCommentId != null
                ? profileLogComment.parentComment.id.eq(parentCommentId)
                : null;
    }

    private BooleanExpression isTopLevelComment() {
        QProfileLogComment profileLogComment = QProfileLogComment.profileLogComment;
        return profileLogComment.parentComment.isNull();
    }

    private BooleanExpression isNotDeleted() {
        QProfileLogComment profileLogComment = QProfileLogComment.profileLogComment;
        return profileLogComment.isDeleted.eq(false);
    }
}
