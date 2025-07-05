package liaison.linkit.global.config.csv.profileState;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class ProfileStateCsvData {
    private String profileStateName;

    public static List<String> getFieldNames() {
        Field[] declaredFields = ProfileStateCsvData.class.getDeclaredFields();
        List<String> result = new ArrayList<>();
        for (Field declaredField : declaredFields) {
            result.add(declaredField.getName());
        }
        return result;
    }
}
