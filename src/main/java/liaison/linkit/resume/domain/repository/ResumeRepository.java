package liaison.linkit.resume.domain.repository;

import liaison.linkit.resume.domain.Resume;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResumeRepository extends JpaRepository<Resume, Long> {


}
