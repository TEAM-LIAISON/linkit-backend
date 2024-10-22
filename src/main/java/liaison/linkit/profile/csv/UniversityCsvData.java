package liaison.linkit.profile.csv;

import lombok.Data;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

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
