package liaison.linkit.member.domain.repository;


import liaison.linkit.member.domain.MemberRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface MemberRoleRepository extends JpaRepository<MemberRole, Long> {

    MemberRole findMemberRolesByRoleName(@Param("roleName") final String roleName);
}
