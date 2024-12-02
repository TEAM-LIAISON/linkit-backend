package liaison.linkit.team.domain;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import liaison.linkit.common.domain.BaseDateTimeEntity;
import liaison.linkit.profile.domain.portfolio.ProjectSize;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class TeamProduct extends BaseDateTimeEntity {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

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

    private String productDescription; // 프로덕트 설명
    private String productRepresentImagePath; // 프로덕트 대표 이미지
}
