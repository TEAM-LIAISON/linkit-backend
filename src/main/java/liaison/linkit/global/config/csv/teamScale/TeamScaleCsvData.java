package liaison.linkit.global.config.csv.teamScale;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class TeamScaleCsvData {
    public String scaleName;

    public static List<String> getFieldNames() {
        Field[] declaredFields = TeamScaleCsvData.class.getDeclaredFields();
        List<String> result = new ArrayList<>();
        for (Field declaredField : declaredFields) {
            result.add(declaredField.getName());
        }
        return result;
    }
}
