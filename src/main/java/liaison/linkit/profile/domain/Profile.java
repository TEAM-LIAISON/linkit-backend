package liaison.linkit.profile.domain;

import jakarta.persistence.*;
import liaison.linkit.member.domain.Member;
import liaison.linkit.member.domain.type.ProfileType;
import liaison.linkit.profile.dto.request.ProfileUpdateRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

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
            final double completion,
            final String introduction
    ) {
        this.id = id;
        this.member = member;
        this.completion = completion;
        this.introduction = introduction;
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
            final int completion,
            final String introduction
    ) {
        this(null, member, completion, introduction);
    }

    // 보유기술, 희망 팀빌딩 분야, 위치 및 지역, 학력 59%
    // 자기소개 20%
    // 이력 7%
    // 수상 7%
    // 첨부 7%

    public void update(final ProfileUpdateRequest updateRequest) {this.introduction = updateRequest.getIntroduction();}
    public void deleteIntroduction() {this.introduction = null;}
    public void addPerfectionSeven() {this.completion += 7;}
    public void cancelPerfectionSeven() {this.completion -= 7;}
    public void addPerfectionTwenty() { this.completion += 20; }
    public void cancelPerfectionTwenty() {this.completion -= 20;}

    // Default 항목 3개 관리 (isSkill, isProfileTeamBuildingField, isEducation)

    public void updateIsProfileTeamBuildingField(final Boolean isProfileTeamBuildingField) {
        this.isProfileTeamBuildingField = isProfileTeamBuildingField;
        checkAndUpdateByDefault();
    }

    public void updateIsEducation(final Boolean isEducation) {
        this.isEducation = isEducation;
        checkAndUpdateByDefault();
    }

    public void updateIsProfileSkill(final boolean isProfileSkill) {
        this.isProfileSkill = isProfileSkill;
        checkAndUpdateByDefault();
    }

    public void updateIsProfileRegion(final boolean isProfileRegion) {
        this.isProfileRegion = isProfileRegion;
        // 프로그레스 상태 관리 로직 추가할 것 (6/1)
    }

    // 자기소개 등록 또는 삭제에서만 호출
    public void updateIsIntroduction(final Boolean isIntroduction) {
        this.isIntroduction = isIntroduction;
        if (isIntroduction) {
            addPerfectionTwenty();
        } else {
            cancelPerfectionTwenty();
        }
    }

    public void updateIsAntecedents(final Boolean isAntecedents) {
        this.isAntecedents = isAntecedents;
        if (isAntecedents) {
            addPerfectionSeven();
        } else {
            cancelPerfectionSeven();
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

//        if (presentCompletion >= 0 && presentCompletion < 50) {
//            if (ProfileType.NO_PERMISSION.equals(profileType)) {
//                return;
//            } else {
//                // 해당 상태를 변경해줘야함.
//                this.getMember().
//            }
//        }

//
//        // 완성도 값을 호출한다.
//        final int presentCompletion = this.getCompletion();
//
//        // 사용자의 권한 상태를 호출한다.
//        final MemberProfileType memberProfileType = this.getMember().getMemberProfileType();
//
//        if (presentCompletion >= 0 && presentCompletion < 50) {
//            if (MemberProfileType.NO_PERMISSION.equals(memberProfileType)) {
//                // 아무 조치를 하지 않는다.
//                return;
//            } else {
//                // 같지 않으면 기존에 프로필 열람 및 매칭 요청 권한이 부여된 상태이다.
//                // false 전달하여 NO_PERMISSION 상태로 변경을 진행한다.
//                this.getMember().openAndClosePermission(false);
//            }
//        } else if (presentCompletion >= 50 && presentCompletion < 80) {
//            if (MemberProfileType.PROFILE_OPEN_PERMISSION.equals(memberProfileType)) {
//                return;
//            } else {
//                // true 전달하여 PROFILE_OPEN_PERMISSION 상태로 변경한다.
//                this.getMember().openAndClosePermission(true);
//            }
//        } else {
//            if (MemberProfileType.MATCHING_PERMISSION.equals(memberProfileType)) {
//                return;
//            } else {
//                this.getMember().changeAndOpenPermission(true);
//            }
//        }
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
