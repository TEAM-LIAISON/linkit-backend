package liaison.linkit.profile.domain.education;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
public class School {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "school_id")
    private Long id;

    @Column(name = "school_name")
    private String schoolName;
}
