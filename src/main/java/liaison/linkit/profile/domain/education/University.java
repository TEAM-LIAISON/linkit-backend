package liaison.linkit.profile.domain.education;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Builder
@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
public class University {

    // 대학교 이름만 다룬다.

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "university_id")
    private Long id;

    @Column(name = "university_name")
    private String universityName;

    public static University of(
            final String universityName
    ) {
        return new University(
                null,
                universityName
        );
    }
}
