package liaison.linkit.team.domain.miniprofile;

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
public class TeamScale {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "team_size_id")
    private Long id;

    @Column(name = "size_type")
    private String sizeType;

    public static TeamScale of(
            final String sizeType
    ) {
        return new TeamScale(
                null,
                sizeType
        );
    }
}
