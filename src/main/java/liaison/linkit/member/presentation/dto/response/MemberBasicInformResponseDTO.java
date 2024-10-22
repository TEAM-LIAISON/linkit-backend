package liaison.linkit.member.presentation.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberBasicInformResponseDTO {

    // 회원 기본 정보에 대한 요청 (생성 및 수정)
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RequestMemberBasicInform {
        private String memberName;
        private String contact;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberBasicInformDetail {
        private Long memberBasicInformId;
        private String memberName;
        private String contact;
        private String email;
        private boolean marketingAgree;
    }
}
