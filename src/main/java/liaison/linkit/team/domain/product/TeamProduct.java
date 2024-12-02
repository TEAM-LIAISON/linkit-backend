package liaison.linkit.team.domain.product;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import liaison.linkit.common.domain.BaseDateTimeEntity;
import liaison.linkit.profile.domain.portfolio.ProjectSize;
import liaison.linkit.team.domain.Team;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class TeamProduct extends BaseDateTimeEntity {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    private String productName;
    private String productLineDescription;

    @Column(nullable = false)
    @Enumerated(value = STRING)
    private ProjectSize projectSize; // 규모

    private int productHeadCount; // 인원
    private String productTeamComposition; // 팀 구성

    private String productStartDate; // 프로덕트 시작 날짜
    private String productEndDate; // 프로덕트 종료 날짜
    private boolean isProductInProgress; // 프로덕트 진행 중 여부

    @OneToMany(mappedBy = "teamProduct", fetch = FetchType.LAZY)
    private List<ProductLink> productLinks = new ArrayList<>();

    private String productDescription; // 프로덕트 설명
    private String productRepresentImagePath; // 프로덕트 대표 이미지

    public void updateProductRepresentImagePath(final String productRepresentImagePath) {
        this.productRepresentImagePath = productRepresentImagePath;
    }
}
