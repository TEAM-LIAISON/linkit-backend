package liaison.linkit.profile.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import liaison.linkit.global.ControllerTest;
import liaison.linkit.login.domain.MemberTokens;
import liaison.linkit.profile.dto.request.EducationCreateRequest;
import liaison.linkit.profile.service.EducationService;
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
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EducationController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
class EducationControllerTest extends ControllerTest {

    private static final MemberTokens MEMBER_TOKENS = new MemberTokens("refreshToken", "accessToken");
    private static final Cookie COOKIE = new Cookie("refresh-token", MEMBER_TOKENS.getRefreshToken());

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EducationService educationService;

    @BeforeEach
    void setUp() {
        given(refreshTokenRepository.existsById(any())).willReturn(true);
        doNothing().when(jwtProvider).validateTokens(any());
        given(jwtProvider.getSubject(any())).willReturn("1");
        given(educationService.validateEducationByMember(1L)).willReturn(1L);
    }

    private ResultActions performPostRequest(final EducationCreateRequest createRequest) throws Exception {
        return mockMvc.perform(
                post("/education")
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest))
        );
    }

    @DisplayName("교육 항목을 생성할 수 있다.")
    @Test
    void createEducation() throws Exception {
        // given
        final EducationCreateRequest educationCreateRequest = new EducationCreateRequest(
                2022,
                2025,
                "홍익대학교",
                "컴퓨터공학과",
                "졸업"
        );

        // when
        final ResultActions resultActions = performPostRequest(educationCreateRequest);

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
                                        fieldWithPath("admissionYear")
                                                .type(JsonFieldType.NUMBER)
                                                .description("입학 연도")
                                                .attributes(field("constraint", "4자리 숫자")),
                                        fieldWithPath("graduationYear")
                                                .type(JsonFieldType.NUMBER)
                                                .description("졸업 연도")
                                                .attributes(field("constraint", "4자리 숫자")),
                                        fieldWithPath("universityName")
                                                .type(JsonFieldType.STRING)
                                                .description("학교명")
                                                .attributes(field("constraint", "문자열")),
                                        fieldWithPath("majorName")
                                                .type(JsonFieldType.STRING)
                                                .description("전공명")
                                                .attributes(field("constraint", "문자열")),
                                        fieldWithPath("degreeName")
                                                .type(JsonFieldType.STRING)
                                                .description("학위명")
                                                .attributes(field("constraint", "문자열"))
                                )
                        )
                );
    }
}
