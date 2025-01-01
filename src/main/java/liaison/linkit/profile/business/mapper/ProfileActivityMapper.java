package liaison.linkit.profile.business.mapper;

import java.util.List;
import java.util.stream.Collectors;
import liaison.linkit.common.annotation.Mapper;
import liaison.linkit.profile.domain.profile.Profile;
import liaison.linkit.profile.domain.activity.ProfileActivity;
import liaison.linkit.profile.presentation.activity.dto.ProfileActivityRequestDTO;
import liaison.linkit.profile.presentation.activity.dto.ProfileActivityResponseDTO;
import liaison.linkit.profile.presentation.activity.dto.ProfileActivityResponseDTO.AddProfileActivityResponse;
import liaison.linkit.profile.presentation.activity.dto.ProfileActivityResponseDTO.ProfileActivityItem;
import liaison.linkit.profile.presentation.activity.dto.ProfileActivityResponseDTO.UpdateProfileActivityResponse;

@Mapper
public class ProfileActivityMapper {

    public ProfileActivity toAddProfileActivity(final Profile profile, final ProfileActivityRequestDTO.AddProfileActivityRequest request) {
        return ProfileActivity
                .builder()
                .id(null)
                .profile(profile)
                .activityName(request.getActivityName())
                .activityRole(request.getActivityRole())
                .activityStartDate(request.getActivityStartDate())
                .activityEndDate(request.getActivityEndDate())
                .isActivityInProgress(request.getIsActivityInProgress())
                .activityDescription(request.getActivityDescription())
                .isActivityCertified(false)
                .isActivityVerified(false)
                .activityCertificationAttachFileName(null)
                .activityCertificationAttachFilePath(null)
                .build();
    }

    public AddProfileActivityResponse toAddProfileActivityResponse(final ProfileActivity profileActivity) {
        return AddProfileActivityResponse
                .builder()
                .profileActivityId(profileActivity.getId())
                .activityName(profileActivity.getActivityName())
                .activityRole(profileActivity.getActivityRole())
                .activityStartDate(profileActivity.getActivityStartDate())
                .activityEndDate(profileActivity.getActivityEndDate())
                .isActivityInProgress(profileActivity.isActivityInProgress())
                .activityDescription(profileActivity.getActivityDescription())
                .build();
    }

    public UpdateProfileActivityResponse toUpdateProfileActivityResponse(final ProfileActivity profileActivity) {
        return UpdateProfileActivityResponse
                .builder()
                .profileActivityId(profileActivity.getId())
                .activityName(profileActivity.getActivityName())
                .activityRole(profileActivity.getActivityRole())
                .activityStartDate(profileActivity.getActivityStartDate())
                .activityEndDate(profileActivity.getActivityEndDate())
                .isActivityInProgress(profileActivity.isActivityInProgress())
                .activityDescription(profileActivity.getActivityDescription())
                .build();
    }

    public ProfileActivityResponseDTO.ProfileActivityItem toProfileActivityItem(final ProfileActivity profileActivity) {
        return ProfileActivityResponseDTO.ProfileActivityItem.builder()
                .profileActivityId(profileActivity.getId())
                .activityName(profileActivity.getActivityName())
                .activityRole(profileActivity.getActivityRole())
                .activityStartDate(profileActivity.getActivityStartDate())
                .activityEndDate(profileActivity.getActivityEndDate())
                .isActivityVerified(profileActivity.isActivityVerified())
                .activityDescription(profileActivity.getActivityDescription())
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

    public ProfileActivityResponseDTO.ProfileActivityDetail toProfileActivityDetail(final ProfileActivity profileActivity) {
        return ProfileActivityResponseDTO.ProfileActivityDetail
                .builder()
                .profileActivityId(profileActivity.getId())
                .activityName(profileActivity.getActivityName())
                .activityRole(profileActivity.getActivityRole())
                .activityStartDate(profileActivity.getActivityStartDate())
                .activityEndDate(profileActivity.getActivityEndDate())
                .isActivityInProgress(profileActivity.isActivityInProgress())
                .activityDescription(profileActivity.getActivityDescription())
                .isActivityCertified(profileActivity.isActivityCertified())
                .isActivityVerified(profileActivity.isActivityVerified())
                .activityCertificationAttachFileName(profileActivity.getActivityCertificationAttachFileName())
                .activityCertificationAttachFilePath(profileActivity.getActivityCertificationAttachFilePath())
                .build();
    }

    public ProfileActivityResponseDTO.ProfileActivityCertificationResponse toAddProfileActivityCertification(final ProfileActivity profileActivity) {
        return ProfileActivityResponseDTO.ProfileActivityCertificationResponse
                .builder()
                .isActivityCertified(profileActivity.isActivityCertified())
                .isActivityVerified(profileActivity.isActivityVerified())
                .activityCertificationAttachFileName(profileActivity.getActivityCertificationAttachFileName())
                .activityCertificationAttachFilePath(profileActivity.getActivityCertificationAttachFilePath())
                .build();
    }


    public ProfileActivityResponseDTO.RemoveProfileActivityCertificationResponse toRemoveProfileActivityCertification(final Long profileActivityId) {
        return ProfileActivityResponseDTO.RemoveProfileActivityCertificationResponse
                .builder()
                .profileActivityId(profileActivityId)
                .build();
    }


    public ProfileActivityResponseDTO.RemoveProfileActivityResponse toRemoveProfileActivity(final Long profileActivityId) {
        return ProfileActivityResponseDTO.RemoveProfileActivityResponse
                .builder()
                .profileActivityId(profileActivityId)
                .build();
    }

    public List<ProfileActivityResponseDTO.ProfileActivityItem> profileActivitiesToProfileActivityItems(final List<ProfileActivity> profileActivities) {
        return profileActivities.stream()
                .map(this::toProfileActivityItem)
                .collect(Collectors.toList());
    }
}
