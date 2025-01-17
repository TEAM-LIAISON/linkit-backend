package liaison.linkit.common.presentation;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RegionResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegionDetail {
        private String cityName;
        private String divisionName;

        public String getFullRegionDetail() {
            if (cityName != null && divisionName != null) {
                return cityName + " " + divisionName;
            } else if (cityName != null) {
                return cityName;
            } else if (divisionName != null) {
                return divisionName;
            }
            return "";
        }
    }
}
