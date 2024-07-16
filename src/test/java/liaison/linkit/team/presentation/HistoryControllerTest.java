package liaison.linkit.team.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import liaison.linkit.global.ControllerTest;
import liaison.linkit.login.domain.MemberTokens;
import liaison.linkit.team.dto.request.HistoryCreateRequest;
import liaison.linkit.team.service.HistoryService;
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

import java.util.Arrays;
import java.util.List;

import static liaison.linkit.global.restdocs.RestDocsConfiguration.field;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HistoryController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
public class HistoryControllerTest extends ControllerTest {

    private static final MemberTokens MEMBER_TOKENS = new MemberTokens("refreshToken", "accessToken");
    private static final Cookie COOKIE = new Cookie("refresh-token", MEMBER_TOKENS.getRefreshToken());

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private HistoryService historyService;

    @BeforeEach
    void setUp() {
        given(refreshTokenRepository.existsById(any())).willReturn(true);
        doNothing().when(jwtProvider).validateTokens(any());
        given(jwtProvider.getSubject(any())).willReturn("1");
        doNothing().when(historyService).validateHistoryByMember(anyLong());
    }

    private void makeHistories() throws Exception{
        final HistoryCreateRequest firstHistoryCreateRequest = new HistoryCreateRequest(
                "Seed 투자 유치",
                2023,
                2024,
                true,
                "5,000만원 투자를 받았어요"
        );

        final HistoryCreateRequest secondHistoryCreateRequest = new HistoryCreateRequest(
                "MVP 테스트",
                2022,
                2023,
                false,
                "사용자 5,000명을 모았어요"
        );

        final List<HistoryCreateRequest> historyCreateRequestList = Arrays.asList(firstHistoryCreateRequest, secondHistoryCreateRequest);

        doNothing().when(historyService).saveHistories(1L, historyCreateRequestList);

        performPostHistories(historyCreateRequestList);
    }

