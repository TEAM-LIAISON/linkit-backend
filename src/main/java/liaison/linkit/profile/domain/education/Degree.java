package liaison.linkit.profile.domain.education;

import jakarta.persistence.*;
import liaison.linkit.team.domain.miniprofile.IndustrySector;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
public class Degree {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "degree_id")
    private Long id;

    @Column(name = "degree_name", nullable = false)
    private String degreeName;

    public static Degree of(
            final String degreeName
    ) {
        return new Degree(
                null,
                degreeName
        );
    }
}
