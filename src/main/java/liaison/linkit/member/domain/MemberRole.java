package liaison.linkit.member.domain;

import jakarta.persistence.*;
import liaison.linkit.member.dto.request.MemberRoleRequest;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberRole {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "member_role_id")
    private Long id;

    @Column(nullable = false, length = 50)
    private String roleName;
}
