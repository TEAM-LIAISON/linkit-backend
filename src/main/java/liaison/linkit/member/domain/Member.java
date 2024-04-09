package liaison.linkit.member.domain;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;
import static liaison.linkit.member.domain.MemberState.ACTIVE;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@SQLDelete(sql = "UPDATE member SET status = 'DELETED' WHERE id = ?")
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

    @Column(nullable = false)
    private LocalDateTime lastLoginDate;

    @Column
    private String imageUrl;

    @Enumerated(value = STRING)
    private MemberState status;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime modifiedAt;

    @OneToOne(mappedBy = "member")
    private MemberBasicInform memberBasicInform;

    public Member(
            final Long id,
            final String socialLoginId,
            final String email,
            final MemberBasicInform memberBasicInform
    ) {
        this.id = id;
        this.email = email;
        this.socialLoginId = socialLoginId;
        this.lastLoginDate = LocalDateTime.now();
        this.imageUrl = imageUrl;
        this.status = ACTIVE;
        this.createdAt = LocalDateTime.now();
        this.modifiedAt = LocalDateTime.now();
        this.memberBasicInform = memberBasicInform;
    }

    public Member(final String socialLoginId, final String email, final MemberBasicInform memberBasicInform) {
        this(null, socialLoginId, email, memberBasicInform);
    }
}
