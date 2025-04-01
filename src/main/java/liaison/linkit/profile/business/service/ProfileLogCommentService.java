package liaison.linkit.profile.business.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProfileLogCommentService {

    // Adapters
    private final ProfileQueryAdapter profileQueryAdapter;
    private final ProfileLogQueryAdapter profileLogQueryAdapter;
    private final ProfileLogCommentQueryAdapter profileLogCommentQueryAdapter;
    private final ProfileLogCommentCommandAdapter profileLogCommentCommandAdapter;

    // Mappers
    private final ProfileLogCommentMapper profileLogCommentMapper;

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
     * 프로필 로그 댓글 목록을 페이지 없이 조회합니다.
     *
     * @param memberId 조회 요청자의 회원 ID (선택적)
     * @param profileLogId 조회할 프로필 로그 ID
     * @return 댓글 목록 응답
     */
    @Transactional(readOnly = true)
    public ProfileLogCommentResponseDTO.PageResponse getPageProfileLogComments(
            final Optional<Long> memberId, final Long profileLogId) {
        // 1. 대상 프로필 로그 조회
        final ProfileLog targetProfileLog = profileLogQueryAdapter.getProfileLog(profileLogId);

        // 2. 댓글 목록 조회 (페이징 없이 모든 댓글 조회)
        final List<ProfileLogComment> comments =
                profileLogCommentQueryAdapter.getProfileLogComments(profileLogId);

        // 3. 댓글 목록 응답 생성
        List<ParentCommentResponse> commentResponses =
                comments.stream()
                        .map(
                                comment ->
                                        profileLogCommentMapper.toProfileLogCommentResponse(
                                                comment, memberId))
                        .filter(comment -> comment != null) // null인 응답은 필터링 (삭제된 댓글)
                        .collect(Collectors.toList());

        return ProfileLogCommentResponseDTO.PageResponse.builder()
                .comments(commentResponses)
                .totalElements(commentResponses.size())
                .totalPages(1)
                .currentPage(0)
                .hasNext(false)
                .build();
    }

    /**
     * 프로필 로그 댓글을 페이징하여 조회합니다.
     *
     * @param memberId 조회 요청자의 회원 ID (선택적)
     * @param profileLogId 조회할 프로필 로그 ID
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기
     * @return 페이징된 댓글 목록 응답
     */
    @Transactional(readOnly = true)
    public ProfileLogCommentResponseDTO.PageResponse getPageProfileLogComments(
            final Optional<Long> memberId,
            final Long profileLogId,
            final int page,
            final int size) {
        // 1. 대상 프로필 로그 조회
        final ProfileLog targetProfileLog = profileLogQueryAdapter.getProfileLog(profileLogId);

        // 2. 페이징 객체 생성
        final Pageable pageable = PageRequest.of(page, size);

        // 3. 댓글 목록 조회 (페이징)
        final Page<ProfileLogComment> commentsPage =
                profileLogCommentQueryAdapter.getPageProfileLogComments(profileLogId, pageable);

        // 4. 댓글 목록 응답 생성
        List<ParentCommentResponse> commentResponses =
                commentsPage.getContent().stream()
                        .map(
                                comment ->
                                        profileLogCommentMapper.toProfileLogCommentResponse(
                                                comment, memberId))
                        .filter(Objects::nonNull) // null인 응답은 필터링 (삭제된 댓글)
                        .collect(Collectors.toList());

        return ProfileLogCommentResponseDTO.PageResponse.builder()
                .comments(commentResponses)
                .totalElements(commentsPage.getTotalElements())
                .totalPages(commentsPage.getTotalPages())
                .currentPage(page)
                .hasNext(commentsPage.hasNext())
                .build();
    }
}
