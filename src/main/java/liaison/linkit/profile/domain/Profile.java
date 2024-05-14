package liaison.linkit.profile.domain;

import jakarta.persistence.*;
import liaison.linkit.member.domain.Member;
import liaison.linkit.member.domain.type.MemberProfileType;
import liaison.linkit.profile.dto.request.ProfileUpdateRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Profile {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @OneToOne(cascade = ALL, orphanRemoval = true, fetch = LAZY)
    @JoinColumn(name = "member_id", unique = true)
    private Member member;

    // 전체 프로필 완성도 값 (%) 직결
    @Column(nullable = false)
    private int completion;

    @Column(name = "introduction")
    private String introduction;

    // 기입 완료 시 참으로 변환
    @Column(nullable = false)
    private boolean isIntroduction;

    @Column(nullable = false)
    private boolean isProfileSkill;

    @Column(nullable = false)
    private boolean isProfileTeamBuildingField;

    @Column(nullable = false)
    private boolean isAntecedents;

    @Column(nullable = false)
    private boolean isEducation;

    @Column(nullable = false)
    private boolean isAwards;

    // Attach 2개를 한번에 관리하는 컬럼이 있어야 프론트에 던져줄 수 있음.
    @Column(nullable = false)
    private boolean isAttach;

    @Column(nullable = false)
    private boolean isAttachUrl;

    @Column(nullable = false)
    private boolean isAttachFile;

    // 생성자
    public Profile(
            final Long id,
            final Member member,
            final int completion,
            final String introduction
    ) {
        this.id = id;
        this.member = member;
        this.completion = completion;
        this.introduction = introduction;
        this.isIntroduction = false;
        this.isProfileSkill = false;
        this.isProfileTeamBuildingField = false;
        this.isAntecedents = false;
        this.isEducation = false;
        this.isAwards = false;
        this.isAttach = false;
        this.isAttachUrl = false;
        this.isAttachFile = false;
    }

    public Profile(
            final Member member,
            final int completion,
            final String introduction
    ) {
        this(null, member, completion, introduction);
    }

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

    // 첨부 항목 등록 또는 삭제에서만 호출
    public void updateIsAttachUrl(final Boolean isAttachUrl) {

    }

    public void updateIsAttachFile(final Boolean isAttachFile) {
        
    }

    public void updateMemberProfileTypeByCompletion() {

        // 완성도 값을 호출한다.
        final int presentCompletion = this.getCompletion();

        // 사용자의 권한 상태를 호출한다.
        final MemberProfileType memberProfileType = this.getMember().getMemberProfileType();

        if (presentCompletion >= 0 && presentCompletion < 50) {
            if (MemberProfileType.NO_PERMISSION.equals(memberProfileType)) {
                // 아무 조치를 하지 않는다.
                return;
            } else {
                // 같지 않으면 기존에 프로필 열람 및 매칭 요청 권한이 부여된 상태이다.
                // false 전달하여 NO_PERMISSION 상태로 변경을 진행한다.
                this.getMember().openAndClosePermission(false);
            }
        } else if (presentCompletion >= 50 && presentCompletion < 80) {
            if (MemberProfileType.PROFILE_OPEN_PERMISSION.equals(memberProfileType)) {
                return;
            } else {
                // true 전달하여 PROFILE_OPEN_PERMISSION 상태로 변경한다.
                this.getMember().openAndClosePermission(true);
            }
        } else {
            if (MemberProfileType.MATCHING_PERMISSION.equals(memberProfileType)) {
                return;
            } else {
                this.getMember().changeAndOpenPermission(true);
            }
        }
    }

    // Default 항목에 대한 검사 진행
    private void checkAndUpdateByDefault() {
        if (this.isProfileSkill && this.isProfileTeamBuildingField && this.isEducation) {
            // 전부 참인 경우
            this.completion = 59;
            this.getMember().openAndClosePermission(true);
        } else {
            this.completion = 0;
            this.getMember().openAndClosePermission(false);
        }
    }


}
