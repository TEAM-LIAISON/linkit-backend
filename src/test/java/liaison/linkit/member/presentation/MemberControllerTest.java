package liaison.linkit.member.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import groovy.util.logging.Slf4j;
import jakarta.servlet.http.Cookie;
import liaison.linkit.global.ControllerTest;
import liaison.linkit.login.domain.MemberTokens;
import liaison.linkit.member.dto.request.MemberBasicInformCreateRequest;
import liaison.linkit.member.dto.response.MemberBasicInformResponse;
import liaison.linkit.member.dto.response.MemberResponse;
import liaison.linkit.member.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

import static liaison.linkit.global.restdocs.RestDocsConfiguration.field;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MemberController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
@Slf4j
class MemberControllerTest extends ControllerTest {

    private static final MemberTokens MEMBER_TOKENS = new MemberTokens("refreshToken", "accessToken");
    private static final Cookie COOKIE = new Cookie("refresh-token", MEMBER_TOKENS.getRefreshToken());

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MemberService memberService;

    @BeforeEach
    void setUp() {
        given(refreshTokenRepository.existsById(any())).willReturn(true);
        doNothing().when(jwtProvider).validateTokens(any());
        given(jwtProvider.getSubject(any())).willReturn("1");
        given(memberService.validateMemberBasicInformByMember(1L)).willReturn(1L);

    }

    private ResultActions performPostRequest(final MemberBasicInformCreateRequest memberBasicInformCreateRequest) throws Exception {
        return mockMvc.perform(
                post("/members/basic-inform")
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberBasicInformCreateRequest))
        );
    }

    private ResultActions performGetRequest() throws Exception {
        return mockMvc.perform(
                get("/members/basic-inform")
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
        );
    }

    private ResultActions performGetEmailRequest() throws Exception {
        return mockMvc.perform(
                get("/members/email")
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
        );
    }

    @DisplayName("사용자 기본 정보를 조회할 수 있다.")
    @Test
    void getMemberBasicInform() throws Exception {
        // given
        final MemberBasicInformResponse response = new MemberBasicInformResponse(
                1L,
                "권동민",
                "010-3661-4067",
                "개발자",
                true
        );

        given(memberService.getMemberBasicInformDetail(1L))
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
                                                .description("멤버 기본 정보 ID")
                                                .attributes(field("constraint", "양의 정수")),
                                        fieldWithPath("memberName")
                                                .type(JsonFieldType.STRING)
                                                .description("성함")
                                                .attributes(field("constraint", "문자열")),
                                        fieldWithPath("contact")
                                                .type(JsonFieldType.STRING)
                                                .description("연락처")
                                                .attributes(field("constraint", "010-xxxx-xxxx 형태")),
                                        fieldWithPath("roleName")
                                                .type(JsonFieldType.STRING)
                                                .description("직무 및 역할")
                                                .attributes(field("constraint", "문자열")),
                                        fieldWithPath("marketingAgree")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("마케팅 수신 동의 여부")
                                                .attributes(field("constraint", "Boolean & Default FALSE"))
                                )
                        )
                );
    }

    @DisplayName("사용자 기본 정보를 생성할 수 있다.")
    @Test
    void createMemberBasicInform() throws Exception {
        // given
        final MemberBasicInformCreateRequest createRequest = new MemberBasicInformCreateRequest(
                "권동민",
                "010-3661-4067",
                "개발자",
                true
        );

        // when
        final ResultActions resultActions = performPostRequest(createRequest);

        // then
        resultActions.andExpect(status().isCreated())
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
                                        fieldWithPath("memberName")
                                                .type(JsonFieldType.STRING)
                                                .description("성함")
                                                .attributes(field("constraint", "문자열")),
                                        fieldWithPath("contact")
                                                .type(JsonFieldType.STRING)
                                                .description("연락처")
                                                .attributes(field("constraint", "010-xxxx-xxxx 형태")),
                                        fieldWithPath("roleName")
                                                .type(JsonFieldType.STRING)
                                                .description("직무 및 역할")
                                                .attributes(field("constraint", "문자열")),
                                        fieldWithPath("marketingAgree")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("마케팅 수신 동의 여부")
                                                .attributes(field("constraint", "Boolean & Default FALSE"))
                                )
                        )
                );
    }

    @DisplayName("사용자 이메일 정보를 조회할 수 있다.")
    @Test
    void getMemberEmail() throws Exception {
        // given
        final MemberResponse response = new MemberResponse(
                "kwondm7@naver.com"
        );

        given(memberService.getMemberEmail(1L))
                .willReturn(response);

        // when
        final ResultActions resultActions = performGetEmailRequest();

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
                                        fieldWithPath("email")
                                                .type(JsonFieldType.STRING)
                                                .description("이메일")
                                                .attributes(field("constraint", "@포함 문자열"))
                                )
                        )
                );
    }
}
