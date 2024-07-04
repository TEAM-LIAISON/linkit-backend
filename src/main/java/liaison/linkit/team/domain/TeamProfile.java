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
import static liaison.linkit.member.domain.type.TeamProfileType.ALLOW_MATCHING;
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

    // 4.3. 팀 프로필 완성도
    @Column(name = "team_profile_completion")
    private double teamProfileCompletion;

    // 4.7. 팀 소개
    @Column(name = "team_introduction")
    private String teamIntroduction;

    // 4.4. 희망 팀빌딩 분야 기입 여부
    @Column(nullable = false)
    private boolean isTeamProfileTeamBuildingField;

    // 4.5. 팀원 공고 기입 여부
    @Column(nullable = false)
    private boolean isTeamMemberAnnouncement;

    // 4.6. 활동 방식 + 활동 지역 및 위치 기입 여부
    @Column(nullable = false)
    private boolean isActivity;

    // 4.6.1. 활동 방식 기입 여부
    @Column(nullable = false)
    private boolean isActivityMethod;

    // 4.6.2. 활동 지역 기입 여부
    @Column(nullable = false)
    private boolean isActivityRegion;

    // 4.7. 팀 소개 기입 여부
    @Column(nullable = false)
    private boolean isTeamIntroduction;

    // 4.8. 팀원 소개 기입 여부
    @Column(nullable = false)
    private boolean isTeamMemberIntroduction;

    // 4.9. 연혁 기입 여부
    @Column(nullable = false)
    private boolean isHistory;

    // 4.10. 팀 첨부 기입 여부
    @Column(nullable = false)
    private boolean isTeamAttach;

    // 4.10.1 팀 첨부 링크(URL) 기입 여부
    @Column(nullable = false)
    private boolean isTeamAttachUrl;

    // 4.10.2. 팀 첨부 파일(File) 기입 여부
    @Column(nullable = false)
    private boolean isTeamAttachFile;

    // 4.1. 팀 미니 프로필 기입 여부
    @Column(nullable = false)
    private boolean isTeamMiniProfile;

    // 생성자
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

    // 기본 입력 항목 (희망 팀빌딩 분야 / 활동 방식 + 활동 지역 및 위치)
    // 팀원 공고 15.0%
    // 팀 소개 15.0%
    // 팀원 소개 15.0%
    // 연혁 2.5%
    // 첨부 2.5%

    // 디폴트 항목 관리 메서드
    public void addTeamPerfectionDefault() {this.teamProfileCompletion += 25.0;}
    public void cancelTeamPerfectionDefault() {this.teamProfileCompletion -= 25.0;}

    public void addTeamPerfectionFifteen() {this.teamProfileCompletion += 15.0;}
    public void cancelTeamPerfectionFifteen() {this.teamProfileCompletion -= 15.0;}

    public void addTeamPerfectionTwoPointFive() {this.teamProfileCompletion += 2.5;}
    public void cancelTeamPerfectionTwoPointFive() {this.teamProfileCompletion -= 2.5;}

    // 4.1. 미니 프로필 업데이트
    public void updateIsTeamMiniProfile(final boolean isTeamMiniProfile) {
        this.isTeamMiniProfile = isTeamMiniProfile;
    }


    // 4.4. 희망 팀빌딩 분야 업데이트
    public void updateIsTeamProfileTeamBuildingField(final boolean isTeamTeamBuildingField) {
        this.isTeamProfileTeamBuildingField = isTeamTeamBuildingField;
        if (isTeamTeamBuildingField) {
            addTeamPerfectionDefault();
        } else {
            cancelTeamPerfectionDefault();
        }
    }

    // 4.5. 팀원 공고 업데이트
    public void updateIsTeamMemberAnnouncement(final boolean isTeamMemberAnnouncement) {
        this.isTeamMemberAnnouncement = isTeamMemberAnnouncement;
        if (isTeamMemberAnnouncement) {
            addTeamPerfectionFifteen();
        } else {
            cancelTeamPerfectionFifteen();
        }
    }

    // 4.6. 활동 방식 + 활동 지역 및 위치 업데이트
    public void updateIsActivity(final boolean isActivityMethod, final boolean isActivityRegion) {
        if (this.isActivity != (isActivityMethod && isActivityRegion)) {
            this.isActivity = !this.isActivity;
            if (this.isActivity) {
                addTeamPerfectionDefault();
            } else {
                cancelTeamPerfectionDefault();
            }
        }
    }

    // 4.6.1 활동 방식 업데이트
    public void updateIsActivityMethod(final boolean isActivityMethod) {
        this.isActivityMethod = isActivityMethod;
        updateIsActivity(isActivityMethod, this.isActivityRegion);
    }

    // 4.6.2. 활동 지역 및 위치 업데이트
    public void updateIsActivityRegion(final boolean isActivityRegion) {
        this.isActivityRegion = isActivityRegion;
        updateIsActivity(this.isActivityMethod, isActivityRegion);
    }

    // 4.7. 팀 소개 업데이트
    public void updateIsTeamIntroduction(final boolean isTeamIntroduction) {
        this.isTeamIntroduction = isTeamIntroduction;
    }

    // 4.8. 팀원 소개 업데이트
    public void updateIsTeamMemberIntroduction(final boolean isTeamMemberIntroduction) {
        this.isTeamMemberIntroduction = isTeamMemberIntroduction;
    }

    // 4.9. 연혁 업데이트
    public void updateIsHistory(final boolean isHistory) {
        this.isHistory = isHistory;
    }

    // 4.10. 팀 첨부 업데이트
    private void updateIsTeamAttach(final boolean isTeamAttachUrl, final boolean isTeamAttachFile) {
        if (this.isTeamAttach != (isTeamAttachUrl || isTeamAttachFile)) {
            this.isTeamAttach = !this.isTeamAttach;
            if (this.isTeamAttach) {
                addTeamPerfectionTwoPointFive();
            } else {
                cancelTeamPerfectionTwoPointFive();
            }
        }
    }

    // 4.10.1. 팀 첨부 링크 업데이트
    public void updateIsTeamAttachUrl(final boolean isTeamAttachUrl) {
        this.isTeamAttachUrl = isTeamAttachUrl;
        updateIsTeamAttach(isTeamAttachUrl, this.isTeamAttachFile);
    }

    // 4.10.2. 팀 첨부 파일 업데이트
    public void updateIsTeamAttachFile(final boolean isTeamAttachFile) {
        this.isTeamAttachFile = isTeamAttachFile;
        updateIsTeamAttach(this.isTeamAttachUrl, isTeamAttachFile);
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
    public boolean getIsTeamAttach() {return isTeamAttach;}
    public boolean getIsTeamMiniProfile() {return isTeamMiniProfile;}


    // 4.7. 팀 소개 텍스트 내용 업데이트
    public void updateTeamIntroduction(final String teamIntroduction) {
        if (!Objects.equals(teamIntroduction, "")) {        // 하나라도 텍스트가 들어오는 경우
            if (this.teamIntroduction != null) {
                this.teamIntroduction = teamIntroduction;
            } else {
                this.teamIntroduction = teamIntroduction;
                updateIsTeamIntroduction(true);
                addTeamPerfectionFifteen();
                updateMemberTeamProfileTypeByCompletion();
            }
        } else {                                                // 삭제 요청으로 간주
            this.teamIntroduction = null;
            updateIsTeamIntroduction(false);
            cancelTeamPerfectionFifteen();
            updateMemberTeamProfileTypeByCompletion();

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
            if (ALLOW_MATCHING.equals(teamProfileType)) {
                return;
            } else {
                this.getMember().setTeamProfileType(ALLOW_MATCHING);
            }
        }
    }


    public boolean getExistDefaultTeamProfile() {
        if (this.isTeamProfileTeamBuildingField && this.isActivity) {
            return true;
        } else return false;
    }
}
