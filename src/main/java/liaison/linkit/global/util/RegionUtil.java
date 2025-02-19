package liaison.linkit.global.util;

public class RegionUtil {

    // 지역 정보를 문자열로 조합하는 유틸리티 메서드
    public static String buildRegionString(
        final String cityName,
        final String divisionName
    ) {
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
