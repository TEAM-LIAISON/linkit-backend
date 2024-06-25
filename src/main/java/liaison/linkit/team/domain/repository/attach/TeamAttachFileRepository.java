package liaison.linkit.team.domain.repository.attach;

import liaison.linkit.team.domain.attach.TeamAttachFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamAttachFileRepository extends JpaRepository<TeamAttachFile, Long> {
    boolean existsByTeamProfileId(final Long teamProfileId);
}
