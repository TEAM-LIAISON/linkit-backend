package liaison.linkit.admin.domain.repository;

import liaison.linkit.admin.domain.AdminMember;
import liaison.linkit.admin.domain.type.AdminType;

import java.util.Optional;

public interface AdminMemberRepository {
    Optional<AdminMember> findByUsername(String username);

    Boolean existsByIdAndAdminType(Long id, AdminType adminType);

    Boolean existsByUsername(String username);
}
