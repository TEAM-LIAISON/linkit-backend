package liaison.linkit.profile.domain.teambuilding;

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
public class ProfileTeamBuildingField {

    // 다대다를 풀어서 설계하는 것은 맞다 (필터링 떄문에 - 단순 저장 정보가 아니다)

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "profile_team_building_field_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "profile_id")
    private Profile profile;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "team_building_field_id")
    private TeamBuildingField teamBuildingField;

    public static ProfileTeamBuildingField of(
        final Profile profile,
        final TeamBuildingField teamBuildingField
    ){
        return new ProfileTeamBuildingField(
                null,
                profile,
                teamBuildingField
        );
    }
}
