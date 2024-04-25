package liaison.linkit.member.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberRole {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "member_role_id")
    private Long id;

    @Column(nullable = false, length = 50)
    private String roleName;
}
