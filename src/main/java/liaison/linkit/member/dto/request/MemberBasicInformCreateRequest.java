package liaison.linkit.member.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class MemberBasicInformCreateRequest {

    private final String profileImageName;

    @NotNull(message = "성함을 입력해주세요")
    private final String memberName;

    @NotNull(message = "연락처를 입력해주세요")
    private final String contact;

    @NotNull(message = "직무 및 역할을 입력해주세요")
    private final String roleName;

    private final boolean marketingAgree;

    public MemberBasicInformCreateRequest(
            final String profileImageName,
            final String memberName,
            final String contact,
            final String roleName,
            boolean marketingAgree
    ) {
        this.profileImageName = profileImageName;
        this.memberName = memberName;
        this.contact = contact;
        this.roleName = roleName;
        this.marketingAgree = marketingAgree;
    }
}
