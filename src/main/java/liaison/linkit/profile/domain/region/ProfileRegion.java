package liaison.linkit.profile.domain.region;

import jakarta.persistence.*;
import liaison.linkit.profile.domain.Profile;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
public class ProfileRegion {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @OneToOne(cascade = ALL, orphanRemoval = true, fetch = LAZY)
    @JoinColumn(name = "profile_id", unique = true)
    private Profile profile;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "region_id")
    private Region region;
}
