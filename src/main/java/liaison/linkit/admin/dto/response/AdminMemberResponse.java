package liaison.linkit.admin.dto.response;

import liaison.linkit.admin.domain.AdminMember;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public class AdminMemberResponse {
    private final Long id;
    private final String username;
    private final String adminType;

    public static AdminMemberResponse from(final AdminMember adminMember) {
        return new AdminMemberResponse(
                adminMember.getId(),
                adminMember.getUsername(),
                adminMember.getAdminType().name()
        );
    }
}
