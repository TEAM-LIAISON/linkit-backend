package liaison.linkit.login.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import liaison.linkit.global.ControllerTest;
import liaison.linkit.login.domain.MemberTokens;
import liaison.linkit.login.dto.*;
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

import static liaison.linkit.global.restdocs.RestDocsConfiguration.field;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


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

    @DisplayName("로그인을 할 수 있다.")
    @Test
    void login() throws Exception {
        // given
        final LoginRequest loginRequest = new LoginRequest("code");
        final MemberTokens memberTokens = new MemberTokens(REFRESH_TOKEN, ACCESS_TOKEN);
        final MemberTokensAndOnBoardingStepInform memberTokensAndOnBoardingStepInform
                = new MemberTokensAndOnBoardingStepInform(ACCESS_TOKEN, REFRESH_TOKEN, EMAIL, false, false);

        when(loginService.login(anyString(), anyString()))
                .thenReturn(memberTokensAndOnBoardingStepInform);

        final ResultActions resultActions = mockMvc.perform(post("/login/{provider}", GOOGLE_PROVIDER)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest))
        );

        // when
        final MvcResult mvcResult = resultActions.andExpect(status().isCreated())
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
                                        fieldWithPath("accessToken")
                                                .type(JsonFieldType.STRING)
                                                .description("access token")
                                                .attributes(field("constraint", "문자열(jwt)")),
                                        fieldWithPath("email")
                                                .type(JsonFieldType.STRING)
                                                .description("소셜 로그인 이메일")
                                                .attributes(field("constraint", "문자열")),
                                        fieldWithPath("existMemberBasicInform")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("기본 정보 기입 여부 (false: 기본 정보 기입하지 않음)")
                                                .attributes(field("constraint", "boolean 값")),
                                        fieldWithPath("existDefaultProfile")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("이력서 작성 여부 (false: 내 이력서와 팀 소개서 모두 존재 X | true: 내 이력서나 팀 소개서 중 최소 1개 이상 필수 항목 기입 완료)")
                                                .attributes(field("constraint", "boolean 값"))
                                )
                        ))
                .andReturn();

        final LoginResponse expected = new LoginResponse(memberTokens.getAccessToken(), EMAIL, false, false);
        final LoginResponse actual = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                LoginResponse.class
        );

        // then
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }



    @DisplayName("accessToken 재발급을 통해 로그인을 인정할 수 있다.")
    @Test
    void extendLogin() throws Exception {
        // given
        final MemberTokens memberTokens = new MemberTokens(REFRESH_TOKEN, RENEW_ACCESS_TOKEN);
        final Cookie cookie = new Cookie("refresh-token", memberTokens.getRefreshToken());

        final RenewTokenResponse renewTokenResponse = new RenewTokenResponse(RENEW_ACCESS_TOKEN, true, true, true);

        when(loginService.renewalAccessToken(REFRESH_TOKEN, ACCESS_TOKEN))
                .thenReturn(renewTokenResponse);

        // when
        final ResultActions resultActions = mockMvc.perform(post("/token")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                .cookie(cookie)
        );

        final MvcResult mvcResult = resultActions.andExpect(status().isCreated())
                .andDo(restDocs.document(
                        requestCookies(
                                cookieWithName("refresh-token")
                                        .description("갱신 토큰")
                        ),
                        requestHeaders(
                                headerWithName("Authorization")
                                        .description("access token")
                                        .attributes(field("constraint", "문자열(jwt)"))
                        ),
                        responseFields(
                                fieldWithPath("accessToken")
                                        .type(JsonFieldType.STRING)
                                        .description("access token")
                                        .attributes(field("constraint", "문자열(jwt)")),
                                fieldWithPath("existMemberBasicInform")
                                        .type(JsonFieldType.BOOLEAN)
                                        .description("기본 정보 기입 여부 (false: 기본 정보 기입하지 않음)")
                                        .attributes(field("constraint", "boolean 값")),
                                fieldWithPath("existDefaultProfile")
                                        .type(JsonFieldType.BOOLEAN)
                                        .description("이력서 작성 여부 (false: 내 이력서와 팀 소개서 모두 존재 X | true: 내 이력서나 팀 소개서 중 최소 1개 이상 필수 항목 기입 완료)")
                                        .attributes(field("constraint", "boolean 값")),
                                fieldWithPath("existNonCheckNotification")
                                        .type(JsonFieldType.BOOLEAN)
                                        .description("확인하지 않은 알림의 존재 유무")
                                        .attributes(field("constraint", "boolean 값"))
                        )
                ))
                .andReturn();

        final AccessTokenResponse expected = new AccessTokenResponse(memberTokens.getAccessToken());

        final AccessTokenResponse actual = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                AccessTokenResponse.class
        );

        // then
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @DisplayName("멤버의 refreshToken을 삭제하고 로그아웃 할 수 있다.")
    @Test
    void logout() throws Exception {
        // given
        given(refreshTokenRepository.existsById(any())).willReturn(true);
        doNothing().when(jwtProvider).validateTokens(any());
        given(jwtProvider.getSubject(any())).willReturn("1");
        doNothing().when(loginService).removeRefreshToken(anyString());

        final MemberTokens memberTokens = new MemberTokens(REFRESH_TOKEN, RENEW_ACCESS_TOKEN);
        final Cookie cookie = new Cookie("refresh-token", memberTokens.getRefreshToken());

        // when
        final ResultActions resultActions = mockMvc.perform(delete("/logout")
                .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                .cookie(cookie)
        );

        resultActions.andExpect(status().isNoContent())
                .andDo(restDocs.document(
                        requestCookies(
                                cookieWithName("refresh-token")
                                        .description("갱신 토큰")
                        ),
                        requestHeaders(
                                headerWithName("Authorization")
                                        .description("access token")
                                        .attributes(field("constraint", "문자열(jwt)"))
                        )
                ));

        // then
        verify(loginService).removeRefreshToken(anyString());
    }

    @DisplayName("회원을 탈퇴 할 수 있다.")
    @Test
    void deleteAccount() throws Exception {
        // given
        given(refreshTokenRepository.existsById(any())).willReturn(true);
        doNothing().when(jwtProvider).validateTokens(any());
        given(jwtProvider.getSubject(any())).willReturn("1");
        doNothing().when(loginService).deleteAccount(anyLong());

        final MemberTokens memberTokens = new MemberTokens(REFRESH_TOKEN, RENEW_ACCESS_TOKEN);
        final Cookie cookie = new Cookie("refresh-token", memberTokens.getRefreshToken());

        // when
        final ResultActions resultActions = mockMvc.perform(delete("/account")
                .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN)
                .cookie(cookie)
        );

        resultActions.andExpect(status().isNoContent())
                .andDo(restDocs.document(
                        requestCookies(
                                cookieWithName("refresh-token")
                                        .description("갱신 토큰")
                        ),
                        requestHeaders(
                                headerWithName("Authorization")
                                        .description("access token")
                                        .attributes(field("constraint", "문자열(jwt)"))
                        )
                ));

        // then
        verify(loginService).deleteAccount(anyLong());
    }
}