    private ResultActions performPostHistory(final HistoryCreateRequest historyCreateRequest) throws Exception {
        return mockMvc.perform(
                post("/team/history")
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(historyCreateRequest))
        );
    }


    private ResultActions performUpdateHistory(final int historyId, final HistoryCreateRequest historyCreateRequest) throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders.post("/team/history/{historyId}", historyId)
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(historyCreateRequest))
        );
    }

    private ResultActions performPostHistories(final List<HistoryCreateRequest> historyCreateRequests) throws Exception {
        return mockMvc.perform(
                post("/team/histories")
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(historyCreateRequests)));
    }

    private ResultActions performDeleteHistoryRequest(
            final int historyId
    ) throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders.delete("/team/history/{historyId}", historyId)
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
                        .contentType(APPLICATION_JSON)
        );
    }

    @DisplayName("단일 연혁을 수정할 수 있다.")
    @Test
    void updateHistory() throws Exception {
        // given
        final HistoryCreateRequest historyCreateRequest = new HistoryCreateRequest(
                "Seed 투자 유치",
                2023,
                2024,
                true,
                "5,000만원 투자를 받았어요"
        );

        // when
        final ResultActions resultActions = performUpdateHistory(1, historyCreateRequest);

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
                                pathParameters(
                                        parameterWithName("historyId")
                                                .description("연혁 ID")
                                ),
                                requestFields(
                                        fieldWithPath("historyOneLineIntroduction")
                                                .type(JsonFieldType.STRING)
                                                .description("연혁 한 줄 소개")
                                                .attributes(field("constraint", "문자열")),
                                        fieldWithPath("startYear")
                                                .type(JsonFieldType.NUMBER)
                                                .description("시작 연도")
                                                .attributes(field("constraint", "숫자")),
                                        fieldWithPath("endYear")
                                                .type(JsonFieldType.NUMBER)
                                                .description("종료 연도")
                                                .attributes(field("constraint", "숫자")),
                                        fieldWithPath("inProgress")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("현재 진행 여부")
                                                .attributes(field("constraint", "boolean")),
                                        fieldWithPath("historyIntroduction")
                                                .type(JsonFieldType.STRING)
                                                .description("연혁 소개 텍스트")
                                                .attributes(field("constraint", "문자열"))
                                )
                        )
                );
    }


    @DisplayName("단일 연혁을 생성할 수 있다.")
    @Test
    void createHistory() throws  Exception {
        // given
        final HistoryCreateRequest historyCreateRequest = new HistoryCreateRequest(
                "Seed 투자 유치",
                2023,
                2024,
                true,
                "5,000만원 투자를 받았어요"
        );

        // when
        final ResultActions resultActions = performPostHistory(historyCreateRequest);

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
                                        fieldWithPath("historyOneLineIntroduction")
                                                .type(JsonFieldType.STRING)
                                                .description("연혁 한 줄 소개")
                                                .attributes(field("constraint", "문자열")),
                                        fieldWithPath("startYear")
                                                .type(JsonFieldType.NUMBER)
                                                .description("시작 연도")
                                                .attributes(field("constraint", "숫자")),
                                        fieldWithPath("endYear")
                                                .type(JsonFieldType.NUMBER)
                                                .description("종료 연도")
                                                .attributes(field("constraint", "숫자")),
                                        fieldWithPath("inProgress")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("현재 진행 여부")
                                                .attributes(field("constraint", "boolean")),
                                        fieldWithPath("historyIntroduction")
                                                .type(JsonFieldType.STRING)
                                                .description("연혁 소개 텍스트")
                                                .attributes(field("constraint", "문자열"))
                                )
                        )
                );
    }

    @DisplayName("연혁 리스트 생성할 수 있다.")
    @Test
    void createHistories() throws Exception {
        // given
        final HistoryCreateRequest firstHistoryCreateRequest = new HistoryCreateRequest(
                "Seed 투자 유치",
                2023,
                2024,
                true,
                "5,000만원 투자를 받았어요"
        );

        final HistoryCreateRequest secondHistoryCreateRequest = new HistoryCreateRequest(
                "MVP 테스트",
                2022,
                2023,
                false,
                "사용자 5,000명을 모았어요"
        );

        final List<HistoryCreateRequest> historyCreateRequestList = Arrays.asList(firstHistoryCreateRequest, secondHistoryCreateRequest);

        // when
        final ResultActions resultActions = performPostHistories(historyCreateRequestList);

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
                                        fieldWithPath("[].historyOneLineIntroduction")
                                                .type(JsonFieldType.STRING)
                                                .description("연혁 한 줄 소개")
                                                .attributes(field("constraint", "문자열")),
                                        fieldWithPath("[].startYear")
                                                .type(JsonFieldType.NUMBER)
                                                .description("시작 연도")
                                                .attributes(field("constraint", "숫자")),
                                        fieldWithPath("[].endYear")
                                                .type(JsonFieldType.NUMBER)
                                                .description("종료 연도")
                                                .attributes(field("constraint", "숫자")),
                                        fieldWithPath("[].inProgress")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("현재 진행 여부")
                                                .attributes(field("constraint", "boolean")),
                                        fieldWithPath("[].historyIntroduction")
                                                .type(JsonFieldType.STRING)
                                                .description("연혁 소개 텍스트")
                                                .attributes(field("constraint", "문자열"))
                                )
                        )
                );
    }

    @DisplayName("연혁 1개를 삭제 할 수 있다.")
    @Test
    void deleteHistory() throws Exception {
        // given
        makeHistories();
        doNothing().when(historyService).validateHistoryByMember(anyLong());

        // when
        final ResultActions resultActions = performDeleteHistoryRequest(1);

        // then
        verify(historyService).deleteHistory(1L, 1L);

        resultActions.andExpect(status().isNoContent())
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("historyId")
                                        .description("연혁 ID")
                        )
                ));
    }



}
