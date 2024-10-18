package liaison.linkit.member.presentation.dto.csv;

import lombok.Data;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Data
public class MemberRoleCsvData {
    private String roleName;

    public static List<String> getFieldNames() {
        Field[] declaredFields = MemberRoleCsvData.class.getDeclaredFields();
        List<String> result = new ArrayList<>();
        for (Field declaredField : declaredFields) {
            result.add(declaredField.getName());
        }
        return result;
    }
}
