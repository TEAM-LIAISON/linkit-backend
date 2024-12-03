package liaison.linkit.team.domain.log;

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
import liaison.linkit.profile.domain.type.LogType;
import liaison.linkit.team.domain.Team;
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
public class TeamLog extends BaseDateTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @Column(nullable = false)
    private String logTitle;

    @Column(nullable = false)
    private String logContent;

    private boolean isLogPublic;

    @Column(nullable = false)
    @Enumerated(value = STRING)
    private LogType logType;

    public void setLogType(final LogType logType) {
        this.logType = logType;
    }

}
