package liaison.linkit.global.config.csv.region;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class RegionCsvData {

    // 시도 이름
    public String cityName;

    // 시군구 이름
    public String divisionName;

    public static List<String> getFieldNames() {
        Field[] declaredFields = RegionCsvData.class.getDeclaredFields();
        List<String> result = new ArrayList<>();
        for (Field declaredField : declaredFields) {
            result.add(declaredField.getName());
        }
        return result;
    }
}
