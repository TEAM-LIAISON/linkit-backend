package liaison.linkit.admin.service;

import liaison.linkit.admin.domain.AdminMember;
import liaison.linkit.admin.domain.repository.AdminMemberRepository;
import liaison.linkit.admin.domain.type.AdminType;
import liaison.linkit.admin.dto.request.AdminLoginRequest;
import liaison.linkit.admin.infrastructure.PasswordEncoder;
import liaison.linkit.global.exception.AdminException;
import liaison.linkit.login.domain.MemberTokens;
import liaison.linkit.login.domain.repository.RefreshTokenRepository;
import liaison.linkit.login.infrastructure.BearerAuthorizationExtractor;
import liaison.linkit.login.infrastructure.JwtProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AdminLoginServiceTest {
    @InjectMocks
    private AdminLoginService adminLoginService;

    @Mock
    private AdminMemberRepository adminMemberRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private BearerAuthorizationExtractor bearerAuthorizationExtractor;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    public void testLoginSuccess() {
        // given
        final AdminLoginRequest loginRequest = new AdminLoginRequest("user", "password");
        final AdminMember adminMember = new AdminMember(1L, "user", "password", AdminType.ADMIN);
        final MemberTokens memberTokens = new MemberTokens("accessToken", "refreshToken");

        when(adminMemberRepository.findByUsername(anyString())).thenReturn(Optional.of(adminMember));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtProvider.generateLoginToken(anyString())).thenReturn(memberTokens);

        // when
        final MemberTokens result = adminLoginService.login(loginRequest);

        // then
        assertSoftly(softly -> {
            softly.assertThat(result).isNotNull();
            softly.assertThat(memberTokens.getAccessToken()).isEqualTo(result.getAccessToken());
            softly.assertThat(memberTokens.getRefreshToken()).isEqualTo(result.getRefreshToken());
        });
    }

    @Test
    public void testLoginFailure_InvalidPassword() {
        // given
        final AdminLoginRequest loginRequest = new AdminLoginRequest("user", "wrongpassword");
        final AdminMember adminMember = new AdminMember("user", "password", AdminType.ADMIN);

        when(adminMemberRepository.findByUsername(anyString())).thenReturn(Optional.of(adminMember));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);
        // when & then
        assertThrows(AdminException.class, () -> adminLoginService.login(loginRequest));
    }
}
