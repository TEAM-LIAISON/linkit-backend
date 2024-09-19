package liaison.linkit.member.domain;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import liaison.linkit.global.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    // 서비스 이용약관 동의
    @Column(nullable = false)
    private boolean serviceUseAgree;

    // 개인정보 수집 및 이용 동의
    @Column(nullable = false)
    private boolean privateInformAgree;

    // 만 14세 이상
    @Column(nullable = false)
    private boolean ageCheck;

    // 광고성 정보 수신 동의
    @Column(nullable = false)
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
        this.serviceUseAgree = true;
        this.privateInformAgree = true;
        this.ageCheck = true;
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
