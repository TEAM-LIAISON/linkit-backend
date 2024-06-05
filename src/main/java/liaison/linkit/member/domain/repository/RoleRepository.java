package liaison.linkit.member.domain.repository;


import liaison.linkit.member.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findMemberRolesByRoleName(@Param("roleName") final String roleName);
}
