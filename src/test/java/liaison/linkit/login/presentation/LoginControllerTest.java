package liaison.linkit.login.presentation;

import static liaison.linkit.global.restdocs.RestDocsConfiguration.field;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import java.time.LocalDateTime;
import liaison.linkit.common.presentation.CommonResponse;
import liaison.linkit.global.ControllerTest;
import liaison.linkit.login.domain.MemberTokens;
import liaison.linkit.login.presentation.dto.AccountRequestDTO;
import liaison.linkit.login.presentation.dto.AccountRequestDTO.AuthCodeVerificationRequest;
import liaison.linkit.login.presentation.dto.AccountRequestDTO.EmailReAuthenticationRequest;
import liaison.linkit.login.presentation.dto.AccountRequestDTO.LoginRequest;
import liaison.linkit.login.presentation.dto.AccountResponseDTO;
import liaison.linkit.login.presentation.dto.AccountResponseDTO.EmailReAuthenticationResponse;
import liaison.linkit.login.presentation.dto.AccountResponseDTO.EmailVerificationResponse;
import liaison.linkit.login.presentation.dto.AccountResponseDTO.LoginResponse;
import liaison.linkit.login.presentation.dto.AccountResponseDTO.LoginServiceResponse;
import liaison.linkit.login.presentation.dto.AccountResponseDTO.QuitAccountResponse;
import liaison.linkit.login.presentation.dto.AccountResponseDTO.RenewTokenResponse;
import liaison.linkit.login.service.LoginService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;


