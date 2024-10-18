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

    // 계정 상태 관리 컬럼
    @Column(nullable = false)
    @Enumerated(value = STRING)
    private MemberState memberState;

    @Column(nullable = false)
    private boolean existMemberBasicInform;

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
        this.memberState = ACTIVE;
        this.memberBasicInform = memberBasicInform;
        this.existMemberBasicInform = false;
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

    public void existIsMemberBasicInform(final Boolean existMemberBasicInform) {
        this.existMemberBasicInform = existMemberBasicInform;
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
