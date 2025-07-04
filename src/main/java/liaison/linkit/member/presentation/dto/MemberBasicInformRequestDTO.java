package liaison.linkit.member.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberBasicInformRequestDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateMemberBasicInformRequest {

        @NotBlank(message = "이름을 입력해주세요")
        private String memberName;

        @NotBlank(message = "유저 아이디를 입력해주세요")
        private String emailId;

        @NotBlank(message = "연락처를 입력해주세요")
        private String contact;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateConsentServiceUseRequest {

        @NotNull(message = "서비스 이용약관 동의를 체크해주세요")
        private Boolean isServiceUseAgree;

        @NotNull(message = "개인정보 수집 및 이용 동의를 체크해주세요")
        private Boolean isPrivateInformAgree;

        @NotNull(message = "만 14세 이상 여부를 체크해주세요")
        private Boolean isAgeCheck;

        @NotNull(message = "광고성 정보 수신 동의를 선택해주세요")
        private Boolean isMarketingAgree;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateMemberNameRequest {
        @NotNull(message = "변경하려는 이름을 입력해주세요.")
        private String memberName;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateMemberContactRequest {
        @NotNull(message = "변경하려는 전화번호를 입력해주세요")
        private String contact;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateConsentMarketingRequest {
        @NotNull(message = "광고성 정보 수신 동의를 선택해주세요")
        private Boolean isMarketingAgree;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MailReAuthenticationRequest {
        @NotNull(message = "변경하고자 하는 이메일을 입력해주세요")
        private String email;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AuthCodeVerificationRequest {

        @NotNull(message = "변경하고자 하는 이메일을 입력해주세요")
        private String changeRequestEmail;

        @NotNull(message = "이메일로 발송된 인증코드를 입력해주세요")
        private String authCode;
    }
}
