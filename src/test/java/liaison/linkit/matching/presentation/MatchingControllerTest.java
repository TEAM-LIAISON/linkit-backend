package liaison.linkit.matching.presentation;


import com.fasterxml.jackson.databind.ObjectMapper;
import groovy.util.logging.Slf4j;
import jakarta.servlet.http.Cookie;
import liaison.linkit.global.ControllerTest;
import liaison.linkit.login.domain.MemberTokens;
import liaison.linkit.matching.dto.request.MatchingCreateRequest;
import liaison.linkit.matching.service.MatchingService;
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
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MatchingController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
@Slf4j
class MatchingControllerTest extends ControllerTest {

    private static final MemberTokens MEMBER_TOKENS = new MemberTokens("refreshToken", "accessToken");
    private static final Cookie COOKIE = new Cookie("refresh-token", MEMBER_TOKENS.getRefreshToken());

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MatchingService matchingService;

    @BeforeEach
    void setUp() {
        given(refreshTokenRepository.existsById(any())).willReturn(true);
        doNothing().when(jwtProvider).validateTokens(any());
        given(jwtProvider.getSubject(any())).willReturn("1");
    }

    private ResultActions performPostProfileMatchingRequest(
            final int profileId,
            final MatchingCreateRequest matchingCreateRequest
    ) throws Exception {
        return mockMvc.perform(post("/matching/profile/{profileId}", profileId)
                .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken()) // Ensure "Bearer " prefix is used if required by your authentication mechanism
                .cookie(COOKIE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(matchingCreateRequest))
        );
    }

    private ResultActions performPostTeamProfileMatchingRequest(
            final int teamProfileId,
            final MatchingCreateRequest matchingCreateRequest
    ) throws Exception {
        return mockMvc.perform(post("/matching/teamProfile/{teamProfileId}", teamProfileId)
                .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                .cookie(COOKIE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(matchingCreateRequest))
        );
    }

//    @DisplayName("내 이력서에 매칭 요청을 보낼 수 있다.")
//    @Test
//    void createProfileMatching() throws Exception {
//        // given
//        final MatchingCreateRequest matchingCreateRequest = new MatchingCreateRequest(
//                "개발자를 찾고 계신가요?"
//        );
//
//        // when
//        final ResultActions resultActions = performPostProfileMatchingRequest(1, matchingCreateRequest);
//
//        // then
//        resultActions.andExpect(status().isCreated())
//                .andDo(
//                        restDocs.document(
//                                requestCookies(
//                                        cookieWithName("refresh-token")
//                                                .description("갱신 토큰")
//                                ),
//                                requestHeaders(
//                                        headerWithName("Authorization")
//                                                .description("access token")
//                                                .attributes(field("constraint", "문자열(jwt)"))
//                                ),
//                                pathParameters(
//                                        parameterWithName("profileId")
//                                                .description("내 이력서 ID")
//                                ),
//                                requestFields(
//                                        fieldWithPath("requestMessage")
//                                                .type(JsonFieldType.STRING)
//                                                .description("요청 메시지")
//                                                .attributes(field("constraint", "문자열"))
//                                )
//                        )
//                );
//    }

    @DisplayName("내 이력서에 매칭 요청을 보낼 수 있다.")
    @Test
    void createTeamProfileMatching() throws Exception {
        // given
        final MatchingCreateRequest matchingCreateRequest = new MatchingCreateRequest(
                "팀에 합류하고 싶어요"
        );

        // when
        final ResultActions resultActions = performPostTeamProfileMatchingRequest(1, matchingCreateRequest);

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
                                pathParameters(
                                        parameterWithName("teamProfileId")
                                                .description("팀 소개서 ID")
                                ),
                                requestFields(
                                        fieldWithPath("requestMessage")
                                                .type(JsonFieldType.STRING)
                                                .description("요청 메시지")
                                                .attributes(field("constraint", "문자열"))
                                )
                        )
                );
    }
}
