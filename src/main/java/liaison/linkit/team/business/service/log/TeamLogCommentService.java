package liaison.linkit.team.business.service.log;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import liaison.linkit.profile.domain.profile.Profile;
import liaison.linkit.profile.implement.profile.ProfileQueryAdapter;
import liaison.linkit.search.presentation.dto.cursor.CursorResponse;
import liaison.linkit.team.business.mapper.log.TeamLogCommentMapper;
import liaison.linkit.team.domain.log.TeamLog;
import liaison.linkit.team.domain.log.TeamLogComment;
import liaison.linkit.team.exception.log.ParentCommentBadRequestException;
import liaison.linkit.team.exception.log.PrivateTeamLogCommentBadRequestException;
import liaison.linkit.team.implement.log.TeamLogCommentCommandAdapter;
import liaison.linkit.team.implement.log.TeamLogCommentQueryAdapter;
import liaison.linkit.team.implement.log.TeamLogQueryAdapter;
import liaison.linkit.team.implement.teamMember.TeamMemberQueryAdapter;
import liaison.linkit.team.presentation.log.dto.TeamLogCommentRequestDTO;
import liaison.linkit.team.presentation.log.dto.TeamLogCommentResponseDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class TeamLogCommentService {

    private static final Logger log = LoggerFactory.getLogger(TeamLogCommentService.class);

    // Adapters
    private final ProfileQueryAdapter profileQueryAdapter;
    private final TeamMemberQueryAdapter teamMemberQueryAdapter;
    private final TeamLogQueryAdapter teamLogQueryAdapter;
    private final TeamLogCommentQueryAdapter teamLogCommentQueryAdapter;
    private final TeamLogCommentCommandAdapter teamLogCommentCommandAdapter;

    // Mappers
    private final TeamLogCommentMapper teamLogCommentMapper;

    /**
     * 프로필 로그에 댓글을 추가합니다.
     *
     * @param memberId 댓글 작성자의 회원 ID
     * @param teamLogId 댓글이 작성될 팀 로그 ID
     * @param request 댓글 작성 요청 데이터
     * @return 댓글 작성 결과 응답
     */
    public TeamLogCommentResponseDTO.AddTeamLogCommentResponse addTeamLogComment(
            final Long memberId,
            final Long teamLogId,
            final TeamLogCommentRequestDTO.AddTeamLogCommentRequest request) {
        // 1. 대상 팀 로그 조회
        final TeamLog targetTeamLog = teamLogQueryAdapter.getTeamLog(teamLogId);

        // 2. 댓글 작성자 프로필 조회
        final Profile authorProfile = profileQueryAdapter.findByMemberId(memberId);

        // 3. 비공개 로그인 경우 팀의 관리자, 오너만 댓글 작성 가능
        if (!targetTeamLog.isLogPublic()
                && !teamMemberQueryAdapter.isOwnerOrManagerOfTeam(
                        targetTeamLog.getTeam().getId(), authorProfile.getMember().getId())) {
            throw PrivateTeamLogCommentBadRequestException.EXCEPTION;
        }

        // 4. 부모 댓글 처리 (대댓글인 경우)
        TeamLogComment parentComment = null;
        if (request.getParentCommentId() != null) {
            parentComment =
                    teamLogCommentQueryAdapter.getTeamLogComment(request.getParentCommentId());

            // 답글에 답글을 다는 것은 지원하지 않음
            if (parentComment.getParentComment() != null) {
                throw ParentCommentBadRequestException.EXCEPTION;
            }
        }

        // 댓글 엔티티 생성
        final TeamLogComment comment =
                TeamLogComment.builder()
                        .teamLog(targetTeamLog)
                        .profile(authorProfile)
                        .content(request.getContent())
                        .parentComment(parentComment)
                        .isDeleted(false)
                        .build();

        // 6. 댓글 저장
        final TeamLogComment savedComment = teamLogCommentCommandAdapter.addTeamLogComment(comment);

        // 7. 댓글 수 증가
        targetTeamLog.increaseCommentCount();

        // 8. 응답 생성
        return teamLogCommentMapper.toAddTeamLogCommentResponse(
                savedComment, authorProfile, teamLogId);
    }

    public TeamLogCommentResponseDTO.UpdateTeamLogCommentResponse updateTeamLogComment(
            final Long authorMemberId,
            final Long teamLogCommentId,
            final TeamLogCommentRequestDTO.UpdateTeamLogCommentRequest request) {

        // 1. 대상 댓글 조회
        final TeamLogComment targetComment =
                teamLogCommentQueryAdapter.getTeamLogComment(teamLogCommentId);

        // 2. 댓글 작성자 확인 (권한 검증)
        final Profile authorProfile = profileQueryAdapter.findByMemberId(authorMemberId);
        if (!targetComment.getProfile().getId().equals(authorProfile.getId())) {
            throw new IllegalStateException("댓글 작성자만 수정할 수 있습니다.");
        }

        // 3. 댓글 내용 업데이트
        targetComment.updateContent(request.getContent());

        // 4. 응답 생성
        return teamLogCommentMapper.toUpdateTeamLogCommentResponse(
                targetComment, authorProfile, targetComment.getTeamLog().getId());
    }

    /**
     * 팀 로그 댓글을 삭제합니다.
     *
     * @param authorMemberId 삭제 요청자의 회원 ID
     * @param teamLogCommentId 삭제할 댓글 ID
     * @return 댓글 삭제 결과 응답
     */
    @Transactional
    public TeamLogCommentResponseDTO.DeleteTeamLogCommentResponse deleteTeamLogComment(
            final Long authorMemberId, final Long teamLogCommentId) {

        // 1. 대상 댓글 조회
        final TeamLogComment targetComment =
                teamLogCommentQueryAdapter.getTeamLogComment(teamLogCommentId);

        // 2. 댓글 작성자 확인 (권한 검증)
        final Profile authorProfile = profileQueryAdapter.findByMemberId(authorMemberId);
        if (!targetComment.getProfile().getId().equals(authorProfile.getId())) {
            throw new IllegalStateException("댓글 작성자만 삭제할 수 있습니다.");
        }

        // 3. 댓글 논리적 삭제 처리
        targetComment.delete();

        // 4. 댓글 수 감소
        targetComment.getTeamLog().decreaseCommentCount();

        // 5. 응답 생성
        return teamLogCommentMapper.toDeleteTeamLogCommentResponse(
                teamLogCommentId, targetComment.getTeamLog().getId());
    }

    /**
     * 프로필 로그 댓글을 무한 스크롤 방식으로 조회합니다.
     *
     * @param memberId 조회 요청자의 회원 ID (선택적)
     * @param teamLogId 조회할 프로필 로그 ID
     * @param cursor 커서 값 (이전 페이지의 마지막 댓글 ID)
     * @param size 페이지 크기
     * @return 커서 기반 댓글 목록 응답
     */
    @Transactional(readOnly = true)
    public CursorResponse<TeamLogCommentResponseDTO.ParentCommentResponse> getPageTeamLogComments(
            final Optional<Long> memberId,
            final Long teamLogId,
            final String cursor,
            final int size) {
        // 1. 대상 프로필 로그 조회
        final TeamLog targetTeamLog = teamLogQueryAdapter.getTeamLog(teamLogId);

        // 2. 커서 값 처리
        Long cursorId = null;
        if (cursor != null && !cursor.isBlank()) {
            try {
                cursorId = Long.parseLong(cursor);
            } catch (NumberFormatException e) {
                log.warn("잘못된 커서 값: {}", cursor);
                // 잘못된 커서 값은 무시하고 첫 페이지를 조회
            }
        }

        // 3. 댓글 목록 조회 (커서 기반)
        final List<TeamLogComment> comments =
                teamLogCommentQueryAdapter.getTeamLogCommentsWithCursor(teamLogId, cursorId, size);

        // 4. 댓글 목록 응답 생성 (try-catch 처리로 오류 방지)
        List<TeamLogCommentResponseDTO.ParentCommentResponse> commentResponses =
                comments.stream()
                        .map(
                                comment -> {
                                    try {
                                        return teamLogCommentMapper.toTeamLogCommentResponse(
                                                comment, memberId);
                                    } catch (Exception e) {
                                        // 삭제된 프로필 관련 오류 발생 시 처리
                                        log.warn(
                                                "Error processing comment ID: " + comment.getId(),
                                                e);
                                        if (comment.isDeleted()) {
                                            return null; // 삭제된 댓글은 null 반환
                                        }

                                        // 프로필 정보 접근 시 오류가 발생한 경우, 탈퇴한 사용자로 처리
                                        List<TeamLogComment> replies =
                                                teamLogCommentQueryAdapter
                                                        .findRepliesByParentCommentId(
                                                                comment.getId());

                                        List<TeamLogCommentResponseDTO.ReplyResponse>
                                                replyResponses = new java.util.ArrayList<>();

                                        for (TeamLogComment reply : replies) {
                                            try {
                                                // 안전한 공개 메서드 사용
                                                TeamLogCommentResponseDTO.ReplyResponse
                                                        replyResponse =
                                                                teamLogCommentMapper
                                                                        .toReplyResponseForOrphanedComment(
                                                                                reply, memberId);
                                                if (replyResponse != null) {
                                                    replyResponses.add(replyResponse);
                                                }
                                            } catch (Exception ex) {
                                                log.warn(
                                                        "Error processing reply ID: "
                                                                + reply.getId(),
                                                        ex);
                                            }
                                        }

                                        return TeamLogCommentResponseDTO.ParentCommentResponse
                                                .builder()
                                                .id(comment.getId())
                                                .authorName("(탈퇴한 사용자)")
                                                .emailId(null)
                                                .authorProfileImagePath(null)
                                                .content(comment.getContent())
                                                .createdAt(null)
                                                .isUpdated("false")
                                                .isDeleted(false)
                                                .isAuthor(false)
                                                .replies(replyResponses)
                                                .build();
                                    }
                                })
                        .filter(comment -> comment != null) // null인 응답은 필터링 (삭제된 댓글)
                        .collect(Collectors.toList());

        // 5. 다음 커서 값 설정 (조회된 댓글이 size보다 적으면 다음 페이지 없음)
        String nextCursor = null;
        if (commentResponses.size() == size && !commentResponses.isEmpty()) {
            nextCursor = String.valueOf(commentResponses.get(commentResponses.size() - 1).getId());
        }

        return CursorResponse.<TeamLogCommentResponseDTO.ParentCommentResponse>builder()
                .content(commentResponses)
                .nextCursor(nextCursor)
                .hasNext(nextCursor != null)
                .build();
    }
}
