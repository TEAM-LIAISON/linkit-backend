package liaison.linkit.search.business.model;

import java.util.List;

import org.springframework.web.bind.annotation.RequestParam;

public record TeamSearchCondition(
        @RequestParam(value = "scaleName", required = false) List<String> scaleName,
        @RequestParam(value = "cityName", required = false) List<String> cityName,
        @RequestParam(value = "teamStateName", required = false) List<String> teamStateName) {
    public boolean isDefault() {
        return (scaleName == null || scaleName.isEmpty())
                && (cityName == null || cityName.isEmpty())
                && (teamStateName == null || teamStateName.isEmpty());
    }
}
