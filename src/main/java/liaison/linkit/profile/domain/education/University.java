package liaison.linkit.profile.domain.education;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;

import static jakarta.persistence.GenerationType.IDENTITY;

@Builder
@Entity
@Getter
public class University {

    // 대학교 이름만 다룬다.

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "university_id")
    private Long id;

    @Column(name = "university_name")
    private String universityName;
}
