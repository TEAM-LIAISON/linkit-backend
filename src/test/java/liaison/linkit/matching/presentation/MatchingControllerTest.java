package liaison.linkit.matching.presentation;

import static liaison.linkit.global.restdocs.RestDocsConfiguration.field;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import liaison.linkit.common.presentation.CommonResponse;
import liaison.linkit.global.ControllerTest;
import liaison.linkit.login.domain.MemberTokens;
import liaison.linkit.matching.presentation.dto.MatchingResponseDTO.MatchingMenu;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(MatchingController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
public class MatchingControllerTest extends ControllerTest {
    private static final MemberTokens MEMBER_TOKENS = new MemberTokens("accessToken", "refreshToken");
    private static final Cookie COOKIE = new Cookie("refreshToken", MEMBER_TOKENS.getRefreshToken());

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

    private ResultActions performGetMatchingMenu() throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders.get("/api/v1/matching/notification/menu")
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
        );
    }

    @DisplayName("매칭 관리 상단 정보를 조회할 수 있다.")
    @Test
    void getMatchingMenu() throws Exception {
        // given
        final MatchingMenu matchingMenu = MatchingMenu.builder()
                .receivedMatchingNotificationCount(10)
                .requestedMatchingNotificationCount(12)
                .build();

        // when
        when(matchingService.getMatchingMenu(any())).thenReturn(matchingMenu);

        final ResultActions resultActions = performGetMatchingMenu();

        // then
        final MvcResult mvcResult = resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value("true"))
                .andExpect(jsonPath("$.code").value("1000"))
                .andExpect(jsonPath("$.message").value("요청에 성공하였습니다."))
                .andDo(
                        restDocs.document(
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
                                        subsectionWithPath("result.receivedMatchingNotificationCount")
                                                .type(JsonFieldType.NUMBER)
                                                .description("수신함 안읽은 알림 개수"),
                                        subsectionWithPath("result.requestedMatchingNotificationCount")
                                                .type(JsonFieldType.NUMBER)
                                                .description("발신함 안읽은 알림 개수")
                                )
                        )
                ).andReturn();

        final String jsonResponse = mvcResult.getResponse().getContentAsString();
        final CommonResponse<MatchingMenu> actual = objectMapper.readValue(
                jsonResponse,
                new TypeReference<CommonResponse<MatchingMenu>>() {
                }
        );

        final CommonResponse<MatchingMenu> expected = CommonResponse.onSuccess(matchingMenu);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }
}