@WebMvcTest(LoginController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
public class LoginControllerTest extends ControllerTest {

    private final static String GOOGLE_PROVIDER = "google";
    private final static String REFRESH_TOKEN = "refreshToken";
    private final static String ACCESS_TOKEN = "accessToken";
    private final static String RENEW_ACCESS_TOKEN = "I'mNewAccessToken!";
    private final static String EMAIL = "linkit@gmail.com";

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LoginService loginService;

    @DisplayName("회원은 로그인 할 수 있다.")
    @Test
    void login() throws Exception {
        // given
        final AccountRequestDTO.LoginRequest loginRequest = new LoginRequest("code");
        final AccountResponseDTO.LoginServiceResponse loginServiceResponse
                = new LoginServiceResponse(ACCESS_TOKEN, REFRESH_TOKEN, EMAIL, false);

        final AccountResponseDTO.LoginResponse loginResponse
                = new LoginResponse(ACCESS_TOKEN, EMAIL, false);

        // when
        when(loginService.login(anyString(), anyString())).thenReturn(loginServiceResponse);

        final ResultActions resultActions = mockMvc.perform(post("/api/v1/login/{provider}", GOOGLE_PROVIDER)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest))
        );

        final MvcResult mvcResult = resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value("true"))
                .andExpect(jsonPath("$.code").value("1000"))
                .andExpect(jsonPath("$.message").value("요청에 성공하였습니다."))
                .andDo(
                        restDocs.document(
                                pathParameters(
                                        parameterWithName("provider")
                                                .description("로그인 유형 (플랫폼 영어 이름)")
                                ),
                                requestFields(
                                        fieldWithPath("code")
                                                .type(JsonFieldType.STRING)
                                                .description("인가 코드")
                                                .attributes(field("constraint", "문자열"))
                                ),
                                responseFields(
                                        fieldWithPath("isSuccess")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("요청 성공 여부")
                                                .attributes(field("constraint", "boolean 값")),
                                        fieldWithPath("code")
                                                .type(JsonFieldType.STRING)
                                                .description("요청 성공 코드")
                                                .attributes(field("constraint", "문자열")),
                                        fieldWithPath("message")
                                                .type(JsonFieldType.STRING)
                                                .description("요청 성공 메시지")
                                                .attributes(field("constraint", "문자열")),
                                        fieldWithPath("result.accessToken")
                                                .type(JsonFieldType.STRING)
                                                .description("access token")
                                                .attributes(field("constraint", "문자열(jwt)")),
                                        fieldWithPath("result.email")
                                                .type(JsonFieldType.STRING)
                                                .description("소셜 로그인 이메일")
                                                .attributes(field("constraint", "문자열")),
                                        fieldWithPath("result.isMemberBasicInform")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("기본 정보 기입 여부 (false: 기본 정보 기입하지 않음)")
                                                .attributes(field("constraint", "boolean 값"))
                                )
                        ))
                .andReturn();

        final String jsonResponse = mvcResult.getResponse().getContentAsString();
        final CommonResponse<AccountResponseDTO.LoginResponse> actual = objectMapper.readValue(
                jsonResponse,
                new TypeReference<CommonResponse<AccountResponseDTO.LoginResponse>>() {
                }
        );

        final CommonResponse<AccountResponseDTO.LoginResponse> expected = CommonResponse.onSuccess(loginResponse);

        // then
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @DisplayName("accessToken 만료가 된 경우, 토큰 재발행이 가능하다.")
    @Test
    void renewToken() throws Exception {
        // given
        final MemberTokens memberTokens = new MemberTokens(REFRESH_TOKEN, ACCESS_TOKEN);
        final Cookie cookie = new Cookie("refreshToken", memberTokens.getRefreshToken());
        final AccountResponseDTO.RenewTokenResponse renewTokenResponse = new AccountResponseDTO.RenewTokenResponse(RENEW_ACCESS_TOKEN);

        // when
        when(loginService.renewalAccessToken(anyString(), anyString())).thenReturn(renewTokenResponse);

        final ResultActions resultActions = mockMvc.perform(post("/api/v1/renew/token")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                .cookie(cookie)
        );

        final MvcResult mvcResult = resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value("true"))
                .andExpect(jsonPath("$.code").value("1000"))
                .andExpect(jsonPath("$.message").value("요청에 성공하였습니다."))
                .andDo(
                        restDocs.document(
                                requestCookies(
                                        cookieWithName("refreshToken")
                                                .description("갱신 토큰")
                                ),
                                requestHeaders(
                                        headerWithName("Authorization")
                                                .description("access token")
                                                .attributes(field("constraint", "문자열(jwt)"))
                                ),
                                responseFields(
                                        fieldWithPath("isSuccess")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("요청 성공 여부")
                                                .attributes(field("constraint", "boolean 값")),
                                        fieldWithPath("code")
                                                .type(JsonFieldType.STRING)
                                                .description("요청 성공 코드")
                                                .attributes(field("constraint", "문자열")),
                                        fieldWithPath("message")
                                                .type(JsonFieldType.STRING)
                                                .description("요청 성공 메시지")
                                                .attributes(field("constraint", "문자열")),
                                        fieldWithPath("result.accessToken")
                                                .type(JsonFieldType.STRING)
                                                .description("재발행 이후 access token")
                                                .attributes(field("constraint", "문자열(jwt)"))
                                )
                        )
                ).andReturn();

        // JSON 응답에서 result 객체를 추출 및 검증
        final String jsonResponse = mvcResult.getResponse().getContentAsString();
        final CommonResponse<RenewTokenResponse> actual = objectMapper.readValue(
                jsonResponse,
                new TypeReference<CommonResponse<RenewTokenResponse>>() {
                }
        );

        final CommonResponse<AccountResponseDTO.RenewTokenResponse> expected = CommonResponse.onSuccess(renewTokenResponse);

        // then
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @DisplayName("회원은 로그아웃 할 수 있다.")
    @Test
    void logout() throws Exception {
        // given
        given(refreshTokenRepository.existsById(any())).willReturn(true);
        doNothing().when(jwtProvider).validateTokens(any());
        given(jwtProvider.getSubject(any())).willReturn("1");
        doNothing().when(loginService).removeRefreshToken(anyString());

        final MemberTokens memberTokens = new MemberTokens(RENEW_ACCESS_TOKEN, REFRESH_TOKEN);
        final Cookie cookie = new Cookie("refreshToken", memberTokens.getRefreshToken());

        final AccountResponseDTO.LogoutResponse logoutResponse = new AccountResponseDTO.LogoutResponse(LocalDateTime.now());
        when(loginService.logout(any(), anyString())).thenReturn(logoutResponse);

        // when
        final ResultActions resultActions = mockMvc.perform(delete("/api/v1/logout")
                .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                .cookie(cookie)
        );

        final MvcResult mvcResult = resultActions
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestCookies(
                                cookieWithName("refreshToken")
                                        .description("갱신 토큰")
                        ),
                        requestHeaders(
                                headerWithName("Authorization")
                                        .description("access token")
                                        .attributes(field("constraint", "문자열(jwt)"))
                        ),
                        responseFields(
                                fieldWithPath("isSuccess")
                                        .type(JsonFieldType.BOOLEAN)
                                        .description("요청 성공 여부")
                                        .attributes(field("constraint", "boolean 값")),
                                fieldWithPath("code")
                                        .type(JsonFieldType.STRING)
                                        .description("요청 성공 코드")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("message")
                                        .type(JsonFieldType.STRING)
                                        .description("요청 성공 메시지")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("result.logoutAt")
                                        .type(JsonFieldType.STRING)
                                        .description("로그아웃 시간")
                                        .attributes(field("constraint", "LocalDateTime Type"))
                        )
                )).andReturn();

        final String jsonResponse = mvcResult.getResponse().getContentAsString();
        final CommonResponse<AccountResponseDTO.LogoutResponse> actual = objectMapper.readValue(
                jsonResponse,
                new TypeReference<CommonResponse<AccountResponseDTO.LogoutResponse>>() {
                }
        );

        final CommonResponse<AccountResponseDTO.LogoutResponse> expected = CommonResponse.onSuccess(logoutResponse);

        // then
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
        verify(loginService).logout(any(), anyString());
    }

    @DisplayName("회원은 회원탈퇴 할 수 있다.")
    @Test
    void quitAccount() throws Exception {
        // given
        given(refreshTokenRepository.existsById(any())).willReturn(true);
        doNothing().when(jwtProvider).validateTokens(any());
        given(jwtProvider.getSubject(any())).willReturn("1");

        final MemberTokens memberTokens = new MemberTokens(REFRESH_TOKEN, RENEW_ACCESS_TOKEN);
        final Cookie cookie = new Cookie("refreshToken", memberTokens.getRefreshToken());

        final AccountResponseDTO.QuitAccountResponse quitAccountResponse = new QuitAccountResponse(LocalDateTime.now());

        when(loginService.quitAccount(anyLong())).thenReturn(quitAccountResponse);

        // when
        final ResultActions resultActions = mockMvc.perform(delete("/api/v1/quit")
                .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                .cookie(cookie)
        );

        final MvcResult mvcResult = resultActions
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestCookies(
                                cookieWithName("refreshToken")
                                        .description("갱신 토큰")
                        ),
                        requestHeaders(
                                headerWithName("Authorization")
                                        .description("access token")
                                        .attributes(field("constraint", "문자열(jwt)"))
                        ),
                        responseFields(
                                fieldWithPath("isSuccess")
                                        .type(JsonFieldType.BOOLEAN)
                                        .description("요청 성공 여부")
                                        .attributes(field("constraint", "boolean 값")),
                                fieldWithPath("code")
                                        .type(JsonFieldType.STRING)
                                        .description("요청 성공 코드")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("message")
                                        .type(JsonFieldType.STRING)
                                        .description("요청 성공 메시지")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("result.quitAt")
                                        .type(JsonFieldType.STRING)
                                        .description("회원탈퇴 시간")
                                        .attributes(field("constraint", "LocalDateTime Type"))
                        )
                )).andReturn();

        final String jsonResponse = mvcResult.getResponse().getContentAsString();
        final CommonResponse<AccountResponseDTO.QuitAccountResponse> actual = objectMapper.readValue(
                jsonResponse,
                new TypeReference<CommonResponse<AccountResponseDTO.QuitAccountResponse>>() {
                }
        );

        final CommonResponse<AccountResponseDTO.QuitAccountResponse> expected = CommonResponse.onSuccess(quitAccountResponse);

        // then
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
        verify(loginService).quitAccount(anyLong());
    }

    @DisplayName("회원은 이메일 재인증을 할 수 있다.")
    @Test
    void reAuthenticationEmail() throws Exception {
        // given
        given(refreshTokenRepository.existsById(any())).willReturn(true);
        doNothing().when(jwtProvider).validateTokens(any());
        given(jwtProvider.getSubject(any())).willReturn("1");

        final MemberTokens memberTokens = new MemberTokens(REFRESH_TOKEN, RENEW_ACCESS_TOKEN);
        final Cookie cookie = new Cookie("refreshToken", memberTokens.getRefreshToken());

        final EmailReAuthenticationRequest emailReAuthenticationRequest = new EmailReAuthenticationRequest("kwondm7@gmail.com");

        final AccountResponseDTO.EmailReAuthenticationResponse emailReAuthenticationResponse = new EmailReAuthenticationResponse(LocalDateTime.now());

        // when
        when(loginService.reAuthenticationEmail(anyLong(), any())).thenReturn(emailReAuthenticationResponse);

        final ResultActions resultActions = mockMvc.perform(post("/api/v1/email/re-authentication")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emailReAuthenticationRequest)));

        final MvcResult mvcResult = resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value("true"))
                .andExpect(jsonPath("$.code").value("1000"))
                .andExpect(jsonPath("$.message").value("요청에 성공하였습니다."))
                .andDo(
                        restDocs.document(
                                requestFields(
                                        fieldWithPath("email")
                                                .type(JsonFieldType.STRING)
                                                .description("변경하고자 하는 타겟 이메일")
                                                .attributes(field("constraint", "문자열"))
                                ),
                                responseFields(
                                        fieldWithPath("isSuccess")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("요청 성공 여부")
                                                .attributes(field("constraint", "boolean 값")),
                                        fieldWithPath("code")
                                                .type(JsonFieldType.STRING)
                                                .description("요청 성공 코드")
                                                .attributes(field("constraint", "문자열")),
                                        fieldWithPath("message")
                                                .type(JsonFieldType.STRING)
                                                .description("요청 성공 메시지")
                                                .attributes(field("constraint", "문자열")),
                                        fieldWithPath("result.reAuthenticationEmailSendAt")
                                                .type(JsonFieldType.STRING)
                                                .description("재인증 이메일 발송 시각")
                                                .attributes(field("constraint", "LocalDateTime"))
                                )
                        ))
                .andReturn();

        final String jsonResponse = mvcResult.getResponse().getContentAsString();
        final CommonResponse<AccountResponseDTO.EmailReAuthenticationResponse> actual = objectMapper.readValue(
                jsonResponse,
                new TypeReference<CommonResponse<AccountResponseDTO.EmailReAuthenticationResponse>>() {
                }
        );

        final CommonResponse<AccountResponseDTO.EmailReAuthenticationResponse> expected = CommonResponse.onSuccess(emailReAuthenticationResponse);

        // then
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
        verify(loginService).reAuthenticationEmail(anyLong(), any());
    }

    @DisplayName("회원은 인증코드를 입력하여 이메일 변경을 진행할 수 있다.")
    @Test
    void verificationAuthCode() throws Exception {
        // given
        given(refreshTokenRepository.existsById(any())).willReturn(true);
        doNothing().when(jwtProvider).validateTokens(any());
        given(jwtProvider.getSubject(any())).willReturn("1");

        final MemberTokens memberTokens = new MemberTokens(REFRESH_TOKEN, RENEW_ACCESS_TOKEN);
        final Cookie cookie = new Cookie("refreshToken", memberTokens.getRefreshToken());

        final AuthCodeVerificationRequest authCodeVerificationRequest = new AuthCodeVerificationRequest("kwondm7@gmail.com", "123456");
        final AccountResponseDTO.EmailVerificationResponse emailVerificationResponse = new EmailVerificationResponse("kwondm7@gmail.com", LocalDateTime.now());

        // when
        when(loginService.verifyAuthCodeAndChangeAccountEmail(anyLong(), any())).thenReturn(emailVerificationResponse);

        final ResultActions resultActions = mockMvc.perform(post("/api/v1/email/verification")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authCodeVerificationRequest)));

        final MvcResult mvcResult = resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value("true"))
                .andExpect(jsonPath("$.code").value("1000"))
                .andExpect(jsonPath("$.message").value("요청에 성공하였습니다."))
                .andDo(
                        restDocs.document(
                                requestFields(
                                        fieldWithPath("changeRequestEmail")
                                                .type(JsonFieldType.STRING)
                                                .description("변경하고자 하는 타겟 이메일")
                                                .attributes(field("constraint", "문자열")),
                                        fieldWithPath("authCode")
                                                .type(JsonFieldType.STRING)
                                                .description("인증 코드")
                                                .attributes(field("constraint", "문자열"))
                                ),
                                responseFields(
                                        fieldWithPath("isSuccess")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("요청 성공 여부")
                                                .attributes(field("constraint", "boolean 값")),
                                        fieldWithPath("code")
                                                .type(JsonFieldType.STRING)
                                                .description("요청 성공 코드")
                                                .attributes(field("constraint", "문자열")),
                                        fieldWithPath("message")
                                                .type(JsonFieldType.STRING)
                                                .description("요청 성공 메시지")
                                                .attributes(field("constraint", "문자열")),
                                        fieldWithPath("result.changedEmail")
                                                .type(JsonFieldType.STRING)
                                                .description("변경된 이메일")
                                                .attributes(field("constraint", "문자열")),
                                        fieldWithPath("result.verificationSuccessAt")
                                                .type(JsonFieldType.STRING)
                                                .description("변경 성공 시각")
                                                .attributes(field("constraint", "LocalDateTime"))
                                )
                        )
                ).andReturn();

        final String jsonResponse = mvcResult.getResponse().getContentAsString();
        final CommonResponse<AccountResponseDTO.EmailVerificationResponse> actual = objectMapper.readValue(
                jsonResponse,
                new TypeReference<CommonResponse<AccountResponseDTO.EmailVerificationResponse>>() {
                }
        );

        final CommonResponse<AccountResponseDTO.EmailVerificationResponse> expected = CommonResponse.onSuccess(emailVerificationResponse);

        // then
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
        verify(loginService).verifyAuthCodeAndChangeAccountEmail(anyLong(), any());
    }
}
