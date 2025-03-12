package liaison.linkit.profile.domain.repository.license;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import com.querydsl.jpa.impl.JPAQueryFactory;
import liaison.linkit.profile.domain.license.ProfileLicense;
import liaison.linkit.profile.domain.license.QProfileLicense;
import liaison.linkit.profile.presentation.license.dto.ProfileLicenseRequestDTO.UpdateProfileLicenseRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProfileLicenseCustomRepositoryImpl implements ProfileLicenseCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @PersistenceContext private EntityManager entityManager; // EntityManager 주입

    @Override
    public List<ProfileLicense> getProfileLicenses(final Long memberId) {
        QProfileLicense qProfileLicense = QProfileLicense.profileLicense;

        return jpaQueryFactory
                .selectFrom(qProfileLicense)
                .where(qProfileLicense.profile.member.id.eq(memberId))
                .fetch();
    }

    @Override
    public ProfileLicense updateProfileLicense(
            final Long profileLicenseId, final UpdateProfileLicenseRequest updateProfileLicense) {
        QProfileLicense qProfileLicense = QProfileLicense.profileLicense;

        // 프로필 활동 업데이트
        long updatedCount =
                jpaQueryFactory
                        .update(qProfileLicense)
                        .set(qProfileLicense.licenseName, updateProfileLicense.getLicenseName())
                        .set(
                                qProfileLicense.licenseInstitution,
                                updateProfileLicense.getLicenseInstitution())
                        .set(
                                qProfileLicense.licenseAcquisitionDate,
                                updateProfileLicense.getLicenseAcquisitionDate())
                        .set(
                                qProfileLicense.licenseDescription,
                                updateProfileLicense.getLicenseDescription())
                        .where(qProfileLicense.id.eq(profileLicenseId))
                        .execute();

        entityManager.flush();
        entityManager.clear();

        if (updatedCount > 0) {
            // 업데이트된 ProfileLicense 조회 및 반환
            return jpaQueryFactory
                    .selectFrom(qProfileLicense)
                    .where(qProfileLicense.id.eq(profileLicenseId))
                    .fetchOne();
        } else {
            // 업데이트된 행이 없다면 null 또는 예외 처리
            return null;
        }
    }

    @Override
    public boolean existsByProfileId(final Long profileId) {
        QProfileLicense qProfileLicense = QProfileLicense.profileLicense;

        return jpaQueryFactory
                        .selectOne()
                        .from(qProfileLicense)
                        .where(qProfileLicense.profile.id.eq(profileId))
                        .fetchFirst()
                != null;
    }

    @Override
    public void removeProfileLicensesByProfileId(final Long profileId) {
        QProfileLicense qProfileLicense = QProfileLicense.profileLicense;

        jpaQueryFactory
                .delete(qProfileLicense)
                .where(qProfileLicense.profile.id.eq(profileId))
                .execute();

        entityManager.flush();
        entityManager.clear();
    }
}
