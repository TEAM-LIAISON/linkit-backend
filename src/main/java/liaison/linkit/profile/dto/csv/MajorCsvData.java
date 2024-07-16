package liaison.linkit.profile.dto.csv;

import lombok.Data;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Data
public class MajorCsvData {

    public String majorName;

    public static List<String> getFieldNames() {
        Field[] declaredFields = MajorCsvData.class.getDeclaredFields();
        List<String> result = new ArrayList<>();
        for (Field declaredField : declaredFields) {
            result.add(declaredField.getName());
        }
        return result;
    }
}
