package liaison.linkit.profile.domain.repository;

import liaison.linkit.profile.domain.education.Education;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EducationRepository extends JpaRepository<Education, Long> {

    boolean existsByProfileId(Long educationId);

    Education findByProfileId(@Param("profileId") final Long profileId);

    @Query("SELECT education FROM Education education WHERE education.profile.id = :profileId")
    List<Education> findAllByProfileId(@Param("profileId") final Long profileId);
}
