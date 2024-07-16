package liaison.linkit.notification.domain;

import jakarta.persistence.*;
import liaison.linkit.global.BaseEntity;
import liaison.linkit.member.domain.Member;
import liaison.linkit.notification.domain.type.NotificationType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
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

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "is_deleted")
    private boolean isDeleted;

    @Column(name = "is_read")
    private boolean isRead;

    @Column(name = "content")
    private String content;

    // 쪽지의 타입
    @Enumerated(value = STRING)
    @Column(name = "notification_type")
    private NotificationType notificationType;

    public Notification(
            final Long id,
            final Member member,
            final String content,
            final NotificationType notificationType
    ) {
        this.id = id;
        this.member = member;
        this.isDeleted = false;
        this.isRead = false;
        this.content = content;
        this.notificationType = notificationType;
    }

    public Notification(
            final Member member,
            final String content,
            final NotificationType notificationType
    ) {
        this(null, member, content, notificationType);
    }
}
