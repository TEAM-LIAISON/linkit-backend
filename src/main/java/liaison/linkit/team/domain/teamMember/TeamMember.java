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
import liaison.linkit.member.domain.Member;
import liaison.linkit.team.domain.team.Team;
import liaison.linkit.team.domain.teamMember.type.TeamMemberManagingTeamState;
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
public class TeamMember {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @Column(nullable = false)
    @Enumerated(value = STRING)
    private TeamMemberType teamMemberType;  // 팀원 권한

    @Column(nullable = false)
    @Enumerated(value = STRING)
    private TeamMemberManagingTeamState teamMemberManagingTeamState;    // 팀원의 팀 관리 상태 (ACTIVE, DENY_DELETE, ALLOW_DELETE)

    public void updateTeamMemberType(final TeamMemberType teamMemberType) {
        this.teamMemberType = teamMemberType;
    }
}
