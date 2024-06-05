package liaison.linkit.team.domain;

import jakarta.persistence.*;
import liaison.linkit.member.domain.Role;
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
public class TeamMemberIntroduction {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "team_member_introduction_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "team_profile_id")
    private TeamProfile teamProfile;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_role")
    private Role role;

    @Column(name = "member_name")
    private String memberName;
}
