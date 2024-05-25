package liaison.linkit.team.domain.activity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class ActivityRegion {
    // 활동 지역 데이터 시/도 + 시/군/구
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "activity_region_id")
    private Long id;

    @Column(name = "region_name")
    private String regionName;
}
