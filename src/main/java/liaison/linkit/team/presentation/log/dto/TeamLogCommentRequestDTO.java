package liaison.linkit.team.presentation.log.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TeamLogCommentRequestDTO {
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddTeamLogCommentRequest {
        @NotBlank(message = "댓글 내용은 필수입니다.")
        @Size(max = 1000, message = "댓글은 최대 1000자까지 작성 가능합니다.")
        private String content;

        private Long parentCommentId; // 대댓글인 경우 부모 댓글 ID (null이면 최상위 댓글)
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateTeamLogCommentRequest {
        @NotBlank(message = "댓글 내용은 필수입니다.")
        @Size(max = 1000, message = "댓글은 최대 1000자까지 작성 가능합니다.")
        private String content;

        private Long parentCommentId; // 대댓글인 경우 부모 댓글 ID (null이면 최상위 댓글)
    }
}
