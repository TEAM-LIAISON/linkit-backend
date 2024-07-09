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
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

import static liaison.linkit.global.restdocs.RestDocsConfiguration.field;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
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

    private ResultActions performPrivateProfileMatchingToPrivate(
            final int profileId,
            final MatchingCreateRequest matchingCreateRequest
    ) throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders.post("/private/profile/matching/private/{profileId}", profileId)
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(matchingCreateRequest))
        );
    }

    @DisplayName("내 이력서로 내 이력서에 매칭 요청을 보낼 수 있다.")
    @Test
    void createPrivateProfileMatchingToPrivate() throws Exception {
        // given
        final MatchingCreateRequest matchingCreateRequest = new MatchingCreateRequest(
                "매칭 요청 메시지입니다."
        );

        // when
        final ResultActions resultActions = performPrivateProfileMatchingToPrivate(1, matchingCreateRequest);

        // then
        verify(matchingService).createPrivateProfileMatchingToPrivate(eq(1L), eq(1L), any(MatchingCreateRequest.class));

        resultActions.andExpect(status().isCreated())
                .andDo(restDocs.document(
                        requestFields(
                                fieldWithPath("requestMessage")
                                        .type(JsonFieldType.STRING)
                                        .description("요청 메시지입니다.")
                                        .attributes(field("constraint", "문자열"))
                        )
                ));
    }

//    @DisplayName("팀 소개서로 내 이력서에 매칭 요청을 보낼 수 있다.")
//    @Test
//    void
}
