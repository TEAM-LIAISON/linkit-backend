package liaison.linkit.profile.domain.education;

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
public class School {

    // 대학교 이름만 다룬다.

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "school_id")
    private Long id;

    @Column(name = "school_name")
    private String schoolName;
}
