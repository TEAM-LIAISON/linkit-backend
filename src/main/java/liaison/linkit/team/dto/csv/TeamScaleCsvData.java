package liaison.linkit.team.dto.csv;

import lombok.Data;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Data
public class TeamScaleCsvData {
    private String sizeType;

    public static List<String> getFieldNames() {
        Field[] declaredFields = TeamScaleCsvData.class.getDeclaredFields();
        List<String> result = new ArrayList<>();
        for (Field declaredField : declaredFields) {
            result.add(declaredField.getName());
        }
        return result;
    }
}
