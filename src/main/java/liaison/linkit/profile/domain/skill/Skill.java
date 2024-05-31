package liaison.linkit.profile.domain.skill;

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
public class Skill {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "skill_id")
    private Long id;

    @Column(name = "role_field")
    private String roleField;

    @Column(name = "skill_name")
    private String skillName;

    public static Skill of(
            final String roleField,
            final String skillName
    ) {
        return new Skill(
                null,
                roleField,
                skillName
        );
    }
}
