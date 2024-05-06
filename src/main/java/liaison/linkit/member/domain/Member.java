package liaison.linkit.member.domain;

import jakarta.persistence.*;
import liaison.linkit.profile.domain.Profile;
import liaison.linkit.profile.domain.type.ProfileType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;
import static liaison.linkit.member.domain.MemberState.ACTIVE;
import static liaison.linkit.profile.domain.type.ProfileType.NO_PERMISSION;
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
    private ProfileType profileType;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime modifiedAt;

    @OneToOne(mappedBy = "member")
    private MemberBasicInform memberBasicInform;

    @OneToOne(mappedBy = "member")
    private Profile profile;

    public Member(
            final Long id,
            final String socialLoginId,
            final String email,
            final MemberBasicInform memberBasicInform
    ) {
        this.id = id;
        this.socialLoginId = socialLoginId;
        this.email = email;
        this.profileType = NO_PERMISSION;
        this.status = ACTIVE;
        this.createdAt = LocalDateTime.now();
        this.modifiedAt = LocalDateTime.now();
        this.memberBasicInform = memberBasicInform;
    }

    public Member(final String socialLoginId, final String email, final MemberBasicInform memberBasicInform) {
        this(null, socialLoginId, email, memberBasicInform);
    }

    public void openPermission(final Boolean isOpen) {
        this.profileType = ProfileType.openPermission(isOpen);
    }

    public void changePermission(final Boolean isMatching) {
        this.profileType = ProfileType.changePermission(isMatching);
    }
}
