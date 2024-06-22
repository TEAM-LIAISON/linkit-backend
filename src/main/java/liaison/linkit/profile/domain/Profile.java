package liaison.linkit.profile.domain;

import jakarta.persistence.*;
import liaison.linkit.member.domain.Member;
import liaison.linkit.member.domain.type.ProfileType;
import liaison.linkit.profile.dto.request.ProfileUpdateRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.CascadeType.REMOVE;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
// 내 이력서
public class Profile {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @OneToOne(cascade = ALL, orphanRemoval = true, fetch = LAZY)
    @JoinColumn(name = "member_id", unique = true)
    private Member member;

    @OneToOne(mappedBy = "profile")
    private MiniProfile miniProfile;

    @OneToMany(mappedBy = "profile", cascade = REMOVE)
    private List<Awards> awardsList = new ArrayList<>();

    // 전체 프로필 완성도 값 (%) - 소수점 가능 (double 자료형)
    @Column(nullable = false)
    private double completion;

    // 자기소개
    @Column(name = "introduction")
    private String introduction;

    // 내 이력서 - 자기소개 항목 입력 여부
    @Column(nullable = false)
    private boolean isIntroduction;

    // 내 이력서 - 보유 기술 항목 기입 여부
    @Column(nullable = false)
    private boolean isProfileSkill;

    // 내 이력서 - 희망 팀빌딩 분야 항목 입력 여부
    @Column(nullable = false)
    private boolean isProfileTeamBuildingField;

    // 내 이력서 - 지역 및 위치 항목 입력 여부
    @Column(nullable = false)
    private boolean isProfileRegion;

    // 내 이력서 - 이력 항목 기입 여부
    @Column(nullable = false)
    private boolean isAntecedents;

    // 내 이력서 - 학력 항목 기입 여부
    @Column(nullable = false)
    private boolean isEducation;

    // 내 이력서 - 수상 항목 기입 여부
    @Column(nullable = false)
    private boolean isAwards;

    // 내 이력서 - 첨부 항목 기입 여부
    @Column(nullable = false)
    private boolean isAttach;

    // 내 이력서 - 첨부 URL 항목 기입 여부
    @Column(nullable = false)
    private boolean isAttachUrl;

    // 내 이력서 - 첨부 File 항목 기입 여부
    @Column(nullable = false)
    private boolean isAttachFile;

    // 내 이력서 - 미니 프로필 항목 존재 여부
    @Column(nullable = false)
    private boolean isMiniProfile;

    // 생성자
    public Profile(
            final Long id,
            final Member member,
            final double completion
    ) {
        this.id = id;
        this.member = member;
        this.completion = completion;
        this.introduction = null;
        this.isIntroduction = false;
        this.isProfileSkill = false;
        this.isProfileTeamBuildingField = false;
        this.isProfileRegion = false;
        this.isAntecedents = false;
        this.isEducation = false;
        this.isAwards = false;
        this.isAttach = false;
        this.isAttachUrl = false;
        this.isAttachFile = false;
        this.isMiniProfile = false;
    }

    public Profile(
            final Member member,
            final int completion
    ) {
        this(null, member, completion);
    }

    // 보유기술, 희망 팀빌딩 분야, 위치 및 지역, 학력 59%
    // 자기소개 20%
    // 이력 7%
    // 수상 7%
    // 첨부 7%

    public void update(final ProfileUpdateRequest updateRequest) {this.introduction = updateRequest.getIntroduction();}
    public void deleteIntroduction() {this.introduction = null;}

    public void addPerfectionDefault() {this.completion += 11.8;}
    public void cancelPerfectionDefault() {this.completion -= 11.8;}

    public void addPerfectionSeven() {this.completion += 7.0;}
    public void cancelPerfectionSeven() {this.completion -= 7.0;}

    public void addPerfectionTwenty() { this.completion += 20.0; }
    public void cancelPerfectionTwenty() {this.completion -= 20.0;}

    // 자기소개 업데이트
    public void updateIntroduction(final String introduction) {
        if (!Objects.equals(introduction, "")) {           // 삭제 요청이 아닌 수정/생성인 경우
            if (this.introduction != null) {  // 기존에 데이터가 있는 경우 (수정)
                this.introduction = introduction;
            } else {                          // 기존에 데이터가 없는 경우 (생성)
                this.introduction = introduction;
                addPerfectionTwenty();
                updateMemberProfileTypeByCompletion();
                updateIsIntroduction(true);
            }
        } else {                              // 삭제 요청인 경우
            this.introduction = null;
            cancelPerfectionTwenty();
            updateMemberProfileTypeByCompletion();
            updateIsIntroduction(false);
        }
    }

