package liaison.linkit.profile.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import liaison.linkit.global.ControllerTest;
import liaison.linkit.login.domain.MemberTokens;
import liaison.linkit.login.service.LoginService;
import liaison.linkit.profile.dto.request.MiniProfileCreateRequest;
import liaison.linkit.profile.dto.response.MiniProfileResponse;
import liaison.linkit.profile.service.MiniProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

import static liaison.linkit.global.restdocs.RestDocsConfiguration.field;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MiniProfileController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
class MiniProfileControllerTest extends ControllerTest {

    private static final MemberTokens MEMBER_TOKENS = new MemberTokens("refreshToken", "accessToken");
    private static final Cookie COOKIE = new Cookie("refresh-token", MEMBER_TOKENS.getRefreshToken());

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MiniProfileService miniProfileService;

    @MockBean
    private LoginService loginService;

    @BeforeEach
    void setUp() {
        given(refreshTokenRepository.existsById(any())).willReturn(true);
        doNothing().when(jwtProvider).validateTokens(any());
        given(jwtProvider.getSubject(any())).willReturn("1");
        given(miniProfileService.validateMiniProfileByMember(1L)).willReturn(1L);
    }

    private ResultActions performGetRequest() throws Exception {
        return mockMvc.perform(
                get("/mini-profile")
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
                        .contentType(APPLICATION_JSON));
    }

    private ResultActions performPostRequest(final MiniProfileCreateRequest miniProfileCreateRequest) throws Exception {
        return mockMvc.perform(post("/mini-profile")
                .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                .cookie(COOKIE)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(miniProfileCreateRequest)));
    }

    private ResultActions performPatchUpdateRequest() throws Exception {
        return mockMvc.perform(patch("/mini-profile")
                .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                .cookie(COOKIE)
                .contentType(APPLICATION_JSON));
    }

    @DisplayName("미니 프로필을 조회할 수 있다.")
    @Test
    void getMiniProfile() throws Exception {
        // given
        final MiniProfileResponse response = new MiniProfileResponse(
                1L,
                "안녕하세요.",
                "Java / Spring Boot / MySQL",
                "홍익대학교 컴퓨터공학과",
                "리에종의 개발자입니다.");

        given(miniProfileService.getMiniProfileDetail(1L))
                .willReturn(response);

        // when
        final ResultActions resultActions = performGetRequest();
        // then

        resultActions.andExpect(status().isOk())
                .andDo(
                        restDocs.document(
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
                                        fieldWithPath("id")
                                                .type(JsonFieldType.NUMBER)
                                                .description("개인용 미니 프로필 ID")
                                                .attributes(field("constraint", "양의 정수")),
                                        fieldWithPath("oneLineIntroduction")
                                                .type(JsonFieldType.STRING)
                                                .description("한 줄 소개")
                                                .attributes(field("constraint", "문자열")),
                                        fieldWithPath("interests")
                                                .type(JsonFieldType.STRING)
                                                .description("관심 분야")
                                                .attributes(field("constraint", "문자열")),
                                        fieldWithPath("firstFreeText")
                                                .type(JsonFieldType.STRING)
                                                .description("자율 기입 첫 줄")
                                                .attributes(field("constraint", "문자열")),
                                        fieldWithPath("secondFreeText")
                                                .type(JsonFieldType.STRING)
                                                .description("자율 기입 둘째 줄")
                                                .attributes(field("constraint", "문자열"))
                                )
                        )
                );
    }

    @DisplayName("단일 미니 프로필을 생성할 수 있다.")
    @Test
    void createMiniProfile() throws Exception {
        // given
        final MiniProfileCreateRequest miniProfileCreateRequest = new MiniProfileCreateRequest(
                "안녕하세요.",
                "Java / Spring Boot / MySQL",
                "홍익대학교 컴퓨터공학과",
                "리에종의 개발자입니다."
        );

        MiniProfileResponse expectedResponse = new MiniProfileResponse(
                1L,
                "안녕하세요.",
                "Java / Spring Boot / MySQL",
                "홍익대학교 컴퓨터공학과",
                "리에종의 개발자입니다."
        );

        when(miniProfileService.save(anyLong(), any(MiniProfileCreateRequest.class)))
                .thenReturn(expectedResponse);
        // when
        final ResultActions resultActions = performPostRequest(miniProfileCreateRequest);

        // then
        resultActions.andExpect(status().isOk())
                .andDo(
                        restDocs.document(
                                requestCookies(
                                        cookieWithName("refresh-token")
                                                .description("갱신 토큰")
                                ),
                                requestHeaders(
                                        headerWithName("Authorization")
                                                .description("access token")
                                                .attributes(field("constraint", "문자열(jwt)"))
                                ),
                                requestFields(
                                        fieldWithPath("oneLineIntroduction")
                                                .type(JsonFieldType.STRING)
                                                .description("한 줄 소개")
                                                .attributes(field("constraint", "문자열")),
                                        fieldWithPath("interests")
                                                .type(JsonFieldType.STRING)
                                                .description("관심 분야")
                                                .attributes(field("constraint", "문자열")),
                                        fieldWithPath("firstFreeText")
                                                .type(JsonFieldType.STRING)
                                                .description("자율 기입 첫 줄")
                                                .attributes(field("constraint", "문자열")),
                                        fieldWithPath("secondFreeText")
                                                .type(JsonFieldType.STRING)
                                                .description("자율 기입 둘째 줄")
                                                .attributes(field("constraint", "문자열"))
                                )
                        )
                );
    }

}
