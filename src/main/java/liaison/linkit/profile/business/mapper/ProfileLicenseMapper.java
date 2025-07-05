package liaison.linkit.profile.business.mapper;

import java.util.List;
import java.util.stream.Collectors;

import liaison.linkit.common.annotation.Mapper;
import liaison.linkit.profile.domain.license.ProfileLicense;
import liaison.linkit.profile.domain.profile.Profile;
import liaison.linkit.profile.presentation.license.dto.ProfileLicenseRequestDTO;
import liaison.linkit.profile.presentation.license.dto.ProfileLicenseResponseDTO;
import liaison.linkit.profile.presentation.license.dto.ProfileLicenseResponseDTO.AddProfileLicenseResponse;
import liaison.linkit.profile.presentation.license.dto.ProfileLicenseResponseDTO.ProfileLicenseItem;
import liaison.linkit.profile.presentation.license.dto.ProfileLicenseResponseDTO.UpdateProfileLicenseResponse;

@Mapper
public class ProfileLicenseMapper {
    public ProfileLicense toAddProfileLicense(
            final Profile profile,
            final ProfileLicenseRequestDTO.AddProfileLicenseRequest request) {
        return ProfileLicense.builder()
                .id(null)
                .profile(profile)
                .licenseName(request.getLicenseName())
                .licenseInstitution(request.getLicenseInstitution())
                .licenseAcquisitionDate(request.getLicenseAcquisitionDate())
                .licenseDescription(request.getLicenseDescription())
                .isLicenseCertified(false)
                .isLicenseVerified(false)
                .licenseCertificationAttachFileName(null)
                .licenseCertificationAttachFilePath(null)
                .build();
    }

    public AddProfileLicenseResponse toAddProfileLicenseResponse(
            final ProfileLicense profileLicense) {
        return AddProfileLicenseResponse.builder()
                .profileLicenseId(profileLicense.getId())
                .licenseName(profileLicense.getLicenseName())
                .licenseInstitution(profileLicense.getLicenseInstitution())
                .licenseAcquisitionDate(profileLicense.getLicenseAcquisitionDate())
                .licenseDescription(profileLicense.getLicenseDescription())
                .build();
    }

    public UpdateProfileLicenseResponse toUpdateProfileLicenseResponse(
            final ProfileLicense profileLicense) {
        return UpdateProfileLicenseResponse.builder()
                .profileLicenseId(profileLicense.getId())
                .licenseName(profileLicense.getLicenseName())
                .licenseInstitution(profileLicense.getLicenseInstitution())
                .licenseAcquisitionDate(profileLicense.getLicenseAcquisitionDate())
                .licenseDescription(profileLicense.getLicenseDescription())
                .build();
    }

    public ProfileLicenseResponseDTO.ProfileLicenseItem toProfileLicenseItem(
            final ProfileLicense profileLicense) {
        return ProfileLicenseResponseDTO.ProfileLicenseItem.builder()
                .profileLicenseId(profileLicense.getId())
                .licenseName(profileLicense.getLicenseName())
                .licenseInstitution(profileLicense.getLicenseInstitution())
                .licenseAcquisitionDate(profileLicense.getLicenseAcquisitionDate())
                .isLicenseVerified(profileLicense.isLicenseVerified())
                .licenseDescription(profileLicense.getLicenseDescription())
                .build();
    }

    public ProfileLicenseResponseDTO.ProfileLicenseItems toProfileLicenseItems(
            final List<ProfileLicense> profilelicenses) {
        List<ProfileLicenseItem> items =
                profilelicenses.stream()
                        .map(this::toProfileLicenseItem)
                        .collect(Collectors.toList());

        return ProfileLicenseResponseDTO.ProfileLicenseItems.builder()
                .profileLicenseItems(items)
                .build();
    }

    public ProfileLicenseResponseDTO.ProfileLicenseDetail toProfileLicenseDetail(
            final ProfileLicense profileLicense) {
        return ProfileLicenseResponseDTO.ProfileLicenseDetail.builder()
                .profileLicenseId(profileLicense.getId())
                .licenseName(profileLicense.getLicenseName())
                .licenseInstitution(profileLicense.getLicenseInstitution())
                .licenseAcquisitionDate(profileLicense.getLicenseAcquisitionDate())
                .licenseDescription(profileLicense.getLicenseDescription())
                .isLicenseCertified(profileLicense.isLicenseCertified())
                .isLicenseVerified(profileLicense.isLicenseVerified())
                .licenseCertificationAttachFileName(
                        profileLicense.getLicenseCertificationAttachFileName())
                .licenseCertificationAttachFilePath(
                        profileLicense.getLicenseCertificationAttachFilePath())
                .build();
    }

    public ProfileLicenseResponseDTO.ProfileLicenseCertificationResponse
            toAddProfileLicenseCertification(final ProfileLicense profileLicense) {
        return ProfileLicenseResponseDTO.ProfileLicenseCertificationResponse.builder()
                .isLicenseCertified(profileLicense.isLicenseCertified())
                .isLicenseVerified(profileLicense.isLicenseVerified())
                .licenseCertificationAttachFileName(
                        profileLicense.getLicenseCertificationAttachFileName())
                .licenseCertificationAttachFilePath(
                        profileLicense.getLicenseCertificationAttachFilePath())
                .build();
    }

    public ProfileLicenseResponseDTO.RemoveProfileLicenseCertificationResponse
            toRemoveProfileLicenseCertification(final Long profileLicenseId) {
        return ProfileLicenseResponseDTO.RemoveProfileLicenseCertificationResponse.builder()
                .profileLicenseId(profileLicenseId)
                .build();
    }

    public ProfileLicenseResponseDTO.RemoveProfileLicenseResponse toRemoveProfileLicense(
            final Long profileLicenseId) {
        return ProfileLicenseResponseDTO.RemoveProfileLicenseResponse.builder()
                .profileLicenseId(profileLicenseId)
                .build();
    }

    public List<ProfileLicenseResponseDTO.ProfileLicenseItem> profileLicensesToProfileLicenseItems(
            final List<ProfileLicense> profileLicenses) {
        return profileLicenses.stream()
                .map(this::toProfileLicenseItem)
                .collect(Collectors.toList());
    }
}
