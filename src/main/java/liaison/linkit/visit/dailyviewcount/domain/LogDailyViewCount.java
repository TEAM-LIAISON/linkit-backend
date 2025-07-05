package liaison.linkit.visit.dailyviewcount.domain;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"log_view_type", "log_id", "date"})})
public class LogDailyViewCount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "log_view_type",
            nullable = false,
            columnDefinition = "ENUM('PROFILE_LOG','TEAM_LOG')")
    private LogViewType logViewType;

    @Column(name = "log_id", nullable = false)
    private Long logId;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "daily_view_count", nullable = false)
    private Long dailyViewCount;

    public void increase() {
        this.dailyViewCount++;
    }
}
