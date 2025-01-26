package liaison.linkit.common.domain;

import static jakarta.persistence.GenerationType.IDENTITY;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Position {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(name = "major_position", nullable = false)
    private String majorPosition;

    @Column(name = "sub_position", nullable = false)
    private String subPosition;

    public static Position of(
            final String majorPosition,
            final String subPosition
    ) {
        return new Position(
                null,
                majorPosition,
                subPosition
        );
    }
}
