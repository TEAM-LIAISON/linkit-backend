//package liaison.linkit.profile.domain.repository.attach;
//
//import liaison.linkit.profile.domain.attach.AttachFile;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//
//import java.util.List;
//
//public interface AttachFileRepository extends JpaRepository<AttachFile, Long> {
//    boolean existsByProfileId(final Long profileId);
//
//    AttachFile findByProfileId(@Param("profileId") final Long profileId);
//
//    @Query("SELECT attachFile FROM AttachFile attachFile WHERE attachFile.profile.id = :profileId")
//    List<AttachFile> findAllByProfileId(@Param("profileId") Long profileId);
//}
