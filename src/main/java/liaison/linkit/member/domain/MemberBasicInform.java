package liaison.linkit.member.domain;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

import liaison.linkit.common.domain.BaseDateTimeEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 이름 및 사용자 정보 기입 플로우 추가 구현 필요
@Entity
@Getter
@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor
public class MemberBasicInform extends BaseDateTimeEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "member_id", unique = true)
    private Member member;

    // 성함
    @Column(length = 50)
    private String memberName;

    // 연락처
    @Column(length = 30)
    private String contact;

    // 서비스 이용약관 동의
    private boolean serviceUseAgree;

    // 개인정보 수집 및 이용 동의
    private boolean privateInformAgree;

    // 만 14세 이상
    private boolean ageCheck;

    // 광고성 정보 수신 동의
    private boolean marketingAgree;

    public boolean isMemberBasicInform() {
        if (this.memberName == null || this.contact == null) {
            // member.isMemberBasicInform false로 설정
            return false;
        } else {
            // member.isMemberBasicInform true로 설정
            return true;
        }
    }
}
