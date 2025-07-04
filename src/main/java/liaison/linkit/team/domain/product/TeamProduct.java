package liaison.linkit.team.domain.product;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import liaison.linkit.common.domain.BaseDateTimeEntity;
import liaison.linkit.team.domain.team.Team;
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
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @Column(nullable = false, length = 50)
    private String productName; // 프로덕트명

    @Column(nullable = false, length = 100)
    private String productLineDescription; // 프로덕트 한줄소개

    @Column(nullable = false)
    private String productField; // 프로덕트 분야

    @Column(nullable = false)
    private String productStartDate; // 프로덕트 시작 날짜

    private String productEndDate; // 프로덕트 종료 날짜
    private boolean isProductInProgress; // 프로덕트 진행 중 여부

    @Column(length = 1000)
    private String productDescription; // 프로덕트 설명

    private String productRepresentImagePath; // 프로덕트 대표 이미지

    public void updateProductRepresentImagePath(final String productRepresentImagePath) {
        this.productRepresentImagePath = productRepresentImagePath;
    }
}
