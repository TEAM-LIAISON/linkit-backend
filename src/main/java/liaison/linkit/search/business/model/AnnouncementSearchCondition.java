package liaison.linkit.search.business.model;

import java.util.List;

import liaison.linkit.search.sortType.AnnouncementSortType;
import org.springframework.web.bind.annotation.RequestParam;

public record AnnouncementSearchCondition(
        @RequestParam(value = "subPosition", required = false) List<String> subPosition,
        @RequestParam(value = "cityName", required = false) List<String> cityName,
        @RequestParam(value = "projectTypeName", required = false) List<String> projectTypeName,
        @RequestParam(value = "workTypeName", required = false) List<String> workTypeName,
        @RequestParam(value = "sortBy", defaultValue = "LATEST") AnnouncementSortType sortType) {
    public boolean isDefault() {
        return (subPosition == null || subPosition.isEmpty())
                && (cityName == null || cityName.isEmpty())
                && (projectTypeName == null || projectTypeName.isEmpty())
                && (workTypeName == null || workTypeName.isEmpty())
                && (sortType == null || sortType == AnnouncementSortType.LATEST);
    }
}
