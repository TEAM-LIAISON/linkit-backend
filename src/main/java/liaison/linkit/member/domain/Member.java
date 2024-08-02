package liaison.linkit.member.domain;

import jakarta.persistence.*;
import liaison.linkit.member.domain.type.MemberType;
import liaison.linkit.member.domain.type.ProfileType;
import liaison.linkit.member.domain.type.TeamProfileType;
import liaison.linkit.profile.domain.Profile;
import liaison.linkit.team.domain.TeamProfile;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static liaison.linkit.member.domain.MemberState.ACTIVE;
import static liaison.linkit.member.domain.type.MemberType.EMPTY_PROFILE;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@SQLRestriction("status = 'ACTIVE'")
public class Member {

    private static final String DEFAULT_MEMBER_IMAGE_NAME = "default-image.png";

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "member", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = LAZY)
    private MemberBasicInform memberBasicInform;

    @OneToOne(mappedBy = "member", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = LAZY)
    private Profile profile;

    @OneToOne(mappedBy = "member", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = LAZY)
    private TeamProfile teamProfile;

    @Column(nullable = false, length = 100)
    private String socialLoginId;

    @Column(nullable = false, length = 50)
    private String email;

    // 계정 상태 관리 컬럼
    @Enumerated(value = STRING)
    private MemberState status;

    // 멤버 타입 관리
    @Column(nullable = false)
    @Enumerated(value = STRING)
    private MemberType memberType;

    // 내 이력서 타입 (완성도 기반)
    @Column(nullable = false)
    @Enumerated(value = STRING)
    private ProfileType profileType;

    // 팀 소개서 타입 (완성도 기반)
    @Column(nullable = false)
    @Enumerated(value = STRING)
    private TeamProfileType teamProfileType;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime modifiedAt;

    @Column(nullable = false)
    private boolean existMemberBasicInform;

    // 내 이력서 기본 항목 기입 여부
    @Column(nullable = false)
    private boolean existDefaultPrivateProfile;

    // 팀 소개서 기본 항목 기입 여부
    @Column(nullable = false)
    private boolean existDefaultTeamProfile;

    @Column(nullable = false)
    private int privateWishCount;

    @Column(nullable = false)
    private int teamWishCount;

    public Member(
            final Long id,
            final String socialLoginId,
            final String email,
            final MemberBasicInform memberBasicInform
    ) {
        this.id = id;
        this.socialLoginId = socialLoginId;
        this.email = email;
        this.memberType = EMPTY_PROFILE;
        this.profileType = ProfileType.NO_PERMISSION;
        this.teamProfileType = TeamProfileType.NO_PERMISSION;
        this.status = ACTIVE;
        this.createdAt = LocalDateTime.now();
        this.modifiedAt = LocalDateTime.now();
        this.memberBasicInform = memberBasicInform;
        this.existMemberBasicInform = false;
        this.existDefaultPrivateProfile = false;
        this.existDefaultTeamProfile = false;
        this.privateWishCount = 0;
        this.teamWishCount = 0;
    }

    public Member(
            final String socialLoginId,
            final String email,
            final MemberBasicInform memberBasicInform
    ) {
        this(null, socialLoginId, email, memberBasicInform);
    }

    public void changeIsMemberBasicInform(final Boolean existMemberBasicInform) {
        this.existMemberBasicInform = existMemberBasicInform;
    }

    public void setProfileType(final ProfileType profileType) {
        this.profileType = profileType;
    }

    public void setTeamProfileType(final TeamProfileType teamProfileType) {
        this.teamProfileType = teamProfileType;
    }

    public void addPrivateWishCount() {
        this.privateWishCount += 1;
    }

    public void addTeamWishCount() {
        this.teamWishCount += 1;
    }

    public void subPrivateWishCount() {
        this.privateWishCount -= 1;
    }

    public void subTeamWishCount() {
        this.teamWishCount -= 1;
    }
}
