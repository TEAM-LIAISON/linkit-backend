package liaison.linkit.profile.domain.region;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
public class Region {

    // 활동 지역 데이터 시/도 + 시/군/구
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "region_id")
    private Long id;

    // 시/도 이름
    @Column(name = "city_name")
    private String cityName;

    // 시/군/구 이름
    @Column(name = "division_name")
    private String divisionName;

    public static Region of(final String cityName, final String divisionName) {
        return new Region(null, cityName, divisionName);
    }
}
