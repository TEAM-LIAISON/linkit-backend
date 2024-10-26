package liaison.linkit.login.business;

import java.time.LocalDateTime;
import liaison.linkit.common.annotation.Mapper;
import liaison.linkit.login.domain.MemberTokens;
import liaison.linkit.login.presentation.dto.AccountResponseDTO;

@Mapper
public class AccountMapper {

    public AccountResponseDTO.LoginResponse toLogin(
            final MemberTokens memberTokens,
            final String email,
            final boolean isMemberBasicInform,
            final boolean isServiceUseConsent
    ) {
        return AccountResponseDTO.LoginResponse.builder()
                .accessToken(memberTokens.getAccessToken())
                .refreshToken(memberTokens.getRefreshToken())
                .email(email)
                .isMemberBasicInform(isMemberBasicInform)
                .isServiceUseConsent(isServiceUseConsent)
                .build();
    }

    public AccountResponseDTO.LogoutResponse toLogout() {
        return AccountResponseDTO.LogoutResponse.builder()
                .logoutAt(LocalDateTime.now())
                .build();
    }

    public AccountResponseDTO.QuitAccountResponse toQuitAccount() {
        return AccountResponseDTO.QuitAccountResponse.builder()
                .quitAt(LocalDateTime.now())
                .build();
    }

    public AccountResponseDTO.RenewTokenResponse toRenewTokenResponse(final String regeneratedAccessToken) {
        return AccountResponseDTO.RenewTokenResponse.builder()
                .accessToken(regeneratedAccessToken)
                .build();
    }
}