    public void updateIsProfileTeamBuildingField(final Boolean isProfileTeamBuildingField) {
        this.isProfileTeamBuildingField = isProfileTeamBuildingField;
        if (isProfileTeamBuildingField) {
            addPerfectionDefault();
        } else {
            cancelPerfectionDefault();
        }
    }

    public void updateIsEducation(final Boolean isEducation) {
        this.isEducation = isEducation;
        if (isEducation) {
            addPerfectionDefault();
        } else {
            cancelPerfectionDefault();
        }
    }

    public void updateIsProfileSkill(final boolean isProfileSkill) {
        this.isProfileSkill = isProfileSkill;
        if (isProfileSkill) {
            addPerfectionDefault();
        } else {
            cancelPerfectionDefault();
        }
    }

    public void updateIsProfileRegion(final boolean isProfileRegion) {
        this.isProfileRegion = isProfileRegion;
        if (isProfileRegion) {
            addPerfectionDefault();
        } else {
            cancelPerfectionDefault();
        }
    }

    // 자기소개 등록 또는 삭제에서만 호출
    public void updateIsIntroduction(final Boolean isIntroduction) {
        this.isIntroduction = isIntroduction;
    }

    public void updateIsAntecedents(final Boolean isAntecedents) {
        this.isAntecedents = isAntecedents;
        if (isAntecedents) {
            addPerfectionDefault();
        } else {
            cancelPerfectionDefault();
        }
    }

    // 수상 항목 등록 또는 삭제에서만 호출
    public void updateIsAwards(final Boolean isAwards) {
        this.isAwards = isAwards;
        if (isAwards) {
            addPerfectionSeven();
        } else {
            cancelPerfectionSeven();
        }
    }

    // 미니프로필 존재 여부 전환
    public void updateIsMiniProfile(final Boolean isMiniProfile) {
        this.isMiniProfile = isMiniProfile;
    }

    // 첨부 항목 등록 또는 삭제에서만 호출
    public void updateIsAttachUrl(final Boolean isAttachUrl) {
        this.isAttachUrl = isAttachUrl;
    }

    public void updateIsAttachFile(final Boolean isAttachFile) {
        this.isAttachFile = isAttachFile;
    }

    public void updateMemberProfileTypeByCompletion() {
        final double presentCompletion = this.getCompletion();
        final ProfileType profileType = this.getMember().getProfileType();

        if (presentCompletion >= 0 && presentCompletion < 50) {
            if (ProfileType.NO_PERMISSION.equals(profileType)) {
                return;
            } else {
                // 해당 상태를 변경해줘야함.
                this.getMember().setProfileType(ProfileType.NO_PERMISSION);
            }
        } else if (presentCompletion >= 50 && presentCompletion < 80) {
            if (ProfileType.ALLOW_BROWSE.equals(profileType)) {
                return;
            } else {
                this.getMember().setProfileType(ProfileType.ALLOW_BROWSE);
            }
        } else {
            if (ProfileType.ALLOW_MATCHING.equals(profileType)) {
                return;
            } else {
                this.getMember().setProfileType(ProfileType.ALLOW_MATCHING);
            }
        }
    }

    // Default 항목에 대한 검사 진행
    private void checkAndUpdateByDefault() {
//        if (this.isProfileSkill && this.isProfileTeamBuildingField && this.isEducation) {
//            // 전부 참인 경우
//            this.completion = 59;
//            this.getMember().openAndClosePermission(true);
//        } else {
//            this.completion = 0;
//            this.getMember().openAndClosePermission(false);
//        }
    }

    public boolean getIsIntroduction() {
        return isIntroduction;
    }

    public boolean getIsProfileSkill() {
        return isProfileSkill;
    }

    public boolean getIsProfileTeamBuildingField() {
        return isProfileTeamBuildingField;
    }

    public boolean getIsAntecedents() {
        return isAntecedents;
    }

    public boolean getIsEducation() {
        return isEducation;
    }

    public boolean getIsAwards() {
        return isAwards;
    }

    public boolean getIsAttach() {
        return isAttach;
    }

    public boolean getIsMiniProfile() { return isMiniProfile; }

    public boolean getIsProfileRegion() { return isProfileRegion; }

    public boolean getIsAttachUrl() {
        return isAttachUrl;
    }

    public boolean getIsAttachFile() {
        return isAttachFile;
    }



}
