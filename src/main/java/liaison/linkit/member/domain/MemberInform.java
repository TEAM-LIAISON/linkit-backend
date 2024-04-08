package liaison.linkit.member.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

// 이름 및 사용자 정보 기입 플로우 추가 구현 필요
@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class MemberInform {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
}
