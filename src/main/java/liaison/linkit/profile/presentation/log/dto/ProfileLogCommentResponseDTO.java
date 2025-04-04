package liaison.linkit.profile.presentation.log.dto;

import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProfileLogCommentResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ParentCommentResponse {
        private Long id;
        private String authorProfileImagePath;
        private String authorName;
        private String emailId;
        private String createdAt;
        private String content;
        private Boolean isUpdated;
        private Boolean isDeleted;
        private Boolean isQuitAccount;
        private Boolean isAuthor;
        private List<ReplyResponse> replies;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReplyResponse {
        private Long id;
        private String authorProfileImagePath;
        private String authorName;
        private String emailId;
        private String createdAt;
        private String content;
        private Boolean isUpdated;
        private Boolean isDeleted;
        private Boolean isQuitAccount;
        private Boolean isAuthor;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PageResponse {
        private List<ParentCommentResponse> comments;
        private long totalElements;
        private int totalPages;
        private int currentPage;
        private boolean hasNext;
    }

    /** 댓글 작성 응답 DTO 부모 댓글과 대댓글 모두에 사용되는 공통 응답 구조 */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AddProfileLogCommentResponse {
        private Long commentId; // 생성된 댓글의 ID
        private Long profileLogId; // 댓글이 작성된 프로필 로그 ID
        private String authorName; // 댓글 작성자 이름
        private String authorProfileImagePath; // 댓글 작성자 프로필 이미지 경로
        private String emailId; // 댓글 작성자 이메일 ID
        private String content; // 댓글 내용
        private String createdAt; // 댓글 작성 시간
        private Boolean isParentComment; // 부모 댓글 여부
        private Long parentCommentId; // 대댓글인 경우 부모 댓글 ID (null이면 부모 댓글)
    }

    /** 댓글 작성 응답 DTO 부모 댓글과 대댓글 모두에 사용되는 공통 응답 구조 */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UpdateProfileLogCommentResponse {
        private Long commentId; // 수정된 댓글의 ID
        private Long profileLogId; // 댓글이 작성된 프로필 로그 ID
        private String authorName; // 댓글 작성자 이름
        private String authorProfileImagePath; // 댓글 작성자 프로필 이미지 경로
        private String emailId; // 댓글 작성자 이메일 ID
        private String content; // 댓글 내용
        private String createdAt; // 댓글 작성 시간
        private Boolean isParentComment; // 부모 댓글 여부
        private Long parentCommentId; // 대댓글인 경우 부모 댓글 ID (null이면 부모 댓글)
    }

    /** 댓글 삭제 응답 DTO */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DeleteProfileLogCommentResponse {
        private Long commentId; // 삭제된 댓글의 ID
        private Long profileLogId; // 댓글이 작성된 프로필 로그 ID
    }
}
