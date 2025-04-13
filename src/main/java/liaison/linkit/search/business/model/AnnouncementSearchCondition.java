package liaison.linkit.search.business.model;

import java.util.List;

import liaison.linkit.search.sortType.AnnouncementSortType;
import org.springframework.web.bind.annotation.RequestParam;

public record AnnouncementSearchCondition(
        @RequestParam(value = "subPosition", required = false) List<String> subPosition,
        @RequestParam(value = "cityName", required = false) List<String> cityName,
        @RequestParam(value = "projectType", required = false) List<String> projectType,
        @RequestParam(value = "workType", required = false) List<String> workType,
        @RequestParam(value = "sortBy", defaultValue = "LATEST") AnnouncementSortType sortType) {
    public boolean isDefault() {
        return (subPosition == null || subPosition.isEmpty())
                && (cityName == null || cityName.isEmpty())
                && (projectType == null || projectType.isEmpty())
                && (workType == null || workType.isEmpty())
                && (sortType == null || sortType == AnnouncementSortType.LATEST);
    }
}
