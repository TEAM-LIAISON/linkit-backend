package liaison.linkit.member.presentation.dto.request.memberBasicInform;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberBasicInformRequestDTO {

    @Getter
    public static class memberBasicInformRequest {
        private String memberName;
        private String contact;
        private boolean marketingAgree;
    }
}
