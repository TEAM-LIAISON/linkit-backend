package liaison.linkit.search.business.model;

import java.util.List;

import org.springframework.web.bind.annotation.RequestParam;

public record ProfileSearchCondition(
        @RequestParam(required = false) List<String> subPosition,
        @RequestParam(required = false) List<String> cityName,
        @RequestParam(required = false) List<String> profileStateName) {
    public boolean isDefault() {
        return (subPosition == null || subPosition.isEmpty())
                && (cityName == null || cityName.isEmpty())
                && (profileStateName == null || profileStateName.isEmpty());
    }
}
