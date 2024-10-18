package liaison.linkit.profile.domain.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import liaison.linkit.profile.domain.QProfileLicense;
import liaison.linkit.profile.presentation.license.dto.ProfileLicenseResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProfileLicenseCustomRepositoryImpl implements ProfileLicenseCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public ProfileLicenseResponseDTO.ProfileLicenseList findProfileLicenseListDTO(final Long memberId) {
        QProfileLicense qProfileLicense = QProfileLicense.profileLicense;

        List<ProfileLicenseResponseDTO.ProfileLicenseListItem> profileLicenseListItems =
                queryFactory
                        .select(
                                Projections.constructor(
                                        ProfileLicenseResponseDTO.ProfileLicenseListItem.class,
                                        qProfileLicense.id,
                                        qProfileLicense.licenseName,
                                        qProfileLicense.licenseInstitution,
                                        qProfileLicense.acquisitionDate,
                                        qProfileLicense.licenseDescription,
                                        qProfileLicense.isLicenseCertified,
                                        qProfileLicense.licenseCertificationAttachFileName,
                                        qProfileLicense.licenseCertificationAttachFilePath,
                                        qProfileLicense.licenseCertificationDescription
                                ))
                        .from(qProfileLicense)
                        .where(qProfileLicense.profile.member.id.eq(memberId))
                        .fetch();

        return ProfileLicenseResponseDTO.ProfileLicenseList
                .builder()
                .profileLicenseList(profileLicenseListItems)
                .build();
    }
}
