package liaison.linkit.profile.business.mapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import liaison.linkit.common.annotation.Mapper;
import liaison.linkit.global.util.DateUtils;
import liaison.linkit.member.domain.Member;
import liaison.linkit.member.domain.type.MemberState;
import liaison.linkit.profile.domain.log.ProfileLogComment;
import liaison.linkit.profile.domain.profile.Profile;
import liaison.linkit.profile.implement.log.ProfileLogCommentQueryAdapter;
import liaison.linkit.profile.presentation.log.dto.ProfileLogCommentResponseDTO;
import liaison.linkit.profile.presentation.log.dto.ProfileLogCommentResponseDTO.ParentCommentResponse;
import liaison.linkit.profile.presentation.log.dto.ProfileLogCommentResponseDTO.ReplyResponse;
import lombok.RequiredArgsConstructor;

@Mapper
@RequiredArgsConstructor
public class ProfileLogCommentMapper {

    private final ProfileLogCommentQueryAdapter profileLogCommentQueryAdapter;

    public ProfileLogCommentResponseDTO.AddProfileLogCommentResponse toAddProfileLogCommentResponse(
            final ProfileLogComment profileLogComment,
            final Profile authorProfile,
            final Long profileLogId) {
        return ProfileLogCommentResponseDTO.AddProfileLogCommentResponse.builder()
                .commentId(profileLogComment.getId())
                .profileLogId(profileLogId)
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

        // 댓글이 삭제되었고 답글이 없으면 null 반환 (삭제된 댓글은 표시하지 않음)
        if (comment.isDeleted()) {
            List<ProfileLogComment> replies =
                    profileLogCommentQueryAdapter.findRepliesByParentCommentId(comment.getId());

            // 본댓글이고 답글이 없는 경우: 흔적 없이 삭제 (null 반환)
            if (comment.getParentComment() == null && replies.isEmpty()) {
                return null;
            }

            // 답글인 경우: 흔적 없이 삭제 (null 반환)
            if (comment.getParentComment() != null) {
                return null;
            }

            // 본댓글이고 답글이 있는 경우: 삭제된 댓글로 표시
            // 대댓글 목록 조회 및 변환 (필터링 적용)
            List<ReplyResponse> filteredReplies =
                    replies.stream()
                            .map(reply -> toReplyResponse(reply, currentMemberId))
                            .filter(reply -> reply != null) // null이 아닌 대댓글만 포함
                            .collect(Collectors.toList());

            return ParentCommentResponse.builder()
                    .id(comment.getId())
                    .authorName("(삭제)")
                    .emailId(null)
                    .authorProfileImagePath(null) // 기본 프로필 이미지 경로 설정
                    .content("삭제된 댓글입니다")
                    .createdAt(null)
                    .isUpdated("false")
                    .isDeleted(true)
                    .isAuthor(false)
                    .replies(filteredReplies)
                    .build();
        }

        // 댓글 작성자 정보
        Profile authorProfile = comment.getProfile();
        Member authorMember = authorProfile.getMember();

        // 회원 상태 확인 (탈퇴한 계정 처리)
        boolean isQuitAccount = authorMember.getMemberState() == MemberState.DELETED;

        String authorName =
                isQuitAccount
                        ? "(탈퇴한 사용자)"
                        : authorProfile.getMember().getMemberBasicInform().getMemberName();

        String emailId = isQuitAccount ? null : authorProfile.getMember().getEmailId();
        String profileImagePath = isQuitAccount ? null : authorProfile.getProfileImagePath();
        String createdAt =
                isQuitAccount ? null : DateUtils.formatRelativeTime(comment.getCreatedAt());

        // 현재 사용자가 댓글 작성자인지 확인
        boolean isAuthor =
                currentMemberId.isPresent()
                        && currentMemberId.get().equals(authorProfile.getMember().getId());

        // 대댓글 목록 조회 및 변환 (필터링 적용)
        List<ReplyResponse> replies =
                profileLogCommentQueryAdapter.findRepliesByParentCommentId(comment.getId()).stream()
                        .map(reply -> toReplyResponse(reply, currentMemberId))
                        .filter(reply -> reply != null) // null이 아닌 대댓글만 포함
                        .collect(Collectors.toList());

        return ParentCommentResponse.builder()
                .id(comment.getId())
                .authorName(authorName)
                .emailId(emailId)
                .authorProfileImagePath(profileImagePath)
                .content(comment.getContent())
                .createdAt(createdAt)
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

        // 삭제된 답글인 경우 null 반환 (표시하지 않음)
        if (reply.isDeleted()) {
            return null;
        }

        // 댓글 작성자 정보
        Profile authorProfile = reply.getProfile();
        Member authorMember = authorProfile.getMember();

        // 회원 상태 확인 (탈퇴한 계정 처리)
        boolean isQuitAccount = authorMember.getMemberState() == MemberState.DELETED;

        String authorName =
                isQuitAccount
                        ? "(탈퇴한 사용자)"
                        : authorProfile.getMember().getMemberBasicInform().getMemberName();

        String emailId = isQuitAccount ? null : authorProfile.getMember().getEmailId();
        String profileImagePath = isQuitAccount ? null : authorProfile.getProfileImagePath();
        String createdAt =
                isQuitAccount ? null : DateUtils.formatRelativeTime(reply.getCreatedAt());

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
                .createdAt(createdAt)
                .isUpdated(reply.getModifiedAt().isAfter(reply.getCreatedAt()) ? "true" : "false")
                .isDeleted(reply.isDeleted())
                .isAuthor(isAuthor)
                .build();
    }

    /**
     * 프로필 로그 댓글 수정 응답 DTO를 생성합니다.
     *
     * @param profileLogComment 수정된 댓글 엔티티
     * @param authorProfile 댓글 작성자 프로필
     * @param profileLogId 프로필 로그 ID
     * @return 수정된 댓글 응답 DTO
     */
    public ProfileLogCommentResponseDTO.UpdateProfileLogCommentResponse
            toUpdateProfileLogCommentResponse(
                    final ProfileLogComment profileLogComment,
                    final Profile authorProfile,
                    final Long profileLogId) {
        return ProfileLogCommentResponseDTO.UpdateProfileLogCommentResponse.builder()
                .commentId(profileLogComment.getId())
                .profileLogId(profileLogId)
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

    public ProfileLogCommentResponseDTO.DeleteProfileLogCommentResponse
            toDeleteProfileLogCommentResponse(
                    final Long profileLogCommentId, final Long profileLogId) {
        return ProfileLogCommentResponseDTO.DeleteProfileLogCommentResponse.builder()
                .commentId(profileLogCommentId)
                .profileLogId(profileLogId)
                .build();
    }
}
