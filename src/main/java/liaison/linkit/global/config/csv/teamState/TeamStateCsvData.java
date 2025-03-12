package liaison.linkit.global.config.csv.teamState;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class TeamStateCsvData {
    private String teamStateName;

    public static List<String> getFieldNames() {
        Field[] declaredFields = TeamStateCsvData.class.getDeclaredFields();
        List<String> result = new ArrayList<>();
        for (Field declaredField : declaredFields) {
            result.add(declaredField.getName());
        }
        return result;
    }
}
