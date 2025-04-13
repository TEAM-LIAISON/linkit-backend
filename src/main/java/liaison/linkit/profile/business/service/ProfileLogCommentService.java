package liaison.linkit.profile.business.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import liaison.linkit.notification.business.NotificationMapper;
import liaison.linkit.notification.domain.type.NotificationType;
import liaison.linkit.notification.domain.type.SubNotificationType;
import liaison.linkit.notification.presentation.dto.NotificationResponseDTO;
import liaison.linkit.notification.service.NotificationService;
import liaison.linkit.profile.business.mapper.ProfileLogCommentMapper;
import liaison.linkit.profile.domain.log.ProfileLog;
import liaison.linkit.profile.domain.log.ProfileLogComment;
import liaison.linkit.profile.domain.profile.Profile;
import liaison.linkit.profile.exception.log.ParentCommentBadRequestException;
import liaison.linkit.profile.exception.log.PrivateProfileLogCommentBadRequestException;
import liaison.linkit.profile.implement.log.ProfileLogCommentCommandAdapter;
import liaison.linkit.profile.implement.log.ProfileLogCommentQueryAdapter;
import liaison.linkit.profile.implement.log.ProfileLogQueryAdapter;
import liaison.linkit.profile.implement.profile.ProfileQueryAdapter;
import liaison.linkit.profile.presentation.log.dto.ProfileLogCommentRequestDTO;
import liaison.linkit.profile.presentation.log.dto.ProfileLogCommentResponseDTO;
import liaison.linkit.profile.presentation.log.dto.ProfileLogCommentResponseDTO.ParentCommentResponse;
import liaison.linkit.search.presentation.dto.cursor.CursorResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProfileLogCommentService {

    private static final Logger log = LoggerFactory.getLogger(ProfileLogCommentService.class);

    // Adapters
    private final ProfileQueryAdapter profileQueryAdapter;
    private final ProfileLogQueryAdapter profileLogQueryAdapter;
    private final ProfileLogCommentQueryAdapter profileLogCommentQueryAdapter;
    private final ProfileLogCommentCommandAdapter profileLogCommentCommandAdapter;

    // Mappers
    private final ProfileLogCommentMapper profileLogCommentMapper;
    private final NotificationMapper notificationMapper;
    private final NotificationService notificationService;

    /**
     * 프로필 로그에 댓글을 추가합니다.
     *
     * @param memberId 댓글 작성자의 회원 ID
     * @param profileLogId 댓글이 작성될 프로필 로그 ID
     * @param request 댓글 작성 요청 데이터
     * @return 댓글 작성 결과 응답
     */
    public ProfileLogCommentResponseDTO.AddProfileLogCommentResponse addProfileLogComment(
            final Long memberId,
            final Long profileLogId,
            final ProfileLogCommentRequestDTO.AddProfileLogCommentRequest request) {
        // 1. 대상 프로필 로그 조회
        final ProfileLog targetProfileLog = profileLogQueryAdapter.getProfileLog(profileLogId);

        // 2. 댓글 작성자 프로필 조회
        final Profile authorProfile = profileQueryAdapter.findByMemberId(memberId);

        // 3. 비공개 로그인 경우 작성자만 댓글 작성 가능
        if (!targetProfileLog.isLogPublic()
                && !targetProfileLog.getProfile().getId().equals(authorProfile.getId())) {
            throw PrivateProfileLogCommentBadRequestException.EXCEPTION;
        }

        // 4. 부모 댓글 처리 (대댓글인 경우)
        ProfileLogComment parentComment = null;
        if (request.getParentCommentId() != null) {
            parentComment =
                    profileLogCommentQueryAdapter.getProfileLogComment(
                            request.getParentCommentId());

            // 답글에 답글을 다는 것은 지원하지 않음
            if (parentComment.getParentComment() != null) {
                throw ParentCommentBadRequestException.EXCEPTION;
            }
        }

        // 5. 댓글 엔티티 생성
        ProfileLogComment comment =
                ProfileLogComment.builder()
                        .profileLog(targetProfileLog)
                        .profile(authorProfile)
                        .content(request.getContent())
                        .isDeleted(false)
                        .parentComment(parentComment)
                        .build();

        // 6. 댓글 저장
        ProfileLogComment savedComment =
                profileLogCommentCommandAdapter.addProfileLogComment(comment);

        // 7. 댓글 수 증가
        targetProfileLog.increaseCommentCount();

        if (parentComment != null) {
            Long parentAuthorId = parentComment.getProfile().getMember().getId();

            // 자기 자신에게 알림 보내지 않도록
            if (!parentAuthorId.equals(authorProfile.getMember().getId())) {
                notifyComment(
                        parentAuthorId, targetProfileLog, authorProfile, true // 대댓글 알림
                        );
            }

        } else {
            Long logAuthorId = targetProfileLog.getProfile().getMember().getId();

            // 자기 자신에게 알림 보내지 않도록
            if (!logAuthorId.equals(authorProfile.getMember().getId())) {
                notifyComment(
                        logAuthorId, targetProfileLog, authorProfile, false // 일반 댓글 알림
                        );
            }
        }

        // 8. 응답 생성
        return profileLogCommentMapper.toAddProfileLogCommentResponse(
                savedComment, authorProfile, profileLogId);
    }

    public ProfileLogCommentResponseDTO.UpdateProfileLogCommentResponse updateProfileLogComment(
            final Long authorMemberId,
            final Long profileLogCommentId,
            final ProfileLogCommentRequestDTO.UpdateProfileLogCommentRequest request) {
        // 1. 대상 댓글 조회
        final ProfileLogComment targetComment =
                profileLogCommentQueryAdapter.getProfileLogComment(profileLogCommentId);

        // 2. 댓글 작성자 확인 (권한 검증)
        final Profile authorProfile = profileQueryAdapter.findByMemberId(authorMemberId);
        if (!targetComment.getProfile().getId().equals(authorProfile.getId())) {
            throw new IllegalStateException("댓글 작성자만 수정할 수 있습니다.");
        }

        // 3. 댓글 내용 업데이트
        targetComment.updateContent(request.getContent());

        // 4. 응답 생성
        return profileLogCommentMapper.toUpdateProfileLogCommentResponse(
                targetComment, authorProfile, targetComment.getProfileLog().getId());
    }

    /**
     * 프로필 로그 댓글을 삭제합니다.
     *
     * @param authorMemberId 삭제 요청자의 회원 ID
     * @param profileLogCommentId 삭제할 댓글 ID
     * @return 댓글 삭제 결과 응답
     */
    public ProfileLogCommentResponseDTO.DeleteProfileLogCommentResponse deleteProfileLogComment(
            final Long authorMemberId, final Long profileLogCommentId) {
        // 1. 대상 댓글 조회
        final ProfileLogComment targetComment =
                profileLogCommentQueryAdapter.getProfileLogComment(profileLogCommentId);

        // 2. 댓글 작성자 확인 (권한 검증)
        final Profile authorProfile = profileQueryAdapter.findByMemberId(authorMemberId);
        if (!targetComment.getProfile().getId().equals(authorProfile.getId())) {
            throw new IllegalStateException("댓글 작성자만 삭제할 수 있습니다.");
        }

        // 3. 댓글 논리적 삭제 처리
        targetComment.delete();

        // 4. 댓글 수 감소
        targetComment.getProfileLog().decreaseCommentCount();

        // 5. 응답 생성
        return profileLogCommentMapper.toDeleteProfileLogCommentResponse(
                profileLogCommentId, targetComment.getProfileLog().getId());
    }

    /**
     * 프로필 로그 댓글을 페이징 없이 조회합니다.
     *
     * @param memberId 조회 요청자의 회원 ID (선택적)
     * @param profileLogId 조회할 프로필 로그 ID
     * @return 댓글 목록 응답
     */
    @Transactional(readOnly = true)
    public ProfileLogCommentResponseDTO.PageResponse getPageProfileLogComments(
            final Optional<Long> memberId, final Long profileLogId) {
        throw new UnsupportedOperationException(
                "This method is no longer supported. Use cursor-based pagination instead.");
    }

    /**
     * 프로필 로그 댓글을 무한 스크롤 방식으로 조회합니다.
     *
     * @param memberId 조회 요청자의 회원 ID (선택적)
     * @param profileLogId 조회할 프로필 로그 ID
     * @param cursor 커서 값 (이전 페이지의 마지막 댓글 ID)
     * @param size 페이지 크기
     * @return 커서 기반 댓글 목록 응답
     */
    @Transactional(readOnly = true)
    public CursorResponse<ProfileLogCommentResponseDTO.ParentCommentResponse>
            getPageProfileLogComments(
                    final Optional<Long> memberId,
                    final Long profileLogId,
                    final String cursor,
                    final int size) {
        // 1. 대상 프로필 로그 조회
        final ProfileLog targetProfileLog = profileLogQueryAdapter.getProfileLog(profileLogId);

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
        final List<ProfileLogComment> comments =
                profileLogCommentQueryAdapter.getProfileLogCommentsWithCursor(
                        profileLogId, cursorId, size);

        // 4. 댓글 목록 응답 생성 (try-catch 처리로 오류 방지)
        List<ParentCommentResponse> commentResponses =
                comments.stream()
                        .map(
                                comment -> {
                                    try {
                                        return profileLogCommentMapper.toProfileLogCommentResponse(
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
                                        List<ProfileLogComment> replies =
                                                profileLogCommentQueryAdapter
                                                        .findRepliesByParentCommentId(
                                                                comment.getId());

                                        List<ProfileLogCommentResponseDTO.ReplyResponse>
                                                replyResponses = new java.util.ArrayList<>();

                                        for (ProfileLogComment reply : replies) {
                                            try {
                                                // 안전한 공개 메서드 사용
                                                ProfileLogCommentResponseDTO.ReplyResponse
                                                        replyResponse =
                                                                profileLogCommentMapper
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

                                        return ParentCommentResponse.builder()
                                                .id(comment.getId())
                                                .authorName("(탈퇴한 사용자)")
                                                .emailId(null)
                                                .authorProfileImagePath(null)
                                                .content(comment.getContent())
                                                .createdAt(null)
                                                .isUpdated(false)
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

        return CursorResponse.<ProfileLogCommentResponseDTO.ParentCommentResponse>builder()
                .content(commentResponses)
                .nextCursor(nextCursor)
                .hasNext(nextCursor != null)
                .build();
    }

    private void notifyComment(
            Long receiverMemberId,
            ProfileLog targetProfileLog,
            Profile authorProfile,
            boolean isChildComment) {
        NotificationResponseDTO.NotificationDetails notificationDetails =
                isChildComment
                        ? NotificationResponseDTO.NotificationDetails.childComment(
                                "PROFILE",
                                targetProfileLog.getProfile().getMember().getEmailId(),
                                targetProfileLog.getId(),
                                null,
                                null,
                                "CHILD_COMMENT",
                                authorProfile.getMember().getMemberBasicInform().getMemberName(),
                                authorProfile.getProfileImagePath())
                        : NotificationResponseDTO.NotificationDetails.parentComment(
                                "PROFILE",
                                targetProfileLog.getProfile().getMember().getEmailId(),
                                targetProfileLog.getId(),
                                null,
                                null,
                                "PARENT_COMMENT",
                                authorProfile.getMember().getMemberBasicInform().getMemberName(),
                                authorProfile.getProfileImagePath());

        notificationService.alertNewNotification(
                notificationMapper.toNotification(
                        receiverMemberId,
                        NotificationType.COMMENT,
                        isChildComment
                                ? SubNotificationType.CHILD_COMMENT
                                : SubNotificationType.PARENT_COMMENT,
                        notificationDetails));
    }
}
