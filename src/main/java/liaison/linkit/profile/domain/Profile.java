package liaison.linkit.profile.domain;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import liaison.linkit.global.BaseEntity;
import liaison.linkit.member.domain.Member;
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

    public void setIsProfileEducation(final boolean isProfileAwards) {
        this.isProfileAwards = isProfileAwards;
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
}
