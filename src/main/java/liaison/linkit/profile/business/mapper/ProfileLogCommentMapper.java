package liaison.linkit.profile.business.mapper;

import java.util.List;
import java.util.Optional;

import liaison.linkit.common.annotation.Mapper;
import liaison.linkit.global.util.DateUtils;
import liaison.linkit.profile.domain.log.ProfileLogComment;
import liaison.linkit.profile.domain.profile.Profile;
import liaison.linkit.profile.presentation.log.dto.ProfileLogCommentResponseDTO;
import liaison.linkit.profile.presentation.log.dto.ProfileLogCommentResponseDTO.ParentCommentResponse;
import liaison.linkit.profile.presentation.log.dto.ProfileLogCommentResponseDTO.ReplyResponse;

@Mapper
public class ProfileLogCommentMapper {

    public ProfileLogCommentResponseDTO.AddProfileLogCommentResponse toAddProfileLogCommentResponse(
            final ProfileLogComment profileLogComment,
            final Profile authorProfile,
            final Long profileLogId) {
        return ProfileLogCommentResponseDTO.AddProfileLogCommentResponse.builder()
                .commentId(profileLogComment.getId())
                .profileLogId(profileLogId)
                .authorProfileId(authorProfile.getId())
                .authorName(authorProfile.getMember().getMemberBasicInform().getMemberName())
                .authorProfileImagePath(authorProfile.getProfileImagePath())
                .emailId(authorProfile.getMember().getEmailId())
                .content(profileLogComment.getContent())
                .createdAt(DateUtils.formatRelativeTime(profileLogComment.getCreatedAt()))
                .isParentComment(profileLogComment.getParentComment() == null)
                .parentCommentId(
                        profileLogComment.getParentComment() != null
                                ? profileLogComment.getParentComment().getId()
                                : null)
                .build();
    }

    /**
     * 프로필 로그 댓글을 응답 DTO로 변환합니다.
     *
     * @param comment 변환할 댓글 엔티티
     * @param currentMemberId 현재 요청한 회원의 ID (Optional)
     * @return 변환된 부모 댓글 응답 DTO
     */
    public ParentCommentResponse toProfileLogCommentResponse(
            final ProfileLogComment comment, final Optional<Long> currentMemberId) {

        // 댓글 작성자 정보
        Profile authorProfile = comment.getProfile();
        String authorName = authorProfile.getMember().getMemberBasicInform().getMemberName();
        String emailId = authorProfile.getMember().getEmailId();
        String profileImagePath = authorProfile.getProfileImagePath();

        // 현재 사용자가 댓글 작성자인지 확인
        boolean isAuthor =
                currentMemberId.isPresent()
                        && currentMemberId.get().equals(authorProfile.getMember().getId());

        // 대댓글 목록 조회는 ProfileLogCommentCustomRepository를 통해 별도 조회 필요
        // 현재는 빈 리스트로 초기화
        List<ReplyResponse> replies = List.of();

        return ParentCommentResponse.builder()
                .id(comment.getId())
                .authorName(authorName)
                .emailId(emailId)
                .authorProfileImagePath(profileImagePath)
                .content(comment.getContent())
                .createdAt(DateUtils.formatRelativeTime(comment.getCreatedAt()))
                .isUpdated(
                        comment.getModifiedAt().isAfter(comment.getCreatedAt()) ? "true" : "false")
                .isDeleted(comment.isDeleted())
                .isAuthor(isAuthor)
                .replies(replies)
                .build();
    }

    /**
     * 대댓글을 응답 DTO로 변환합니다.
     *
     * @param reply 변환할 대댓글 엔티티
     * @param currentMemberId 현재 요청한 회원의 ID (Optional)
     * @return 변환된 대댓글 응답 DTO
     */
    private ReplyResponse toReplyResponse(
            final ProfileLogComment reply, final Optional<Long> currentMemberId) {

        // 댓글 작성자 정보
        Profile authorProfile = reply.getProfile();
        String authorName = authorProfile.getMember().getMemberBasicInform().getMemberName();
        String emailId = authorProfile.getMember().getEmailId();
        String profileImagePath = authorProfile.getProfileImagePath();

        // 현재 사용자가 댓글 작성자인지 확인
        boolean isAuthor =
                currentMemberId.isPresent()
                        && currentMemberId.get().equals(authorProfile.getMember().getId());

        return ReplyResponse.builder()
                .id(reply.getId())
                .authorName(authorName)
                .emailId(emailId)
                .authorProfileImagePath(profileImagePath)
                .content(reply.getContent())
                .createdAt(DateUtils.formatRelativeTime(reply.getCreatedAt()))
                .isUpdated(reply.getModifiedAt().isAfter(reply.getCreatedAt()) ? "true" : "false")
                .isDeleted(reply.isDeleted())
                .isAuthor(isAuthor)
                .build();
    }
}
