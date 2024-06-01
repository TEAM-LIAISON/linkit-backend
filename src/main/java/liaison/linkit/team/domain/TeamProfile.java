package liaison.linkit.team.domain;

import jakarta.persistence.*;
import liaison.linkit.member.domain.Member;
import liaison.linkit.team.domain.miniprofile.TeamMiniProfile;
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

    @OneToOne(mappedBy = "teamProfile")
    private TeamMiniProfile teamMiniProfile;

    @OneToOne(mappedBy = "teamProfile")
    private TeamIntroduction teamIntroduction;

    // 팀 프로필 완성도
    @Column(name = "team_profile_completion")
    private int teamProfileCompletion;

    // 팀 프로필 희망 팀빌딩 분야
    @Column(nullable = false)
    private boolean isTeamTeamBuildingField;

    // 팀원 모집 공고
    @Column(nullable = false)
    private boolean isTeamMemberAnnouncement;

    // ActivityMethod, ActivityRegion 2개 관리 boolean
    @Column(nullable = false)
    private boolean isActivity;

    // 활동 방식
    @Column(nullable = false)
    private boolean isActivityMethod;

    // 활동 지역
    @Column(nullable = false)
    private boolean isActivityRegion;

    // 팀 소개
    @Column(nullable = false)
    private boolean isTeamIntroduction;

    @Column(nullable = false)
    private boolean isTeamMemberIntroduction;

    //
    @Column(nullable = false)
    private boolean isHistory;

    // 팀 미니 프로필 기입 여부
    @Column(nullable = false)
    private boolean isTeamMiniProfile;

    public void updateIsTeamProfileTeamBuildingField(final Boolean isTeamTeamBuildingField) {
        this.isTeamTeamBuildingField = isTeamTeamBuildingField;
        // 프로그레스 처리 로직 추가 필요
    }

    public void updateIsActivityMethod(final Boolean isActivityMethod) {
        this.isActivityMethod = isActivityMethod;
        if (this.isActivityMethod && this.isActivityRegion) {
            this.isActivity = true;
        }
        // 프로그레스 처리 로직 추가 필요
    }

    public void updateIsActivityRegion(final Boolean isActivityRegion) {
        this.isActivityRegion = isActivityRegion;
        // 2개 모두 참인 경우에 isActivity 참으로 변경
        if (this.isActivityMethod && this.isActivityRegion) {
            this.isActivity = true;
        }
    }

    public boolean getIsTeamTeamBuildingField() {
        return isTeamTeamBuildingField;
    }

    public boolean getIsTeamMemberAnnouncement() {
        return isTeamMemberAnnouncement;
    }
    public boolean getIsActivity() {
        return isActivity;
    }
    public boolean getIsActivityMethod() {
        return isActivityMethod;
    }
    public boolean getIsActivityRegion() {
        return isActivityRegion;
    }
    public boolean getIsTeamIntroduction() {
        return isTeamIntroduction;
    }
    public boolean getIsTeamMemberIntroduction() {
        return isTeamMemberIntroduction;
    }
    public boolean getIsHistory() {
        return isHistory;
    }
    public boolean getIsTeamMiniProfile() {
        return isTeamMiniProfile;
    }

}
