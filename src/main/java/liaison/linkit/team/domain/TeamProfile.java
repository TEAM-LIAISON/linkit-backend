package liaison.linkit.team.domain;

import jakarta.persistence.*;
import liaison.linkit.member.domain.Member;
import liaison.linkit.member.domain.type.TeamProfileType;
import liaison.linkit.team.domain.miniprofile.TeamMiniProfile;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

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

    // 팀 프로필 완성도
    @Column(name = "team_profile_completion")
    private double teamProfileCompletion;

    @Column(name = "team_introduction")
    private String teamIntroduction;

    // 팀 프로필 희망 팀빌딩 분야
    @Column(nullable = false)
    private boolean isTeamProfileTeamBuildingField;

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

    // 팀원 소개 기입 여부
    @Column(nullable = false)
    private boolean isTeamMemberIntroduction;

    // 연혁 기입 여부
    @Column(nullable = false)
    private boolean isHistory;

    // 팀 첨부 기입 여부
    @Column(nullable = false)
    private boolean isTeamAttach;

    // 팀 첨부 URL 기입 여부
    @Column(nullable = false)
    private boolean isTeamAttachUrl;

    // 팀 첨부 파일 기입 여부
    private boolean isTeamAttachFile;

    // 팀 미니 프로필 기입 여부
    @Column(nullable = false)
    private boolean isTeamMiniProfile;

    public TeamProfile(
            final Long id,
            final Member member,
            final int teamProfileCompletion
    ) {
        this.id = id;
        this.member = member;
        this.teamProfileCompletion = teamProfileCompletion;
        this.teamIntroduction = null;
        this.isTeamProfileTeamBuildingField = false;
        this.isTeamMemberAnnouncement = false;
        this.isActivity = false;
        this.isActivityMethod = false;
        this.isActivityRegion = false;
        this.isTeamIntroduction = false;
        this.isTeamMemberIntroduction = false;
        this.isHistory = false;
        this.isTeamAttach = false;
        this.isTeamAttachUrl = false;
        this.isTeamAttachFile = false;
        this.isTeamMiniProfile = false;
    }

    public TeamProfile(
            final Member member,
            final int teamProfileCompletion
    ) {
        this(null, member, teamProfileCompletion);
    }


    public void updateIsTeamProfileTeamBuildingField(final Boolean isTeamTeamBuildingField) {
        this.isTeamProfileTeamBuildingField = isTeamTeamBuildingField;
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

    public void updateIsTeamMiniProfile(final Boolean isTeamMiniProfile) {
        this.isTeamMiniProfile = isTeamMiniProfile;
    }

    public boolean getIsTeamProfileTeamBuildingField() {
        return isTeamProfileTeamBuildingField;
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
    public boolean getIsTeamMiniProfile() {return isTeamMiniProfile;}

    // 팀 소개 업데이트 메서드
    public void updateTeamIntroduction(final String teamIntroduction) {
        if (!Objects.equals(teamIntroduction, "")) {        // 하나라도 텍스트가 들어오는 경우
            if (this.teamIntroduction != null) {
                this.teamIntroduction = teamIntroduction;
            } else {
                this.teamIntroduction = teamIntroduction;
                addPerfectionEleven();
                updateMemberTeamProfileTypeByCompletion();
                updateIsTeamIntroduction(true);
            }
        } else {                                                // 삭제 요청으로 간주
            this.teamIntroduction = null;
            cancelPerfectionEleven();
            updateMemberTeamProfileTypeByCompletion();
            updateIsTeamIntroduction(false);
        }
    }

    // 프로필 권한 관리 메서드
    public void updateMemberTeamProfileTypeByCompletion() {
        final double presentTeamProfileCompletion = this.getTeamProfileCompletion();
        final TeamProfileType teamProfileType = this.getMember().getTeamProfileType();

        if (presentTeamProfileCompletion >= 0 && presentTeamProfileCompletion < 50) {
            if (TeamProfileType.NO_PERMISSION.equals(teamProfileType)) {
                return;
            } else {
                this.getMember().setTeamProfileType(TeamProfileType.NO_PERMISSION);
            }
        } else if (presentTeamProfileCompletion >= 50 && presentTeamProfileCompletion < 80) {
            if (TeamProfileType.ALLOW_BROWSE.equals(teamProfileType)) {
                return;
            } else {
                this.getMember().setTeamProfileType(TeamProfileType.ALLOW_BROWSE);
            }
        } else {
            if (TeamProfileType.ALLOW_PROFILE_MATCHING.equals(teamProfileType)) {
                return;
            } else {
                this.getMember().setTeamProfileType(TeamProfileType.ALLOW_PROFILE_MATCHING);
            }
        }
    }

    // 팀 소개 기입 여부
    public void updateIsTeamIntroduction(final Boolean isTeamIntroduction) {
        this.isTeamIntroduction = isTeamIntroduction;
    }

    // 팀원 소개 기입 여부
    public void updateIsTeamMemberIntroduction(final Boolean isTeamMemberIntroduction) {
        this.isTeamMemberIntroduction = isTeamMemberIntroduction;
    }

    public void updateIsHistory(final Boolean isHistory) {
        this.isHistory = isHistory;
    }

    public void updateIsTeamAttachUrl(final Boolean isTeamAttachUrl) {
        this.isTeamAttachUrl = isTeamAttachUrl;
    }

    public void updateIsTeamAttachFile(final Boolean isTeamAttachFile) {
        this.isTeamAttachFile = isTeamAttachFile;
    }


    private void addPerfectionEleven() {this.teamProfileCompletion += 11.0;}
    private void cancelPerfectionEleven() {this.teamProfileCompletion -= 11.0;}



}
