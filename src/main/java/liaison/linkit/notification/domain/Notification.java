package liaison.linkit.notification.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import liaison.linkit.global.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Notification extends BaseEntity {

    // 알림 기능의 역할
    // 누군가로부터 알림이 왔다는 것을 확인할 수 있는 용도
    // 매칭 요청에 의해서 발생하는 이벤트

    // 알림에도 여러 종류가 있을 수 있다. -> 의논 필요
    // 일단은 매칭 관련된 알림만 보내는 것으로

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "notification_id")
    private Long id;


}
