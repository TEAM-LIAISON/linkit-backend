package liaison.linkit.global.config.csv.scale;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class ScaleCsvData {
    public String scaleName;

    public static List<String> getFieldNames() {
        Field[] declaredFields = ScaleCsvData.class.getDeclaredFields();
        List<String> result = new ArrayList<>();
        for (Field declaredField : declaredFields) {
            result.add(declaredField.getName());
        }
        return result;
    }
}
