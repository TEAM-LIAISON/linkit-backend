package liaison.linkit.visit.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

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
@Table(name = "profile_visits")
public class ProfileVisit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 방문한 사용자의 프로필 (더 명확한 이름으로 변경)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    private Profile profile;

    // 방문받은 프로필 ID
    @Column(name = "visited_profile_id", nullable = false)
    private Long visitedProfileId;

    // 방문 시간
    @Column(name = "visit_time", nullable = false)
    private LocalDateTime visitTime;

    /**
     * 방문 시간을 현재 시각으로 업데이트합니다. profileDetail 조회 시 호출되어 최신 방문 시각을 기록합니다.
     *
     * @return 업데이트된 ProfileVisit 객체
     */
    public ProfileVisit updateVisitTime() {
        this.visitTime = LocalDateTime.now();
        return this;
    }
}
