package liaison.linkit.global.config.csv.skill;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class SkillCsvData {
    // 기술 이름
    private String skillName;

    public static List<String> getFieldNames() {
        Field[] declaredFields = SkillCsvData.class.getDeclaredFields();
        List<String> result = new ArrayList<>();
        for (Field declaredField : declaredFields) {
            result.add(declaredField.getName());
        }
        return result;
    }
}
