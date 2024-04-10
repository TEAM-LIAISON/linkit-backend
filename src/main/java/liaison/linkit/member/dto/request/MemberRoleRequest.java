package liaison.linkit.member.dto.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MemberRoleRequest {
    private final Long id;
    private final String roleName;
}
