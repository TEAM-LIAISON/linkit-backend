package liaison.linkit.member.domain;

import jakarta.persistence.*;
import liaison.linkit.profile.domain.Profile;
import liaison.linkit.member.domain.type.MemberProfileType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;
import static liaison.linkit.member.domain.MemberState.ACTIVE;
import static liaison.linkit.member.domain.type.MemberProfileType.NO_PERMISSION;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Where(clause = "status = 'ACTIVE'")
public class Member {

    private static final String DEFAULT_MEMBER_IMAGE_NAME = "default-image.png";

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(nullable = false, length = 30)
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
        this.memberProfileType = NO_PERMISSION;
        this.status = ACTIVE;
        this.createdAt = LocalDateTime.now();
        this.modifiedAt = LocalDateTime.now();
        this.memberBasicInform = memberBasicInform;
        this.isMemberBasicInform = false;
    }

    public Member(final String socialLoginId, final String email, final MemberBasicInform memberBasicInform) {this(null, socialLoginId, email, memberBasicInform);}

    public void openAndClosePermission(final Boolean isOpen) {this.memberProfileType = MemberProfileType.openAndClosePermission(isOpen);}

    public void changeAndOpenPermission(final Boolean isMatching) {this.memberProfileType = MemberProfileType.changeAndOpenPermission(isMatching);}

    public void changeIsMemberBasicInform(final Boolean isMemberBasicInform) {this.isMemberBasicInform = isMemberBasicInform;}

    public boolean getIsMemberBasicInform() {return this.isMemberBasicInform;}
}
