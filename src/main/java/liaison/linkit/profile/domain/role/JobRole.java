package liaison.linkit.profile.domain.role;

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
public class JobRole {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "job_role_id")
    private Long id;

    @Column(name = "job_role_name")
    private String jobRoleName;

    public static JobRole of(
            final String jobRoleName
    ) {
        return new JobRole(
                null,
                jobRoleName
        );
    }
}
