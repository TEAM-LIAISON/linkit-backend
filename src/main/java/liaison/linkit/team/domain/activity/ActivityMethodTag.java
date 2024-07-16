package liaison.linkit.team.domain.activity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
public class ActivityMethodTag {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "activity_method_tag_id")
    private Long id;
    
    // 활동 방식 태그 4개 (사무실 있음, 사무실 없음, 대면 활동 선호, 대면 + 비대면)
    @Column(name = "activity_tag_name")
    private String activityTagName;

    public static ActivityMethodTag of(
            final String activityTagName
    ) {
        return new ActivityMethodTag(
                null,
                activityTagName
        );
    }
}
