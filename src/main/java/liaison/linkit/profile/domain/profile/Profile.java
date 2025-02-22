package liaison.linkit.profile.domain.profile;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import java.util.ArrayList;
import java.util.List;
import liaison.linkit.common.consts.ProfileStatic;
import liaison.linkit.global.BaseEntity;
import liaison.linkit.member.domain.Member;
import liaison.linkit.profile.domain.position.ProfilePosition;
import liaison.linkit.profile.domain.region.ProfileRegion;
import liaison.linkit.profile.domain.skill.ProfileSkill;
import liaison.linkit.profile.domain.state.ProfileCurrentState;
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

    @OneToOne(cascade = ALL, orphanRemoval = true, fetch = LAZY)
    @JoinColumn(name = "member_id", unique = true)
    private Member member;

    @OneToMany(mappedBy = "profile")
    private List<ProfilePosition> profilePositions = new ArrayList<>();

    @OneToMany(mappedBy = "profile")
    private List<ProfileSkill> profileSkills = new ArrayList<>();

    @OneToOne(mappedBy = "profile")
    private ProfileRegion profileRegion;

    @OneToMany(mappedBy = "profile")
    private List<ProfileCurrentState> profileCurrentStates = new ArrayList<>();

    // 프로필 이미지 경로
    private String profileImagePath;

    // 프로필 공개 여부
    private boolean isProfilePublic;

    // 프로필 완성도
    private int profileCompletion;

    private boolean isProfileMiniProfile;
    private boolean isProfileSkill;
    private boolean isProfileActivity;
    private boolean isProfilePortfolio;
    private boolean isProfileEducation;
    private boolean isProfileAwards;
    private boolean isProfileLicense;
    private boolean isProfileLink;

    public void setIsProfilePublic(final boolean isProfilePublic) {
        this.isProfilePublic = isProfilePublic;
    }

    public void setIsProfileMiniProfile(final boolean isProfileMiniProfile) {
        this.isProfileMiniProfile = isProfileMiniProfile;
    }

    public void setIsProfileSkill(final boolean isProfileSkill) {
        this.isProfileSkill = isProfileSkill;
    }

    public void setIsProfileActivity(final boolean isProfileActivity) {
        this.isProfileActivity = isProfileActivity;
    }

    public void setIsProfilePortfolio(final boolean isProfilePortfolio) {
        this.isProfilePortfolio = isProfilePortfolio;
    }

    public void setIsProfileEducation(final boolean isProfileEducation) {
        this.isProfileEducation = isProfileEducation;
    }

    public void setIsProfileAwards(final boolean isProfileAwards) {
        this.isProfileAwards = isProfileAwards;
    }

    public void setIsProfileLicense(final boolean isProfileLicense) {
        this.isProfileLicense = isProfileLicense;
    }

    public void setIsProfileLink(final boolean isProfileLink) {
        this.isProfileLink = isProfileLink;
    }

    public void updateProfileImagePath(final String profileImagePath) {
        this.profileImagePath = profileImagePath;
    }

    // 보유 스킬

    public void addProfileSkillCompletion() {
        this.profileCompletion += ProfileStatic.SKILL_COMPLETION;
    }

    public void removeProfileSkillCompletion() {
        this.profileCompletion -= ProfileStatic.SKILL_COMPLETION;
    }

    // 이력

    public void addProfileActivityCompletion() {
        this.profileCompletion += ProfileStatic.ACTIVITY_COMPLETION;
    }

    public void removeProfileActivityCompletion() {
        this.profileCompletion -= ProfileStatic.ACTIVITY_COMPLETION;
    }

    // 포트폴리오

    public void addProfilePortfolioCompletion() {
        this.profileCompletion += ProfileStatic.PORTFOLIO_COMPLETION;
    }

    public void removeProfilePortfolioCompletion() {
        this.profileCompletion -= ProfileStatic.PORTFOLIO_COMPLETION;
    }

    // 학력

    public void addProfileEducationCompletion() {
        this.profileCompletion += ProfileStatic.EDUCATION_COMPLETION;
    }

    public void removeProfileEducationCompletion() {
        this.profileCompletion -= ProfileStatic.EDUCATION_COMPLETION;
    }

    // 수상

    public void addProfileAwardsCompletion() {
        this.profileCompletion += ProfileStatic.AWARDS_COMPLETION;
    }

    public void removeProfileAwardsCompletion() {
        this.profileCompletion -= ProfileStatic.AWARDS_COMPLETION;
    }

    // 자격증

    public void addProfileLicenseCompletion() {
        this.profileCompletion += ProfileStatic.LICENSE_COMPLETION;
    }

    public void removeProfileLicenseCompletion() {
        this.profileCompletion -= ProfileStatic.LICENSE_COMPLETION;
    }

    // 링크

    public void addProfileLinkCompletion() {
        this.profileCompletion += ProfileStatic.LINK_COMPLETION;
    }

    public void removeProfileLinkCompletion() {
        this.profileCompletion -= ProfileStatic.LINK_COMPLETION;
    }
}
