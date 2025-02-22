package liaison.linkit.profile.domain.log;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import liaison.linkit.common.domain.BaseDateTimeEntity;
import liaison.linkit.profile.domain.profile.Profile;
import liaison.linkit.profile.domain.type.LogType;
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
public class ProfileLog extends BaseDateTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "profile_id")
    private Profile profile;

    @Column(nullable = false, length = 100) // 제목 길이 제한 추가 (100자)
    private String logTitle;

    @Column(length = 5000) // 본문 길이 제한 추가 (5,000자)
    private String logContent;

    private boolean isLogPublic;

    @Column(nullable = false)
    @Enumerated(value = STRING)
    private LogType logType;

    // == 1) 조회수 필드 추가 ==
    // 기본값 0
    @Column(nullable = false)
    private Long viewCount;

    public void setLogType(final LogType logType) {
        this.logType = logType;
    }

    public void setIsLogPublic(final boolean isLogPublic) {
        this.isLogPublic = isLogPublic;
    }

    // == 조회수 증가 메서드 ==
    public void increaseViewCount() {
        this.viewCount++;
    }

    // == 2) 배치에서 주기적으로 호출할 메서드 (조회수 초기화) ==
    public void resetViewCount() {
        this.viewCount = 0L;
    }
}
