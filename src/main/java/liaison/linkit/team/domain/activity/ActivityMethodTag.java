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
public class ActivityMethodTag {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "activity_method_tag_id")
    private Long id;

    @Column(name = "activity_tag_name")
    private String activityTagName;
}
