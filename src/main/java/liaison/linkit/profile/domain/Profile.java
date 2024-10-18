package liaison.linkit.profile.domain;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import java.util.Objects;
import liaison.linkit.common.domain.ProfileState;
import liaison.linkit.global.BaseEntity;
import liaison.linkit.member.domain.Member;
import liaison.linkit.profile.domain.region.Region;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

// [2.0.0] 내 프로필
@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
@SQLRestriction("status = 'USABLE'")
public class Profile extends BaseEntity {

    // 13% * 4 + 16% * 3
    private static final int LOW_COMPLETION = 13;
    private static final int HIGH_COMPLETION = 16;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "region_id", nullable = false)
    private Region region;

    @OneToOne(cascade = ALL, orphanRemoval = true, fetch = LAZY)
    @JoinColumn(name = "member_id", unique = true)
    private Member member;

    // 프로필 이미지 경로
    private String profileImagePath;

    // 프로필 공개 여부
    private boolean isProfilePublic;

    private boolean isProfileSkill;
    private boolean isProfileActivity;
    private boolean isProfilePortfolio;
    private boolean isProfileEducation;
    private boolean isProfileAwards;
    private boolean isProfileLicense;
    private boolean isProfileLink;

    // 3.1. 미니 프로필 업데이트
    public void updateIsMiniProfile(final Boolean isMiniProfile) {
        this.isMiniProfile = isMiniProfile;
    }

    // 3.4. 자기소개 업데이트
    // introduction ""인 경우 -> 삭제로 간주한다.
    public void updateIntroduction(final String introduction) {
        if (!Objects.equals(introduction, "")) {            // 삭제 요청이 아닌 수정/생성인 경우
            if (this.introduction != null) {                    // 기존에 데이터가 있는 경우 (수정)
                this.introduction = introduction;
            } else {                                            // 기존에 데이터가 없는 경우 (생성)
                this.introduction = introduction;
                updateIsIntroduction(true);
                addPerfectionThirty();
            }
        } else {                              // 삭제 요청인 경우
            deleteIntroduction();
            updateIsIntroduction(false);
            cancelPerfectionThirty();
        }
    }

    // 3.5.2. 내 이력서 보유 기술
    public void updateIsProfileSkill(final boolean isProfileSkill) {
        this.isProfileSkill = isProfileSkill;

        // 모두 true인 경우
        if (this.isJobAndSkill != (this.isProfileJobRole && isProfileSkill)) {
            this.isJobAndSkill = !this.isJobAndSkill;
            if (this.isJobAndSkill) {
                addPerfectionDefault();
            } else {
                cancelPerfectionDefault();
            }
        }
    }

    // 3.6. 희망 팀빌딩 분야 업데이트
    public void updateIsProfileTeamBuildingField(final boolean isProfileTeamBuildingField) {
        this.isProfileTeamBuildingField = isProfileTeamBuildingField;
        if (isProfileTeamBuildingField) {
            addPerfectionDefault();
        } else {
            cancelPerfectionDefault();
        }
    }

    // 3.7. 활동 지역 및 위치 업데이트
    public void updateIsProfileRegion(final boolean isProfileRegion) {
        this.isProfileRegion = isProfileRegion;
        if (isProfileRegion) {
            addPerfectionDefault();
        } else {
            cancelPerfectionDefault();
        }
    }

    // 3.8. 이력 업데이트
    public void updateIsAntecedents(final boolean isAntecedents) {
        this.isAntecedents = isAntecedents;
        if (isAntecedents) {
            addPerfectionDefault();
        } else {
            cancelPerfectionDefault();
        }
    }

    // 3.9. 학력 업데이트
    public void updateIsEducation(final boolean isEducation) {
        this.isEducation = isEducation;
        if (isEducation) {
            addPerfectionDefault();
        } else {
            cancelPerfectionDefault();
        }
    }

    // 3.10. 수상 업데이트
    public void updateIsAwards(final boolean isAwards) {
        this.isAwards = isAwards;
        if (isAwards) {
            addPerfectionTen();
        } else {
            cancelPerfectionTen();
        }
    }


    public boolean getIsProfileSkill() {
        return isProfileSkill;
    }

}
