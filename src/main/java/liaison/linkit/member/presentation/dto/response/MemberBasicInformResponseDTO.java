package liaison.linkit.member.presentation.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberBasicInformResponseDTO {
    
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberBasicInformDetail {
        private Long memberBasicInformId;
        private String memberName;
        private String contact;
        private String email;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateMemberBasicInformResponse {
        private Long memberBasicInformId;
        private String memberName;
        private String contact;
        private String email;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateConsentServiceUseResponse {
        private Long memberBasicInformId;
        private Boolean isServiceUseAgree;
        private Boolean isPrivateInformAgree;
        private Boolean isAgeCheck;
        private Boolean isMarketingAgree;
    }

}
