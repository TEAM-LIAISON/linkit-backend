package liaison.linkit.member.domain;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static liaison.linkit.member.domain.type.MemberState.ACTIVE;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import liaison.linkit.common.domain.BaseDateTimeEntity;
import liaison.linkit.member.domain.type.MemberState;
import liaison.linkit.member.domain.type.Platform;
import liaison.linkit.profile.domain.Profile;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@SQLRestriction("member_state = 'ACTIVE'")
public class Member extends BaseDateTimeEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "member", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = LAZY)
    private MemberBasicInform memberBasicInform;

    @OneToOne(mappedBy = "member", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = LAZY)
    private Profile profile;

    @Column(nullable = false, length = 100)
    private String socialLoginId;

    @Column(nullable = false, length = 50)
    private String email;

    @Column(nullable = false, length = 50)
    private String emailId;

    // 계정 상태 관리 컬럼
    @Column(nullable = false)
    @Enumerated(value = STRING)
    private MemberState memberState;

    @Column(nullable = false)
    private boolean createMemberBasicInform;

    @Column(nullable = false)
    private int privateScrapCount;

    @Column(nullable = false)
    private int teamScrapCount;

    @Column(nullable = false)
    private int teamMemberAnnouncementScrapCount;

    @Column(nullable = false)
    @Enumerated(value = STRING)
    private Platform platform;

    public Member(
            final Long id,
            final String socialLoginId,
            final String email,
            final String emailId,
            final MemberBasicInform memberBasicInform,
            final Platform platform
    ) {
        this.id = id;
        this.socialLoginId = socialLoginId;
        this.email = email;
        this.emailId = emailId;
        this.memberState = ACTIVE;
        this.memberBasicInform = memberBasicInform;
        this.createMemberBasicInform = false;
        this.privateScrapCount = 0;
        this.teamScrapCount = 0;
        this.teamMemberAnnouncementScrapCount = 0;
        this.platform = platform;
    }

    public Member(
            final String socialLoginId,
            final String email,
            final String emailId,
            final MemberBasicInform memberBasicInform,
            final Platform platform
    ) {
        this(null, socialLoginId, email, emailId, memberBasicInform, platform);
    }

    public void setCreateMemberBasicInform(final boolean createMemberBasicInform) {
        this.createMemberBasicInform = createMemberBasicInform;
    }

    public void addPrivateScrapCount() {
        this.privateScrapCount += 1;
    }

    public void addTeamScrapCount() {
        this.teamScrapCount += 1;
    }

    public void subPrivateScrapCount() {
        this.privateScrapCount -= 1;
    }

    public void subTeamScrapCount() {
        this.teamScrapCount -= 1;
    }

    public void addTeamMemberAnnouncementScrapCount() {
        this.teamMemberAnnouncementScrapCount += 1;
    }

    public void subTeamMemberAnnouncementScrapCount() {
        this.teamMemberAnnouncementScrapCount -= 1;
    }

    public void updateEmail(final String email) {
        this.email = email;
    }
}
