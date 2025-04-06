package liaison.linkit.search.business.model;

import java.util.List;

public record ProfileSearchCondition(
        List<String> subPosition, List<String> cityName, List<String> profileStateName) {
    public boolean isDefault() {
        return (subPosition == null || subPosition.isEmpty())
                && (cityName == null || cityName.isEmpty())
                && (profileStateName == null || profileStateName.isEmpty());
    }
}
