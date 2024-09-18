package liaison.linkit.common.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

// JPA - Auditing 기능을 활성화하여 저장, 업데이트될 때 특정 필드를 채움
@EntityListeners(AuditingEntityListener.class)
// 이 클래스를 직접 테이블로 매핑하지 않고, 다른 엔티티 클래스들이 상속받아 사용할 수 있는 공통 매핑 정보로 제공
@MappedSuperclass
@Getter
public abstract class BaseDateTimeEntity {
    @Column(updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
