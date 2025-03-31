package liaison.linkit.profile.business.service;

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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProfileLogCommentService {

    final ProfileQueryAdapter profileQueryAdapter;
    final ProfileLogQueryAdapter profileLogQueryAdapter;
    final ProfileLogCommentQueryAdapter profileLogCommentQueryAdapter;
    final ProfileLogCommentCommandAdapter profileLogCommentCommandAdapter;

    final ProfileLogCommentMapper profileLogCommentMapper;

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
}
