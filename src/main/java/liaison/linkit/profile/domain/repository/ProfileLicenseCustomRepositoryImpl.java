package liaison.linkit.profile.domain.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import liaison.linkit.profile.domain.QProfileLicense;
import liaison.linkit.profile.presentation.license.dto.ProfileLicenseResponseDTO.ProfileLicenseItem;
import liaison.linkit.profile.presentation.license.dto.ProfileLicenseResponseDTO.ProfileLicenseItems;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProfileLicenseCustomRepositoryImpl implements ProfileLicenseCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public ProfileLicenseItems findProfileLicenseListDTO(final Long memberId) {
        QProfileLicense qProfileLicense = QProfileLicense.profileLicense;

        List<ProfileLicenseItem> profileLicenseItems =
                queryFactory
                        .select(
                                Projections.constructor(
                                        ProfileLicenseItem.class,
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

        return ProfileLicenseItems
                .builder()
                .profileLicenseItems(profileLicenseItems)
                .build();
    }
}
