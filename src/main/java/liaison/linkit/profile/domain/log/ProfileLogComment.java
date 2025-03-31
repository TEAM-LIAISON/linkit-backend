package liaison.linkit.profile.domain.log;

import static jakarta.persistence.FetchType.LAZY;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import liaison.linkit.common.domain.BaseDateTimeEntity;
import liaison.linkit.profile.domain.profile.Profile;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ProfileLogComment extends BaseDateTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "profile_log_id", nullable = false)
    private ProfileLog profileLog;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    private Profile profile;

    @Column(nullable = false, length = 1000) // 댓글 내용 길이 제한 (1,000자)
    private String content;

    @Column(nullable = false)
    private boolean isDeleted;

    // 대댓글 구현을 위한 필드
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "parent_comment_id")
    private ProfileLogComment parentComment;

    // 댓글 내용 수정 메서드
    public void updateContent(final String content) {
        this.content = content;
    }

    // 댓글 삭제 처리 메서드 (soft delete)
    public void delete() {
        this.isDeleted = true;
    }
}
