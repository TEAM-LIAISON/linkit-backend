//package liaison.linkit.admin.domain.repository;
//
//import liaison.linkit.admin.domain.AdminMember;
//import liaison.linkit.admin.domain.type.AdminType;
//import org.springframework.data.jpa.repository.JpaRepository;
//
//import java.util.Optional;
//
//public interface AdminMemberRepository extends JpaRepository<AdminMember, Long> {
//    Optional<AdminMember> findByUsername(String username);
//
//    Boolean existsByIdAndAdminType(Long id, AdminType adminType);
//
//    Boolean existsByUsername(String username);
//}
