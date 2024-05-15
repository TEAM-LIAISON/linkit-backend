package liaison.linkit.team.domain;

import jakarta.persistence.*;
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
    private int historyIntroduction;

    @Column(name = "in_progress")
    private boolean inProgress;
}
