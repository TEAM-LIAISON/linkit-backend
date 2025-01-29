package liaison.linkit.profile.domain.repository.education;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import liaison.linkit.common.domain.University;
import liaison.linkit.profile.domain.education.ProfileEducation;
import liaison.linkit.profile.domain.education.QProfileEducation;
import liaison.linkit.profile.presentation.education.dto.ProfileEducationRequestDTO.UpdateProfileEducationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProfileEducationCustomRepositoryImpl implements ProfileEducationCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @PersistenceContext
    private EntityManager entityManager; // EntityManager 주입

    @Override
    public List<ProfileEducation> getProfileEducations(final Long profileId) {
        QProfileEducation qProfileEducation = QProfileEducation.profileEducation;

        return jpaQueryFactory
                .selectFrom(qProfileEducation)
                .where(qProfileEducation.profile.id.eq(profileId))
                .fetch();
    }

    @Override
    public ProfileEducation updateProfileEducation(
            final Long profileEducationId,
            final University university,
            final UpdateProfileEducationRequest updateProfileEducationRequest
    ) {
        QProfileEducation qProfileEducation = QProfileEducation.profileEducation;

        // 프로필 활동 업데이트
        long updatedCount = jpaQueryFactory
                .update(qProfileEducation)
                .set(qProfileEducation.university, university)
                .set(qProfileEducation.majorName, updateProfileEducationRequest.getMajorName())
                .set(qProfileEducation.admissionYear, updateProfileEducationRequest.getAdmissionYear())
                .set(qProfileEducation.graduationYear, updateProfileEducationRequest.getGraduationYear())
                .set(qProfileEducation.isAttendUniversity, updateProfileEducationRequest.getIsAttendUniversity())
                .set(qProfileEducation.educationDescription, updateProfileEducationRequest.getEducationDescription())
                .where(qProfileEducation.id.eq(profileEducationId))
                .execute();

        entityManager.flush();
        entityManager.clear();

        if (updatedCount > 0) {
            // 업데이트된 ProfileActivity 조회 및 반환
            return jpaQueryFactory
                    .selectFrom(qProfileEducation)
                    .where(qProfileEducation.id.eq(profileEducationId))
                    .fetchOne();
        } else {
            return null;
        }
    }

    @Override
    public boolean existsByProfileId(final Long profileId) {
        QProfileEducation qProfileEducation = QProfileEducation.profileEducation;

        return jpaQueryFactory
                .selectOne()
                .from(qProfileEducation)
                .where(qProfileEducation.profile.id.eq(profileId))
                .fetchFirst() != null;
    }

    @Override
    public void removeProfileEducationsByProfileId(final Long profileId) {
        QProfileEducation qProfileEducation = QProfileEducation.profileEducation;

        jpaQueryFactory
                .delete(qProfileEducation)
                .where(qProfileEducation.profile.id.eq(profileId))
                .execute();

        entityManager.flush();
        entityManager.clear();
    }
}
