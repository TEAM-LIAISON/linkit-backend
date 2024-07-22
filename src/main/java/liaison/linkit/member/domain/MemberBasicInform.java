package liaison.linkit.member.domain;

import jakarta.persistence.*;
import liaison.linkit.global.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

// 이름 및 사용자 정보 기입 플로우 추가 구현 필요
@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class MemberBasicInform extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "member_basic_inform_id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "member_id")
    private Member member;

    // 성함
    @Column(nullable = false, length = 30)
    private String memberName;

    // 연락처
    @Column(nullable = false, length = 30)
    private String contact;

    // 뉴스레터 및 마케팅 정보 수신동의
    @Column
    private boolean marketingAgree;

    // 생성자
    public MemberBasicInform(
            final Long id,
            final String memberName,
            final String contact,
            final boolean marketingAgree,
            final Member member
    ) {
        this.id = id;
        this.memberName = memberName;
        this.contact = contact;
        this.marketingAgree = marketingAgree;
        this.member = member;
    }

    public MemberBasicInform(
            final String memberName,
            final String contact,
            final boolean marketingAgree,
            final Member member) {
        this(null, memberName, contact, marketingAgree, member);
    }

    public void update(final MemberBasicInform memberBasicInform) {
        this.memberName = memberBasicInform.getMemberName();
        this.contact = memberBasicInform.getContact();
        this.marketingAgree = memberBasicInform.isMarketingAgree();
    }
}
