package liaison.linkit.profile.business;

import java.util.List;
import java.util.stream.Collectors;
import liaison.linkit.common.annotation.Mapper;
import liaison.linkit.profile.domain.Profile;
import liaison.linkit.profile.domain.education.ProfileEducation;
import liaison.linkit.profile.presentation.education.dto.ProfileEducationRequestDTO;
import liaison.linkit.profile.presentation.education.dto.ProfileEducationResponseDTO;
import liaison.linkit.profile.presentation.education.dto.ProfileEducationResponseDTO.AddProfileEducationResponse;
import liaison.linkit.profile.presentation.education.dto.ProfileEducationResponseDTO.ProfileEducationItem;
import liaison.linkit.profile.presentation.education.dto.ProfileEducationResponseDTO.UpdateProfileEducationResponse;

@Mapper
public class ProfileEducationMapper {
    public ProfileEducation toAddProfileEducation(final Profile profile, final ProfileEducationRequestDTO.AddProfileEducationRequest request) {
        return ProfileEducation
                .builder()
                .id(null)
                .profile(profile)
                .majorName(request.getMajorName())
                .admissionYear(request.getAdmissionYear())
                .graduationYear(request.getGraduationYear())
                .isAttendUniversity(request.getIsAttendUniversity())
                .educationDescription(request.getEducationDescription())
                .isEducationCertified(false)
                .isEducationVerified(false)
                .educationCertificationAttachFileName(null)
                .educationCertificationAttachFilePath(null)
                .build();
    }

    public AddProfileEducationResponse toAddProfileEducationResponse(final ProfileEducation profileEducation) {
        return AddProfileEducationResponse
                .builder()
                .profileEducationId(profileEducation.getId())
                .universityName(profileEducation.getUniversity().getUniversityName())
                .majorName(profileEducation.getMajorName())
                .admissionYear(profileEducation.getAdmissionYear())
                .graduationYear(profileEducation.getGraduationYear())
                .isAttendUniversity(profileEducation.isAttendUniversity())
                .educationDescription(profileEducation.getEducationDescription())
                .build();
    }

    public UpdateProfileEducationResponse toUpdateProfileEducationResponse(final ProfileEducation profileEducation) {
        return UpdateProfileEducationResponse
                .builder()
                .profileEducationId(profileEducation.getId())
                .universityName(profileEducation.getUniversity().getUniversityName())
                .majorName(profileEducation.getMajorName())
                .admissionYear(profileEducation.getAdmissionYear())
                .graduationYear(profileEducation.getGraduationYear())
                .isAttendUniversity(profileEducation.isAttendUniversity())
                .educationDescription(profileEducation.getEducationDescription())
                .build();
    }

    public ProfileEducationResponseDTO.ProfileEducationItem toProfileEducationItem(final ProfileEducation profileEducation) {
        return ProfileEducationResponseDTO.ProfileEducationItem.builder()
                .profileEducationId(profileEducation.getId())
                .universityName(profileEducation.getUniversity().getUniversityName())
                .majorName(profileEducation.getMajorName())
                .admissionYear(profileEducation.getAdmissionYear())
                .graduationYear(profileEducation.getGraduationYear())
                .isAttendUniversity(profileEducation.isAttendUniversity())
                .isEducationVerified(profileEducation.isEducationVerified())
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

    public ProfileEducationResponseDTO.ProfileEducationDetail toProfileEducationDetail(final ProfileEducation profileEducation) {
        return ProfileEducationResponseDTO.ProfileEducationDetail
                .builder()
                .profileEducationId(profileEducation.getId())
                .universityName(profileEducation.getUniversity().getUniversityName())
                .majorName(profileEducation.getMajorName())
                .admissionYear(profileEducation.getAdmissionYear())
                .graduationYear(profileEducation.getGraduationYear())
                .isAttendUniversity(profileEducation.isAttendUniversity())
                .educationDescription(profileEducation.getEducationDescription())
                .isEducationCertified(profileEducation.isEducationCertified())
                .isEducationVerified(profileEducation.isEducationVerified())
                .educationCertificationAttachFileName(profileEducation.getEducationCertificationAttachFileName())
                .educationCertificationAttachFilePath(profileEducation.getEducationCertificationAttachFilePath())
                .build();
    }

    public ProfileEducationResponseDTO.ProfileEducationCertificationResponse toAddProfileEducationCertification(final ProfileEducation profileEducation) {
        return ProfileEducationResponseDTO.ProfileEducationCertificationResponse
                .builder()
                .isEducationCertified(profileEducation.isEducationCertified())
                .isEducationVerified(profileEducation.isEducationVerified())
                .educationCertificationAttachFileName(profileEducation.getEducationCertificationAttachFileName())
                .educationCertificationAttachFilePath(profileEducation.getEducationCertificationAttachFilePath())
                .build();
    }


    public ProfileEducationResponseDTO.RemoveProfileEducationCertificationResponse toRemoveProfileEducationCertification(final Long profileEducationId) {
        return ProfileEducationResponseDTO.RemoveProfileEducationCertificationResponse
                .builder()
                .profileEducationId(profileEducationId)
                .build();
    }


    public ProfileEducationResponseDTO.RemoveProfileEducationResponse toRemoveProfileEducation(final Long profileEducationId) {
        return ProfileEducationResponseDTO.RemoveProfileEducationResponse
                .builder()
                .profileEducationId(profileEducationId)
                .build();
    }
}
