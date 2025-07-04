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
import liaison.linkit.member.domain.Member;
import liaison.linkit.team.domain.team.Team;
import liaison.linkit.team.domain.teamMember.type.TeamMemberManagingTeamState;
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
public class TeamMember extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @Column(nullable = false)
    @Enumerated(value = STRING)
    private TeamMemberType teamMemberType; // 팀원 권한

    @Column(nullable = false)
    @Enumerated(value = STRING)
    private TeamMemberManagingTeamState
            teamMemberManagingTeamState; // 팀원의 팀 관리 상태 (ACTIVE, DENY_DELETE, ALLOW_DELETE)

    public void setTeamMemberType(final TeamMemberType teamMemberType) {
        this.teamMemberType = teamMemberType;
    }

    public void setTeamMemberManagingTeamState(
            final TeamMemberManagingTeamState teamMemberManagingTeamState) {
        this.teamMemberManagingTeamState = teamMemberManagingTeamState;
    }
}
