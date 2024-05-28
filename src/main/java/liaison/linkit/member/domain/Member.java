package liaison.linkit.member.domain;

import jakarta.persistence.*;
import liaison.linkit.member.domain.type.MemberProfileType;
import liaison.linkit.profile.domain.Profile;
import liaison.linkit.team.domain.TeamProfile;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;
import static liaison.linkit.member.domain.MemberState.ACTIVE;
import static liaison.linkit.member.domain.type.MemberProfileType.EMPTY_PROFILE;
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

    @Column(nullable = false, length = 100)
    private String socialLoginId;

    @Column(nullable = false, length = 50)
    private String email;

    @Enumerated(value = STRING)
    private MemberState status;

    @Column(nullable = false)
    @Enumerated(value = STRING)
    private MemberProfileType memberProfileType;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime modifiedAt;

    @OneToOne(mappedBy = "member")
    private MemberBasicInform memberBasicInform;

    @OneToOne(mappedBy = "member")
    private Profile profile;

    @OneToOne(mappedBy = "member")
    private TeamProfile teamProfile;

    // MemberBasicInform 기입 여부 판단 코드 추가? (프론트 상태 관리용)
    @Column(nullable = false)
    private boolean isMemberBasicInform;

    public Member(
            final Long id,
            final String socialLoginId,
            final String email,
            final MemberBasicInform memberBasicInform
    ) {
        this.id = id;
        this.socialLoginId = socialLoginId;
        this.email = email;
        this.memberProfileType = EMPTY_PROFILE;
        this.status = ACTIVE;
        this.createdAt = LocalDateTime.now();
        this.modifiedAt = LocalDateTime.now();
        this.memberBasicInform = memberBasicInform;
        this.isMemberBasicInform = false;
    }

    public Member(final String socialLoginId, final String email, final MemberBasicInform memberBasicInform) {this(null, socialLoginId, email, memberBasicInform);}

    public void changeIsMemberBasicInform(final Boolean isMemberBasicInform) {this.isMemberBasicInform = isMemberBasicInform;}

    public boolean getIsMemberBasicInform() {return this.isMemberBasicInform;}
}
