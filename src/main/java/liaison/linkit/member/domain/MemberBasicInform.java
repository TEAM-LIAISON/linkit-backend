package liaison.linkit.member.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

// 이름 및 사용자 정보 기입 플로우 추가 구현 필요
@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class MemberBasicInform {

    private static final String DEFAULT_IMAGE_NAME = "default-profile-image.png";

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "member_basic_inform_id")
    private Long id;

    @Column(nullable = false)
    private String profileImageName;

    // 성함
    @Column(nullable = false, length = 30)
    private String memberName;

    // 연락처
    @Column(nullable = false, length = 30)
    private String contact;

    // 직무/역할 추가 필요
    @ManyToOne(fetch = FetchType.LAZY, cascade = ALL)
    @JoinColumn(name = "member_role_id")
    private MemberRole memberRole;

    // 뉴스레터 및 마케팅 정보 수신동의
    @Column
    private boolean marketingAgree;

    @OneToOne(cascade = ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", unique = true)
    private Member member;

    // 생성자
    public MemberBasicInform(
            final Long id,
            final String memberName,
            final String contact,
            final MemberRole memberRole,
            final boolean marketingAgree,
            final Member member)
    {
        this.id = id;
        this.profileImageName = DEFAULT_IMAGE_NAME;
        this.memberName = memberName;
        this.contact = contact;
        this.memberRole = memberRole;
        this.marketingAgree = marketingAgree;
        this.member = member;
    }

    public MemberBasicInform(
            final String memberName,
            final String contact,
            final MemberRole memberRole,
            final boolean marketingAgree,
            final Member member) {
        this(null, memberName, contact, memberRole, marketingAgree, member);
    }
}
