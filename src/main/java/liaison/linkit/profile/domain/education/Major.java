package liaison.linkit.profile.domain.education;

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
public class Major {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "major_id")
    private Long id;

    @Column(nullable = false, length = 50)
    private String majorType;
}
