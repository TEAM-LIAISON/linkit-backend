package liaison.linkit.team.domain.scale;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
public class Scale {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    // 시/도 이름
    @Column(name = "scale_name")
    private String scaleName;

    public static Scale of(
            final String scaleName
    ) {
        return new Scale(
                null,
                scaleName
        );
    }
}
