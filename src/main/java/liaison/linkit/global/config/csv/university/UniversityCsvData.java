package liaison.linkit.global.config.csv.university;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class UniversityCsvData {
    public String universityName;

    public static List<String> getFieldNames() {
        Field[] declaredFields = UniversityCsvData.class.getDeclaredFields();
        List<String> result = new ArrayList<>();
        for (Field declaredField : declaredFields) {
            result.add(declaredField.getName());
        }
        return result;
    }
}
