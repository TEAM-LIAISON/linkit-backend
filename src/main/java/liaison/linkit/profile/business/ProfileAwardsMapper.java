package liaison.linkit.profile.business;

import java.util.List;
import java.util.stream.Collectors;
import liaison.linkit.common.annotation.Mapper;
import liaison.linkit.profile.domain.profile.Profile;
import liaison.linkit.profile.domain.awards.ProfileAwards;
import liaison.linkit.profile.presentation.awards.dto.ProfileAwardsRequestDTO;
import liaison.linkit.profile.presentation.awards.dto.ProfileAwardsResponseDTO;
import liaison.linkit.profile.presentation.awards.dto.ProfileAwardsResponseDTO.AddProfileAwardsResponse;
import liaison.linkit.profile.presentation.awards.dto.ProfileAwardsResponseDTO.ProfileAwardsItem;
import liaison.linkit.profile.presentation.awards.dto.ProfileAwardsResponseDTO.UpdateProfileAwardsResponse;

@Mapper
public class ProfileAwardsMapper {

    public ProfileAwards toAddProfileAwards(final Profile profile, final ProfileAwardsRequestDTO.AddProfileAwardsRequest request) {
        return ProfileAwards
                .builder()
                .id(null)
                .profile(profile)
                .awardsName(request.getAwardsName())
                .awardsRanking(request.getAwardsRanking())
                .awardsDate(request.getAwardsDate())
                .awardsOrganizer(request.getAwardsOrganizer())
                .awardsDescription(request.getAwardsDescription())
                .isAwardsCertified(false)
                .isAwardsVerified(false)
                .awardsCertificationAttachFileName(null)
                .awardsCertificationAttachFilePath(null)
                .build();
    }

    public AddProfileAwardsResponse toAddProfileAwardsResponse(final ProfileAwards profileAwards) {
        return AddProfileAwardsResponse
                .builder()
                .profileAwardsId(profileAwards.getId())
                .awardsName(profileAwards.getAwardsName())
                .awardsRanking(profileAwards.getAwardsRanking())
                .awardsDate(profileAwards.getAwardsDate())
                .awardsOrganizer(profileAwards.getAwardsOrganizer())
                .awardsDescription(profileAwards.getAwardsDescription())
                .build();
    }

    public UpdateProfileAwardsResponse toUpdateProfileAwardsResponse(final ProfileAwards profileAwards) {
        return UpdateProfileAwardsResponse
                .builder()
                .profileAwardsId(profileAwards.getId())
                .awardsName(profileAwards.getAwardsName())
                .awardsRanking(profileAwards.getAwardsRanking())
                .awardsDate(profileAwards.getAwardsDate())
                .awardsOrganizer(profileAwards.getAwardsOrganizer())
                .awardsDescription(profileAwards.getAwardsDescription())
                .build();
    }

    public ProfileAwardsResponseDTO.ProfileAwardsItem toProfileAwardsItem(final ProfileAwards profileAwards) {
        return ProfileAwardsResponseDTO.ProfileAwardsItem.builder()
                .profileAwardsId(profileAwards.getId())
                .awardsName(profileAwards.getAwardsName())
                .awardsRanking(profileAwards.getAwardsRanking())
                .awardsDate(profileAwards.getAwardsDate())
                .isAwardsVerified(profileAwards.isAwardsVerified())
                .awardsDescription(profileAwards.getAwardsDescription())
                .build();
    }

    public ProfileAwardsResponseDTO.ProfileAwardsItems toProfileAwardsItems(final List<ProfileAwards> profileAwardsGroup) {
        List<ProfileAwardsItem> items = profileAwardsGroup.stream()
                .map(this::toProfileAwardsItem)
                .collect(Collectors.toList());

        return ProfileAwardsResponseDTO.ProfileAwardsItems.builder()
                .profileAwardsItems(items)
                .build();
    }


    public ProfileAwardsResponseDTO.ProfileAwardsDetail toProfileAwardsDetail(final ProfileAwards profileAwards) {
        return ProfileAwardsResponseDTO.ProfileAwardsDetail
                .builder()
                .profileAwardsId(profileAwards.getId())
                .awardsName(profileAwards.getAwardsName())
                .awardsRanking(profileAwards.getAwardsRanking())
                .awardsDate(profileAwards.getAwardsDate())
                .awardsOrganizer(profileAwards.getAwardsOrganizer())
                .awardsDescription(profileAwards.getAwardsDescription())
                .isAwardsCertified(profileAwards.isAwardsCertified())
                .isAwardsVerified(profileAwards.isAwardsVerified())
                .awardsCertificationAttachFileName(profileAwards.getAwardsCertificationAttachFileName())
                .awardsCertificationAttachFilePath(profileAwards.getAwardsCertificationAttachFilePath())
                .build();
    }

    public ProfileAwardsResponseDTO.ProfileAwardsCertificationResponse toAddProfileAwardsCertification(final ProfileAwards profileAwards) {
        return ProfileAwardsResponseDTO.ProfileAwardsCertificationResponse
                .builder()
                .isAwardsCertified(profileAwards.isAwardsCertified())
                .isAwardsVerified(profileAwards.isAwardsVerified())
                .awardsCertificationAttachFileName(profileAwards.getAwardsCertificationAttachFileName())
                .awardsCertificationAttachFilePath(profileAwards.getAwardsCertificationAttachFilePath())
                .build();
    }


    public ProfileAwardsResponseDTO.RemoveProfileAwardsCertificationResponse toRemoveProfileAwardsCertification(final Long profileAwardsId) {
        return ProfileAwardsResponseDTO.RemoveProfileAwardsCertificationResponse
                .builder()
                .profileAwardsId(profileAwardsId)
                .build();
    }

    public ProfileAwardsResponseDTO.RemoveProfileAwardsResponse toRemoveProfileAwards(final Long profileAwardsId) {
        return ProfileAwardsResponseDTO.RemoveProfileAwardsResponse
                .builder()
                .profileAwardsId(profileAwardsId)
                .build();
    }

    public List<ProfileAwardsItem> profileEducationsToProfileProfileEducationItems(final List<ProfileAwards> profileAwardsGroup) {
        return profileAwardsGroup.stream()
                .map(this::toProfileAwardsItem)
                .collect(Collectors.toList());
    }
}
