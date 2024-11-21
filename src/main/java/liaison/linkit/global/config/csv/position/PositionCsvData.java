package liaison.linkit.global.config.csv.position;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class PositionCsvData {
    private String majorPosition;
    private String subPosition;

    public static List<String> getFieldNames() {
        Field[] declaredFields = PositionCsvData.class.getDeclaredFields();
        List<String> result = new ArrayList<>();
        for (Field declaredField : declaredFields) {
            result.add(declaredField.getName());
        }
        return result;
    }
}
