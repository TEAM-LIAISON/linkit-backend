package liaison.linkit.team.domain;

import jakarta.persistence.*;
import liaison.linkit.team.dto.request.HistoryUpdateRequest;
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

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "team_profile_id")
    private TeamProfile teamProfile;

    @Column(name = "history_one_line_introduction")
    private String historyOneLineIntroduction;

    @Column(name = "start_year")
    private int startYear;

    @Column(name = "start_month")
    private int startMonth;

    @Column(name = "end_year")
    private int endYear;

    @Column(name = "end_month")
    private int endMonth;

    @Column(name = "history_introduction")
    private String historyIntroduction;

    @Column(name = "in_progress")
    private boolean inProgress;

    public static History of(
            final TeamProfile teamProfile,
            final String historyOneLineIntroduction,
            final int startYear,
            final int startMonth,
            final int endYear,
            final int endMonth,
            final String historyIntroduction,
            final boolean inProgress
    ) {
        return new History(
                null,
                teamProfile,
                historyOneLineIntroduction,
                startYear,
                startMonth,
                endYear,
                endMonth,
                historyIntroduction,
                inProgress
        );
    }

    public void update(final HistoryUpdateRequest historyUpdateRequest) {
        this.historyOneLineIntroduction = historyUpdateRequest.getHistoryOneLineIntroduction();
        this.startYear = historyUpdateRequest.getStartYear();
        this.startMonth = historyUpdateRequest.getStartMonth();
        this.endYear = historyUpdateRequest.getEndYear();
        this.endMonth = historyUpdateRequest.getEndMonth();
        this.historyIntroduction = historyUpdateRequest.getHistoryIntroduction();
        this.inProgress = historyUpdateRequest.isInProgress();
    }
}
