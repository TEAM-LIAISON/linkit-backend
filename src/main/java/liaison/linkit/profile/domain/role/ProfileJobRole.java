package liaison.linkit.profile.domain.role;

import jakarta.persistence.*;
import liaison.linkit.profile.domain.Profile;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
public class ProfileJobRole {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "profile_job_role_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "profile_id")
    private Profile profile;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "job_role_id")
    private JobRole jobRole;
}
