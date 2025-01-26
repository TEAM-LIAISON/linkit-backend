package liaison.linkit.team.domain.teamMember;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import liaison.linkit.global.BaseEntity;
import liaison.linkit.team.domain.team.Team;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SQLRestriction("status = 'USABLE'")
public class TeamMemberInvitation extends BaseEntity {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String teamMemberInvitationEmail;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @Column(nullable = false)
    @Enumerated(value = STRING)
    private TeamMemberType teamMemberType;

    // 초대 상태
    @Column(nullable = false)
    @Enumerated(value = STRING)
    private TeamMemberInviteState teamMemberInviteState;

    public void setTeamMemberInviteState(final TeamMemberInviteState teamMemberInviteState) {
        this.teamMemberInviteState = teamMemberInviteState;
    }
}
