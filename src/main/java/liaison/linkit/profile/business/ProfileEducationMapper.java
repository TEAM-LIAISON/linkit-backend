package liaison.linkit.profile.business;

import java.util.List;
import java.util.stream.Collectors;
import liaison.linkit.common.annotation.Mapper;
import liaison.linkit.profile.domain.ProfileEducation;
import liaison.linkit.profile.presentation.education.dto.ProfileEducationResponseDTO;
import liaison.linkit.profile.presentation.education.dto.ProfileEducationResponseDTO.ProfileEducationItem;

@Mapper
public class ProfileEducationMapper {


    public ProfileEducationResponseDTO.ProfileEducationItem toProfileEducationItem(final ProfileEducation profileEducation) {
        return ProfileEducationResponseDTO.ProfileEducationItem.builder()
                .profileEducationId(profileEducation.getId())

                .build();
    }

    public ProfileEducationResponseDTO.ProfileEducationItems toProfileEducationItems(final List<ProfileEducation> profileEducations) {
        List<ProfileEducationItem> items = profileEducations.stream()
                .map(this::toProfileEducationItem)
                .collect(Collectors.toList());

        return ProfileEducationResponseDTO.ProfileEducationItems.builder()
                .profileEducationItems(items)
                .build();
    }
}
