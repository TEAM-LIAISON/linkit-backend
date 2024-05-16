package liaison.linkit.team.domain;

import jakarta.persistence.*;
import liaison.linkit.member.domain.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class TeamProfile {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @OneToOne(cascade = ALL, orphanRemoval = true, fetch = LAZY)
    @JoinColumn(name = "member_id", unique = true)
    private Member member;

    @Column(name = "team_profile_completion")
    private int teamProfileCompletion;

    @Column(name = "team_introduction")
    private String teamIntroduction;

    @Column(nullable = false)
    private boolean isTeamTeamBuildingField;

    @Column(nullable = false)
    private boolean isTeamMemberAnnouncement;

    @Column(nullable = false)
    private boolean isActivityMethod;

    @Column(nullable = false)
    private boolean isTeamIntroduction;

    @Column(nullable = false)
    private boolean isTeamMemberIntroduction;

    @Column(nullable = false)
    private boolean isHistory;

}
