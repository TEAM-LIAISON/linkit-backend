package liaison.linkit.profile.domain.repository.education;

import liaison.linkit.profile.domain.education.Major;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MajorRepository extends JpaRepository<Major, Long> {

    @Query("SELECT m FROM Major m WHERE m.majorName = :majorName")
    Major findByMajorName(@Param("majorName") final String majorName);
}
