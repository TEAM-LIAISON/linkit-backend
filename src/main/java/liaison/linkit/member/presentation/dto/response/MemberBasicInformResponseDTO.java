package liaison.linkit.member.presentation.dto.response;

import java.time.LocalDateTime;
import liaison.linkit.member.domain.type.Platform;
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
        private String emailId;
        private Boolean isServiceUseAgree;
        private Boolean isPrivateInformAgree;
        private Boolean isAgeCheck;
        private Boolean isMarketingAgree;
        private Platform platform;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateMemberBasicInformResponse {
        private Long memberBasicInformId;
        private String memberName;
        private String emailId;
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

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateMemberNameResponse {
        private String memberName;
    }


    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateMemberContactResponse {
        private String contact;
    }


    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateConsentMarketingResponse {
        private Boolean isMarketingAgree;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MailReAuthenticationResponse {
        private LocalDateTime reAuthenticationEmailSendAt;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MailVerificationResponse {
        private String changedEmail;
        private LocalDateTime verificationSuccessAt;
    }
}
