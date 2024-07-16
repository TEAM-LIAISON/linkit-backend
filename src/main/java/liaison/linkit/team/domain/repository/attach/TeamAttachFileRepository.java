//package liaison.linkit.team.domain.repository.attach;
//
//import liaison.linkit.team.domain.attach.TeamAttachFile;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//
//import java.util.List;
//
//public interface TeamAttachFileRepository extends JpaRepository<TeamAttachFile, Long> {
//    boolean existsByTeamProfileId(final Long teamProfileId);
//
//    @Query("SELECT teamAttachFile FROM TeamAttachFile teamAttachFile WHERE teamAttachFile.teamProfile.id = :teamProfileId")
//    List<TeamAttachFile> findAllByTeamProfileId(@Param("teamProfileId") final Long teamProfileId);
//
//}
