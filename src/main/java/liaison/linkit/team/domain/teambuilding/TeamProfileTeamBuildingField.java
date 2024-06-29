package liaison.linkit.team.domain.teambuilding;

import jakarta.persistence.*;
import liaison.linkit.profile.domain.teambuilding.TeamBuildingField;
import liaison.linkit.team.domain.TeamProfile;
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
public class TeamProfileTeamBuildingField {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "team_profile_team_building_field_id")
    private Long id;

    // 팀 소개서 연관관계 매핑
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "team_profile_id")
    private TeamProfile teamProfile;

    // 팀빌딩 분야 매핑
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "team_building_field_id")
    private TeamBuildingField teamBuildingField;

    public static TeamProfileTeamBuildingField of(
            final TeamProfile teamProfile,
            final TeamBuildingField teamBuildingField
    ) {
        return new TeamProfileTeamBuildingField(
                null,
                teamProfile,
                teamBuildingField
        );
    }

    // 업데이트 로직 추가 구현 필요
//    public void update(final TeamProfileTeamBuildingFieldUpdateRequest updateRequest) {
//        this.teamProfile = updateRequest.get
//    }
}
