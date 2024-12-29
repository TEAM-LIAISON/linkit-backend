package liaison.linkit.team.presentation;

import static liaison.linkit.global.restdocs.RestDocsConfiguration.field;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import java.util.Arrays;
import liaison.linkit.common.presentation.CommonResponse;
import liaison.linkit.global.ControllerTest;
import liaison.linkit.login.domain.MemberTokens;
import liaison.linkit.team.presentation.history.TeamHistoryController;
import liaison.linkit.team.presentation.history.dto.TeamHistoryRequestDTO;
import liaison.linkit.team.presentation.history.dto.TeamHistoryRequestDTO.AddTeamHistoryRequest;
import liaison.linkit.team.presentation.history.dto.TeamHistoryRequestDTO.UpdateTeamHistoryRequest;
import liaison.linkit.team.presentation.history.dto.TeamHistoryResponseDTO;
import liaison.linkit.team.presentation.history.dto.TeamHistoryResponseDTO.AddTeamHistoryResponse;
import liaison.linkit.team.presentation.history.dto.TeamHistoryResponseDTO.RemoveTeamHistoryResponse;
import liaison.linkit.team.presentation.history.dto.TeamHistoryResponseDTO.TeamHistoryDetail;
import liaison.linkit.team.presentation.history.dto.TeamHistoryResponseDTO.TeamHistoryItem;
import liaison.linkit.team.presentation.history.dto.TeamHistoryResponseDTO.TeamHistoryItems;
import liaison.linkit.team.presentation.history.dto.TeamHistoryResponseDTO.TeamHistoryViewItem;
import liaison.linkit.team.presentation.history.dto.TeamHistoryResponseDTO.TeamHistoryViewItems;
import liaison.linkit.team.presentation.history.dto.TeamHistoryResponseDTO.UpdateTeamHistoryResponse;
import liaison.linkit.team.service.history.TeamHistoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(TeamHistoryController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
public class TeamHistoryControllerTest extends ControllerTest {
    private static final MemberTokens MEMBER_TOKENS = new MemberTokens("accessToken", "refreshToken");
    private static final Cookie COOKIE = new Cookie("refreshToken", MEMBER_TOKENS.getRefreshToken());

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TeamHistoryService teamHistoryService;

    @BeforeEach
    void setUp() {
        given(refreshTokenRepository.existsById(any())).willReturn(true);
        doNothing().when(jwtProvider).validateTokens(any());
        given(jwtProvider.getSubject(any())).willReturn("1");
    }

    private ResultActions performGetTeamHistoryViewItems(final String teamName) throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders.get("/api/v1/team/{teamName}/history/view", teamName)
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
        );
    }

    private ResultActions performGetTeamHistoryItems(final String teamName) throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders.get("/api/v1/team/{teamName}/history", teamName)
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
        );
    }

    private ResultActions performGetTeamHistoryDetail(final String teamName, final Long teamHistoryId) throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders.get("/api/v1/team/{teamName}/history/{teamHistoryId}", teamName, teamHistoryId)
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
        );
    }

    private ResultActions performAddTeamHistory(final String teamName, final AddTeamHistoryRequest request) throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders.post("/api/v1/team/{teamName}/history", teamName)
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));
    }

    private ResultActions performUpdateTeamHistory(final String teamName, final Long teamHistoryId, final UpdateTeamHistoryRequest request) throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders.post("/api/v1/team/{teamName}/history/{teamHistoryId}", teamName, teamHistoryId)
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));
    }

    private ResultActions performRemoveTeamHistory(final String teamName, final Long teamHistoryId) throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders.delete("/api/v1/team/{teamName}/history/{teamHistoryId}", teamName, teamHistoryId)
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE));
    }

    @DisplayName("회원이 다른 팀의 연혁을 전체 조회할 수 있다. (View)")
    @Test
    void getTeamHistoryViewItems() throws Exception {
        // given
        final TeamHistoryResponseDTO.TeamHistoryViewItems teamHistoryViewItems = TeamHistoryViewItems.builder()
                .teamHistoryViewItems(
                        Arrays.asList(
                                TeamHistoryViewItem.builder()
                                        .teamHistoryId(1L)
                                        .historyName("연혁명")
                                        .historyStartDate("연혁 시작 날짜")
                                        .historyEndDate("연혁 종료 날짜")
                                        .isHistoryInProgress(true)
                                        .historyDescription("연혁 설명")
                                        .build(),
                                TeamHistoryViewItem.builder()
                                        .teamHistoryId(2L)
                                        .historyName("연혁명")
                                        .historyStartDate("연혁 시작 날짜")
                                        .historyEndDate("연혁 종료 날짜")
                                        .isHistoryInProgress(true)
                                        .historyDescription("연혁 설명")
                                        .build()
                        )
                )
                .build();

        // when
        when(teamHistoryService.getTeamHistoryViewItems(anyLong(), any())).thenReturn(teamHistoryViewItems);

        final ResultActions resultActions = performGetTeamHistoryViewItems("liaison");

        // then
        final MvcResult mvcResult = resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value("true"))
                .andExpect(jsonPath("$.code").value("1000"))
                .andExpect(jsonPath("$.message").value("요청에 성공하였습니다."))
                .andDo(
                        restDocs.document(
                                pathParameters(
                                        parameterWithName("teamName")
                                                .description("팀 이름")
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

                                        subsectionWithPath("result.teamHistoryViewItems[]")
                                                .type(JsonFieldType.ARRAY)
                                                .description("팀 연혁 배열"),
                                        fieldWithPath("result.teamHistoryViewItems[].teamHistoryId")
                                                .type(JsonFieldType.NUMBER)
                                                .description("팀 연혁 ID"),
                                        fieldWithPath("result.teamHistoryViewItems[].historyName")
                                                .type(JsonFieldType.STRING)
                                                .description("팀 연혁 이름"),
                                        fieldWithPath("result.teamHistoryViewItems[].historyStartDate")
                                                .type(JsonFieldType.STRING)
                                                .description("팀 연혁 시작 날짜"),
                                        fieldWithPath("result.teamHistoryViewItems[].historyEndDate")
                                                .type(JsonFieldType.STRING)
                                                .description("팀 연혁 종료 날짜"),
                                        fieldWithPath("result.teamHistoryViewItems[].isHistoryInProgress")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("팀 연혁 진행 여부"),
                                        fieldWithPath("result.teamHistoryViewItems[].historyDescription")
                                                .type(JsonFieldType.STRING)
                                                .description("팀 연혁 설명")
                                )
                        )).andReturn();

        final String jsonResponse = mvcResult.getResponse().getContentAsString();
        final CommonResponse<TeamHistoryViewItems> actual = objectMapper.readValue(
                jsonResponse,
                new TypeReference<CommonResponse<TeamHistoryViewItems>>() {
                }
        );

        final CommonResponse<TeamHistoryViewItems> expected = CommonResponse.onSuccess(teamHistoryViewItems);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @DisplayName("회원이 팀의 연혁을 전체 조회할 수 있다.")
    @Test
    void getTeamHistoryItems() throws Exception {
        // given
        final TeamHistoryResponseDTO.TeamHistoryItem firstTeamHistoryItem
                = new TeamHistoryItem(1L, "연혁 이름 1", "연혁 시작 날짜 1", "연혁 종료 날짜 2", false);

        final TeamHistoryResponseDTO.TeamHistoryItem secondTeamHistoryItem
                = new TeamHistoryItem(2L, "연혁 이름 2", "연혁 시작 날짜 1", "연혁 종료 날짜 2", false);

        final TeamHistoryResponseDTO.TeamHistoryItems teamHistoryItems
                = new TeamHistoryItems(Arrays.asList(firstTeamHistoryItem, secondTeamHistoryItem));

        // when
        when(teamHistoryService.getTeamHistoryItems(anyLong(), any())).thenReturn(teamHistoryItems);

        final ResultActions resultActions = performGetTeamHistoryItems("리에종");

        // then
        final MvcResult mvcResult = resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value("true"))
                .andExpect(jsonPath("$.code").value("1000"))
                .andExpect(jsonPath("$.message").value("요청에 성공하였습니다."))
                .andDo(
                        restDocs.document(
                                pathParameters(
                                        parameterWithName("teamName")
                                                .description("팀 이름")
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

                                        subsectionWithPath("result.teamHistoryItems[]")
                                                .type(JsonFieldType.ARRAY)
                                                .description("팀 연혁 배열"),
                                        fieldWithPath("result.teamHistoryItems[].teamHistoryId")
                                                .type(JsonFieldType.NUMBER)
                                                .description("팀 연혁 ID"),
                                        fieldWithPath("result.teamHistoryItems[].historyName")
                                                .type(JsonFieldType.STRING)
                                                .description("팀 연혁 이름"),
                                        fieldWithPath("result.teamHistoryItems[].historyStartDate")
                                                .type(JsonFieldType.STRING)
                                                .description("팀 연혁 시작 날짜"),
                                        fieldWithPath("result.teamHistoryItems[].historyEndDate")
                                                .type(JsonFieldType.STRING)
                                                .description("팀 연혁 종료 날짜"),
                                        fieldWithPath("result.teamHistoryItems[].isHistoryInProgress")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("팀 연혁 진행 여부")
                                )
                        )).andReturn();

        final String jsonResponse = mvcResult.getResponse().getContentAsString();
        final CommonResponse<TeamHistoryItems> actual = objectMapper.readValue(
                jsonResponse,
                new TypeReference<CommonResponse<TeamHistoryItems>>() {
                }
        );

        final CommonResponse<TeamHistoryItems> expected = CommonResponse.onSuccess(teamHistoryItems);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @DisplayName("회원이 팀의 연혁을 상세 조회할 수 있다.")
    @Test
    void getTeamHistoryDetail() throws Exception {
        // given
        final TeamHistoryResponseDTO.TeamHistoryDetail teamHistoryDetail
                = new TeamHistoryDetail(1L, "연혁 이름", "연혁 시작 날짜", "연혁 종료 날짜", false, "연혁 설명");

        // when
        when(teamHistoryService.getTeamHistoryDetail(anyLong(), any(), anyLong())).thenReturn(teamHistoryDetail);

        final ResultActions resultActions = performGetTeamHistoryDetail("리에종", 1L);

        // then
        final MvcResult mvcResult = resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value("true"))
                .andExpect(jsonPath("$.code").value("1000"))
                .andExpect(jsonPath("$.message").value("요청에 성공하였습니다."))
                .andDo(
                        restDocs.document(
                                pathParameters(
                                        parameterWithName("teamName")
                                                .description("팀 이름"),
                                        parameterWithName("teamHistoryId")
                                                .description("팀 연혁 ID")
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

                                        fieldWithPath("result.teamHistoryId")
                                                .type(JsonFieldType.NUMBER)
                                                .description("팀 연혁 ID"),
                                        fieldWithPath("result.historyName")
                                                .type(JsonFieldType.STRING)
                                                .description("팀 연혁 이름"),
                                        fieldWithPath("result.historyStartDate")
                                                .type(JsonFieldType.STRING)
                                                .description("팀 연혁 시작 날짜"),
                                        fieldWithPath("result.historyEndDate")
                                                .type(JsonFieldType.STRING)
                                                .description("팀 연혁 종료 날짜"),
                                        fieldWithPath("result.isHistoryInProgress")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("팀 연혁 진행 여부"),
                                        fieldWithPath("result.historyDescription")
                                                .type(JsonFieldType.STRING)
                                                .description("팀 연혁 설명")
                                )
                        )).andReturn();

        final String jsonResponse = mvcResult.getResponse().getContentAsString();
        final CommonResponse<TeamHistoryDetail> actual = objectMapper.readValue(
                jsonResponse,
                new TypeReference<CommonResponse<TeamHistoryDetail>>() {
                }
        );

        final CommonResponse<TeamHistoryDetail> expected = CommonResponse.onSuccess(teamHistoryDetail);

        // then
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @DisplayName("회원이 팀의 연혁을 단일 생성할 수 있다.")
    @Test
    void addTeamHistory() throws Exception {
        // given
        final TeamHistoryRequestDTO.AddTeamHistoryRequest addTeamHistoryRequest
                = new AddTeamHistoryRequest("연혁 이름", "연혁 시작 날짜", "연혁 종료 날짜", false, "연혁 설명");

        final TeamHistoryResponseDTO.AddTeamHistoryResponse addTeamHistoryResponse
                = new AddTeamHistoryResponse(1L, "연혁 이름", "연혁 시작 날짜", "연혁 종료 날짜", false, "연혁 설명");

        // when
        when(teamHistoryService.addTeamHistory(anyLong(), any(), any())).thenReturn(addTeamHistoryResponse);

        final ResultActions resultActions = performAddTeamHistory("리에종", addTeamHistoryRequest);

        // then
        final MvcResult mvcResult = resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value("true"))
                .andExpect(jsonPath("$.code").value("1000"))
                .andExpect(jsonPath("$.message").value("요청에 성공하였습니다."))
                .andDo(
                        restDocs.document(
                                pathParameters(
                                        parameterWithName("teamName")
                                                .description("팀 이름")
                                ),
                                requestFields(
                                        fieldWithPath("historyName")
                                                .type(JsonFieldType.STRING)
                                                .description("팀 연혁 이름"),
                                        fieldWithPath("historyStartDate")
                                                .type(JsonFieldType.STRING)
                                                .description("팀 연혁 시작 날짜"),
                                        fieldWithPath("historyEndDate")
                                                .type(JsonFieldType.STRING)
                                                .description("팀 연혁 종료 날짜"),
                                        fieldWithPath("isHistoryInProgress")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("팀 연혁 진행 여부"),
                                        fieldWithPath("historyDescription")
                                                .type(JsonFieldType.STRING)
                                                .description("팀 연혁 설명")
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

                                        fieldWithPath("result.teamHistoryId")
                                                .type(JsonFieldType.NUMBER)
                                                .description("팀 연혁 ID"),
                                        fieldWithPath("result.historyName")
                                                .type(JsonFieldType.STRING)
                                                .description("팀 연혁 이름"),
                                        fieldWithPath("result.historyStartDate")
                                                .type(JsonFieldType.STRING)
                                                .description("팀 연혁 시작 날짜"),
                                        fieldWithPath("result.historyEndDate")
                                                .type(JsonFieldType.STRING)
                                                .description("팀 연혁 종료 날짜"),
                                        fieldWithPath("result.isHistoryInProgress")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("팀 연혁 진행 여부"),
                                        fieldWithPath("result.historyDescription")
                                                .type(JsonFieldType.STRING)
                                                .description("팀 연혁 설명")
                                )
                        )).andReturn();

        final String jsonResponse = mvcResult.getResponse().getContentAsString();
        final CommonResponse<AddTeamHistoryResponse> actual = objectMapper.readValue(
                jsonResponse,
                new TypeReference<CommonResponse<AddTeamHistoryResponse>>() {
                }
        );

        final CommonResponse<AddTeamHistoryResponse> expected = CommonResponse.onSuccess(addTeamHistoryResponse);

        // then
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @DisplayName("회원이 팀의 연혁을 단일 수정할 수 있다.")
    @Test
    void updateTeamHistory() throws Exception {
        // given
        final TeamHistoryRequestDTO.UpdateTeamHistoryRequest updateTeamHistoryRequest
                = new UpdateTeamHistoryRequest("연혁 이름", "연혁 시작 날짜", "연혁 종료 날짜", false, "연혁 설명");

        final TeamHistoryResponseDTO.UpdateTeamHistoryResponse updateTeamHistoryResponse
                = new UpdateTeamHistoryResponse(1L, "연혁 이름", "연혁 시작 날짜", "연혁 종료 날짜", false, "연혁 설명");

        // when
        when(teamHistoryService.updateTeamHistory(anyLong(), any(), anyLong(), any())).thenReturn(updateTeamHistoryResponse);

        final ResultActions resultActions = performUpdateTeamHistory("리에종", 1L, updateTeamHistoryRequest);

        // then
        final MvcResult mvcResult = resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value("true"))
                .andExpect(jsonPath("$.code").value("1000"))
                .andExpect(jsonPath("$.message").value("요청에 성공하였습니다."))
                .andDo(
                        restDocs.document(
                                pathParameters(
                                        parameterWithName("teamName")
                                                .description("팀 이름"),
                                        parameterWithName("teamHistoryId")
                                                .description("팀 연혁 ID")
                                ),
                                requestFields(
                                        fieldWithPath("historyName")
                                                .type(JsonFieldType.STRING)
                                                .description("팀 연혁 이름"),
                                        fieldWithPath("historyStartDate")
                                                .type(JsonFieldType.STRING)
                                                .description("팀 연혁 시작 날짜"),
                                        fieldWithPath("historyEndDate")
                                                .type(JsonFieldType.STRING)
                                                .description("팀 연혁 종료 날짜"),
                                        fieldWithPath("isHistoryInProgress")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("팀 연혁 진행 여부"),
                                        fieldWithPath("historyDescription")
                                                .type(JsonFieldType.STRING)
                                                .description("팀 연혁 설명")
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

                                        fieldWithPath("result.teamHistoryId")
                                                .type(JsonFieldType.NUMBER)
                                                .description("팀 연혁 ID"),
                                        fieldWithPath("result.historyName")
                                                .type(JsonFieldType.STRING)
                                                .description("팀 연혁 이름"),
                                        fieldWithPath("result.historyStartDate")
                                                .type(JsonFieldType.STRING)
                                                .description("팀 연혁 시작 날짜"),
                                        fieldWithPath("result.historyEndDate")
                                                .type(JsonFieldType.STRING)
                                                .description("팀 연혁 종료 날짜"),
                                        fieldWithPath("result.isHistoryInProgress")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("팀 연혁 진행 여부"),
                                        fieldWithPath("result.historyDescription")
                                                .type(JsonFieldType.STRING)
                                                .description("팀 연혁 설명")
                                )
                        )).andReturn();

        final String jsonResponse = mvcResult.getResponse().getContentAsString();
        final CommonResponse<UpdateTeamHistoryResponse> actual = objectMapper.readValue(
                jsonResponse,
                new TypeReference<CommonResponse<UpdateTeamHistoryResponse>>() {
                }
        );

        final CommonResponse<UpdateTeamHistoryResponse> expected = CommonResponse.onSuccess(updateTeamHistoryResponse);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @DisplayName("회원이 팀의 연혁을 단일 삭제할 수 있다.")
    @Test
    void removeTeamHistory() throws Exception {
        final TeamHistoryResponseDTO.RemoveTeamHistoryResponse removeTeamHistoryResponse
                = new RemoveTeamHistoryResponse(1L);

        when(teamHistoryService.removeTeamHistory(anyLong(), any(), anyLong())).thenReturn(removeTeamHistoryResponse);

        final ResultActions resultActions = performRemoveTeamHistory("리에종", 1L);

        final MvcResult mvcResult = resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value("true"))
                .andExpect(jsonPath("$.code").value("1000"))
                .andExpect(jsonPath("$.message").value("요청에 성공하였습니다."))
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("teamName")
                                        .description("팀 이름"),
                                parameterWithName("teamHistoryId")
                                        .description("팀 연혁 ID")
                        ),
                        responseFields(fieldWithPath("isSuccess")
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
                                fieldWithPath("result.teamHistoryId")
                                        .type(JsonFieldType.NUMBER)
                                        .description("팀 연혁 ID")
                        )
                )).andReturn();

        final String jsonResponse = mvcResult.getResponse().getContentAsString();
        final CommonResponse<RemoveTeamHistoryResponse> actual = objectMapper.readValue(
                jsonResponse,
                new TypeReference<CommonResponse<RemoveTeamHistoryResponse>>() {
                }
        );

        final CommonResponse<RemoveTeamHistoryResponse> expected = CommonResponse.onSuccess(removeTeamHistoryResponse);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

}
