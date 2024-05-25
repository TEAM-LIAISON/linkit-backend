package liaison.linkit.profile.dto.csv;

import lombok.Data;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Data
public class TeamBuildingFieldCsvData {

    public String teamBuildingFieldName;

    public static List<String> getFieldNames() {
        Field[] declaredFields = TeamBuildingFieldCsvData.class.getDeclaredFields();
        List<String> result = new ArrayList<>();
        for (Field declaredField : declaredFields) {
            result.add(declaredField.getName());
        }
        return result;
    }
}
