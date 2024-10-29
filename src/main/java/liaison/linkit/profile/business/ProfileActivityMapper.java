package liaison.linkit.profile.business;

import java.util.List;
import java.util.stream.Collectors;
import liaison.linkit.common.annotation.Mapper;
import liaison.linkit.profile.domain.ProfileActivity;
import liaison.linkit.profile.presentation.activity.dto.ProfileActivityResponseDTO;
import liaison.linkit.profile.presentation.activity.dto.ProfileActivityResponseDTO.ProfileActivityItem;

@Mapper
public class ProfileActivityMapper {

    public ProfileActivityResponseDTO.ProfileActivityItem toProfileActivityItem(final ProfileActivity profileActivity) {
        return ProfileActivityResponseDTO.ProfileActivityItem.builder()
                .profileActivityId(profileActivity.getId())
                .activityName(profileActivity.getActivityName())
                .activityRole(profileActivity.getActivityRole())
                .activityStartDate(profileActivity.getActivityStartDate())
                .activityEndDate(profileActivity.getActivityEndDate())
                .build();
    }


    public ProfileActivityResponseDTO.ProfileActivityItems toProfileActivityItems(final List<ProfileActivity> profileActivities) {
        List<ProfileActivityItem> items = profileActivities.stream()
                .map(this::toProfileActivityItem)
                .collect(Collectors.toList());

        return ProfileActivityResponseDTO.ProfileActivityItems.builder()
                .profileActivityItems(items)
                .build();
    }
}
