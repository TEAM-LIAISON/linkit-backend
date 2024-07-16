package liaison.linkit.profile.domain.repository.education;

import liaison.linkit.profile.domain.education.University;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UniversityRepository extends JpaRepository<University, Long> {

    @Query("SELECT u FROM University u WHERE u.universityName = :universityName")
    Optional<University> findByUniversityName(@Param("universityName") final String universityName);
}
