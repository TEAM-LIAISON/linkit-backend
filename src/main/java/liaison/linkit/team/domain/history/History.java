package liaison.linkit.team.domain.history;

import jakarta.persistence.*;
import liaison.linkit.team.dto.request.HistoryCreateRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
public class History {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "history_id")
    private Long id;

    // 일대다 관계
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "team_profile_id")
    private TeamProfile teamProfile;

    @Column(name = "history_one_line_introduction")
    private String historyOneLineIntroduction;

    @Column(name = "start_year")
    private int startYear;

    @Column(name = "end_year")
    private int endYear;

    @Column(name = "in_progress")
    private boolean inProgress;

    @Column(name = "history_introduction")
    private String historyIntroduction;

    public static History of(
            final TeamProfile teamProfile,
            final String historyOneLineIntroduction,
            final int startYear,
            final int endYear,
            final boolean inProgress,
            final String historyIntroduction
    ) {
        return new History(
                null,
                teamProfile,
                historyOneLineIntroduction,
                startYear,
                endYear,
                inProgress,
                historyIntroduction
        );
    }

    public void update(final HistoryCreateRequest historyCreateRequest) {
        this.historyOneLineIntroduction = historyCreateRequest.getHistoryOneLineIntroduction();
        this.startYear = historyCreateRequest.getStartYear();
        this.endYear = historyCreateRequest.getEndYear();
        this.historyIntroduction = historyCreateRequest.getHistoryIntroduction();
        this.inProgress = historyCreateRequest.isInProgress();
    }
}
