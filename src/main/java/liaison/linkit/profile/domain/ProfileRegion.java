package liaison.linkit.profile.domain;

import jakarta.persistence.*;
import liaison.linkit.region.domain.Region;
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
public class ProfileRegion {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "profile_id")
    private Profile profile;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "region_id")
    private Region region;
}
