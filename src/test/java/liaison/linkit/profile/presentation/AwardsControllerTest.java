package liaison.linkit.profile.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import liaison.linkit.global.ControllerTest;
import liaison.linkit.login.domain.MemberTokens;
import liaison.linkit.profile.dto.request.AwardsCreateRequest;
import liaison.linkit.profile.dto.request.AwardsUpdateRequest;
import liaison.linkit.profile.dto.response.AwardsResponse;
import liaison.linkit.profile.service.AwardsService;
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
import static org.apache.http.HttpHeaders.AUTHORIZATION;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AwardsController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
class AwardsControllerTest extends ControllerTest {

    private static final MemberTokens MEMBER_TOKENS = new MemberTokens("refreshToken", "accessToken");
    private static final Cookie COOKIE = new Cookie("refresh-token", MEMBER_TOKENS.getRefreshToken());

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AwardsService awardsService;

    @BeforeEach
    void setUp() {
        given(refreshTokenRepository.existsById(any())).willReturn(true);
        doNothing().when(jwtProvider).validateTokens(any());
        given(jwtProvider.getSubject(any())).willReturn("1");
        given(awardsService.validateAwardsByMember(1L)).willReturn(1L);
    }

    private void makeAwards() throws Exception {
        final AwardsCreateRequest awardsCreateRequest = new AwardsCreateRequest(
                "홍익대학교 창업경진대회",
                "대상",
                "홍익대학교 창업교육센터",
                2023,
                5,
                "홍익대학교 창업경진대회에서 1등이라는 성과를 이뤄냈습니다."
        );
    }

    // 단일 수상 항목 조회 테스트
    private ResultActions performGetRequest() throws Exception {
        return mockMvc.perform(
                get("/awards")
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
                        .contentType(APPLICATION_JSON)
        );
    }

    // 단일 수상 항목 등록 테스트
    private ResultActions performPostRequest(final AwardsCreateRequest createRequest) throws Exception {
        return mockMvc.perform(post("/awards")
                .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                .cookie(COOKIE)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest))
        );
    }

    // 단일 수상 항목 수정 테스트
    private ResultActions performPutRequest(final AwardsUpdateRequest updateRequest) throws Exception {
        return mockMvc.perform(
                put("/awards")
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest))
        );
    }

    // 단일 수상 항목 삭제 테스트
    private ResultActions performDeleteRequest() throws Exception {
        return mockMvc.perform(delete("/awards")
                .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                .cookie(COOKIE)
        );
    }

    @DisplayName("수상 이력을 조회할 수 있다.")
    @Test
    void getAwards() throws Exception {
        // given
        final AwardsResponse response = new AwardsResponse(
                1L,
                "홍익대학교 창업경진대회",
                "대상",
                "홍익대학교 창업교육센터",
                2023,
                5,
                "홍익대학교 창업경진대회에서 1등이라는 성과를 이뤄냈습니다."
        );

        given(awardsService.getAwardsDetail(1L))
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
                                                .description("수상 이력 항목 ID")
                                                .attributes(field("constraint", "양의 정수")),
                                        fieldWithPath("awardsName")
                                                .type(JsonFieldType.STRING)
                                                .description("수상 부문")
                                                .attributes(field("constraint", "문자열")),
                                        fieldWithPath("ranking")
                                                .type(JsonFieldType.STRING)
                                                .description("수상명")
                                                .attributes(field("constraint", "문자열")),
                                        fieldWithPath("organizer")
                                                .type(JsonFieldType.STRING)
                                                .description("주관 기관")
                                                .attributes(field("constraint", "문자열")),
                                        fieldWithPath("awardsYear")
                                                .type(JsonFieldType.NUMBER)
                                                .description("수상 연도")
                                                .attributes(field("constraint", "양의 정수이자 4자리 수")),
                                        fieldWithPath("awardsMonth")
                                                .type(JsonFieldType.NUMBER)
                                                .description("수상 월")
                                                .attributes(field("constraint", "양의 정수이자 1부터 12까지의 숫자")),
                                        fieldWithPath("awardsDescription")
                                                .type(JsonFieldType.STRING)
                                                .description("수상 이력 설명")
                                                .attributes(field("constraint", "문자열"))
                                )
                        )
                );
    }
}
