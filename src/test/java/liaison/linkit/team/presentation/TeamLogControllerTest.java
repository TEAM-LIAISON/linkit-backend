package liaison.linkit.team.presentation;

import static liaison.linkit.global.restdocs.RestDocsConfiguration.field;
import static liaison.linkit.profile.domain.type.LogType.GENERAL_LOG;
import static liaison.linkit.profile.domain.type.LogType.REPRESENTATIVE_LOG;
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
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import jakarta.servlet.http.Cookie;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import groovy.util.logging.Slf4j;
import liaison.linkit.common.presentation.CommonResponse;
import liaison.linkit.global.ControllerTest;
import liaison.linkit.login.domain.MemberTokens;
import liaison.linkit.team.business.service.log.TeamLogService;
import liaison.linkit.team.presentation.log.TeamLogController;
import liaison.linkit.team.presentation.log.dto.TeamLogRequestDTO;
import liaison.linkit.team.presentation.log.dto.TeamLogRequestDTO.AddTeamLogRequest;
import liaison.linkit.team.presentation.log.dto.TeamLogRequestDTO.UpdateTeamLogRequest;
import liaison.linkit.team.presentation.log.dto.TeamLogResponseDTO;
import liaison.linkit.team.presentation.log.dto.TeamLogResponseDTO.AddTeamLogBodyImageResponse;
import liaison.linkit.team.presentation.log.dto.TeamLogResponseDTO.AddTeamLogResponse;
import liaison.linkit.team.presentation.log.dto.TeamLogResponseDTO.RemoveTeamLogResponse;
import liaison.linkit.team.presentation.log.dto.TeamLogResponseDTO.TeamLogItem;
import liaison.linkit.team.presentation.log.dto.TeamLogResponseDTO.TeamLogItems;
import liaison.linkit.team.presentation.log.dto.TeamLogResponseDTO.UpdateTeamLogPublicStateResponse;
import liaison.linkit.team.presentation.log.dto.TeamLogResponseDTO.UpdateTeamLogResponse;
import liaison.linkit.team.presentation.log.dto.TeamLogResponseDTO.UpdateTeamLogTypeResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(TeamLogController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
@Slf4j
public class TeamLogControllerTest extends ControllerTest {

    private static final MemberTokens MEMBER_TOKENS =
            new MemberTokens("accessToken", "refreshToken");
    private static final Cookie COOKIE =
            new Cookie("refreshToken", MEMBER_TOKENS.getRefreshToken());

    @Autowired private ObjectMapper objectMapper;

    @MockBean private TeamLogService teamLogService;

    @BeforeEach
    void setUp() {
        given(refreshTokenRepository.existsById(any())).willReturn(true);
        doNothing().when(jwtProvider).validateTokens(any());
        given(jwtProvider.getSubject(any())).willReturn("1");
    }

    private ResultActions performGetTeamLogViewItems(final String teamCode) throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders.get("/api/v1/team/{teamCode}/log", teamCode));
    }

    private ResultActions performGetTeamLogItems(final String teamCode) throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders.get("/api/v1/team/{teamCode}/log", teamCode)
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE));
    }

    private ResultActions performGetTeamLogItem(final String teamCode, final Long teamLogId)
            throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders.get(
                        "/api/v1/team/{teamCode}/log/{teamLogId}", teamCode, teamLogId));
    }

    private ResultActions performGetRepresentTeamLogItem(final String teamCode) throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders.get(
                        "/api/v1/team/{teamCode}/log/represent", teamCode));
    }

    private ResultActions performPostTeamLog(
            final String teamCode, final AddTeamLogRequest addTeamLogRequest) throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders.post("/api/v1/team/{teamCode}/log", teamCode)
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addTeamLogRequest)));
    }

    private ResultActions performUpdateTeamLog(
            final String teamCode,
            final Long teamLogId,
            final UpdateTeamLogRequest updateTeamLogRequest)
            throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders.post(
                                "/api/v1/team/{teamCode}/log/{teamLogId}", teamCode, teamLogId)
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateTeamLogRequest)));
    }

    private ResultActions performDeleteTeamLog(final String teamCode, final Long teamLogId)
            throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders.delete(
                                "/api/v1/team/{teamCode}/log/{teamLogId}", teamCode, teamLogId)
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE));
    }

    private ResultActions performUpdateTeamLogType(final Long teamLogId, final String teamCode)
            throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders.post(
                                "/api/v1/team/{teamCode}/log/type/{teamLogId}", teamCode, teamLogId)
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE));
    }

    private ResultActions performUpdateTeamLogPublicState(
            final String teamCode, final Long teamLogId) throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders.post(
                                "/api/v1/team/{teamCode}/log/state/{teamLogId}",
                                teamCode,
                                teamLogId)
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE));
    }

    @DisplayName("회원이 로그 본문에 들어가는 이미지 첨부를 할 수 있다.")
    @Test
    void addTeamLogBodyImage() throws Exception {
        // given
        final TeamLogResponseDTO.AddTeamLogBodyImageResponse addTeamLogBodyImageResponse =
                new AddTeamLogBodyImageResponse("https://image.linkit.im/logo.png");

        final MockMultipartFile teamLogBodyImage =
                new MockMultipartFile(
                        "teamLogBodyImage",
                        "logo.png",
                        "multipart/form-data",
                        "./src/test/resources/static/images/logo.png".getBytes());
        final String teamCode = "liaison";

        // when
        when(teamLogService.addTeamLogBodyImage(anyLong(), any(), any()))
                .thenReturn(addTeamLogBodyImageResponse);

        final ResultActions resultActions =
                mockMvc.perform(
                        RestDocumentationRequestBuilders.multipart(
                                        "/api/v1/team/{teamCode}/log/body/image", teamCode)
                                .file(teamLogBodyImage)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .characterEncoding("UTF-8")
                                .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                                .cookie(COOKIE));

        // then

        final MvcResult mvcResult =
                resultActions
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.isSuccess").value("true"))
                        .andExpect(jsonPath("$.code").value("1000"))
                        .andExpect(jsonPath("$.message").value("요청에 성공하였습니다."))
                        .andDo(
                                restDocs.document(
                                        pathParameters(
                                                parameterWithName("teamCode")
                                                        .description("팀 아이디 (팀 코드)")),
                                        requestParts(
                                                partWithName("teamLogBodyImage")
                                                        .description("팀 본문 이미지")),
                                        responseFields(
                                                fieldWithPath("isSuccess")
                                                        .type(JsonFieldType.BOOLEAN)
                                                        .description("요청 성공 여부")
                                                        .attributes(
                                                                field("constraint", "boolean 값")),
                                                fieldWithPath("code")
                                                        .type(JsonFieldType.STRING)
                                                        .description("요청 성공 코드")
                                                        .attributes(field("constraint", "문자열")),
                                                fieldWithPath("message")
                                                        .type(JsonFieldType.STRING)
                                                        .description("요청 성공 메시지")
                                                        .attributes(field("constraint", "문자열")),
                                                fieldWithPath("result.teamLogBodyImagePath")
                                                        .type(JsonFieldType.STRING)
                                                        .description("팀 로그 본문 이미지 경로"))))
                        .andReturn();

        // JSON 응답에서 result 객체를 추출 및 검증
        final String jsonResponse = mvcResult.getResponse().getContentAsString();
        final CommonResponse<AddTeamLogBodyImageResponse> actual =
                objectMapper.readValue(
                        jsonResponse,
                        new TypeReference<CommonResponse<AddTeamLogBodyImageResponse>>() {});

        final CommonResponse<AddTeamLogBodyImageResponse> expected =
                CommonResponse.onSuccess(addTeamLogBodyImageResponse);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @DisplayName("회원이 로그 뷰어를 전체 조회할 수 있다. (로그인 이전 가능)")
    @Test
    void getTeamLogViewItems() throws Exception {
        // given
        final TeamLogResponseDTO.TeamLogItems teamLogItems =
                TeamLogResponseDTO.TeamLogItems.builder()
                        .teamLogItems(
                                Arrays.asList(
                                        TeamLogItem.builder()
                                                .teamLogId(1L)
                                                .isLogPublic(true)
                                                .logType(REPRESENTATIVE_LOG)
                                                .modifiedAt(LocalDateTime.now())
                                                .logTitle("로그 제목 1")
                                                .logContent("로그 내용 1")
                                                .build(),
                                        TeamLogItem.builder()
                                                .teamLogId(2L)
                                                .isLogPublic(true)
                                                .logType(GENERAL_LOG)
                                                .modifiedAt(LocalDateTime.now())
                                                .logTitle("로그 제목 2")
                                                .logContent("로그 내용 2")
                                                .build()))
                        .build();
        // when
        when(teamLogService.getTeamLogItems(any(), any())).thenReturn(teamLogItems);

        final ResultActions resultActions = performGetTeamLogViewItems("liaison");

        // then
        final MvcResult mvcResult =
                resultActions
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.isSuccess").value("true"))
                        .andExpect(jsonPath("$.code").value("1000"))
                        .andExpect(jsonPath("$.message").value("요청에 성공하였습니다."))
                        .andDo(
                                restDocs.document(
                                        pathParameters(
                                                parameterWithName("teamCode")
                                                        .description("팀 아이디 (팀 코드)")),
                                        responseFields(
                                                fieldWithPath("isSuccess")
                                                        .type(JsonFieldType.BOOLEAN)
                                                        .description("요청 성공 여부")
                                                        .attributes(
                                                                field("constraint", "boolean 값")),
                                                fieldWithPath("code")
                                                        .type(JsonFieldType.STRING)
                                                        .description("요청 성공 코드")
                                                        .attributes(field("constraint", "문자열")),
                                                fieldWithPath("message")
                                                        .type(JsonFieldType.STRING)
                                                        .description("요청 성공 메시지")
                                                        .attributes(field("constraint", "문자열")),
                                                subsectionWithPath("result.teamLogItems[]")
                                                        .type(JsonFieldType.ARRAY)
                                                        .description("팀 로그 아이템 배열"),
                                                fieldWithPath("result.teamLogItems[].teamLogId")
                                                        .type(JsonFieldType.NUMBER)
                                                        .description("내 로그 ID"),
                                                fieldWithPath("result.teamLogItems[].isLogPublic")
                                                        .type(JsonFieldType.BOOLEAN)
                                                        .description("로그 공개 여부"),
                                                fieldWithPath("result.teamLogItems[].logType")
                                                        .type(JsonFieldType.STRING)
                                                        .description("로그 유형 (대표글 여부)"),
                                                fieldWithPath("result.teamLogItems[].modifiedAt")
                                                        .type(JsonFieldType.STRING)
                                                        .description("로그 수정 시간"),
                                                fieldWithPath("result.teamLogItems[].logTitle")
                                                        .type(JsonFieldType.STRING)
                                                        .description("로그 제목"),
                                                fieldWithPath("result.teamLogItems[].logContent")
                                                        .type(JsonFieldType.STRING)
                                                        .description("로그 내용"))))
                        .andReturn();

        // JSON 응답에서 result 객체를 추출 및 검증
        final String jsonResponse = mvcResult.getResponse().getContentAsString();
        final CommonResponse<TeamLogResponseDTO.TeamLogItems> actual =
                objectMapper.readValue(
                        jsonResponse, new TypeReference<CommonResponse<TeamLogItems>>() {});

        final CommonResponse<TeamLogItems> expected = CommonResponse.onSuccess(teamLogItems);

        // then
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @DisplayName("회원이 나의 로그를 전체 조회할 수 있다.")
    @Test
    void getTeamLogItems() throws Exception {
        // given
        final TeamLogResponseDTO.TeamLogItem firstTeamLogItem =
                TeamLogItem.builder()
                        .isTeamManager(true)
                        .teamLogId(1L)
                        .isLogPublic(true)
                        .logType(REPRESENTATIVE_LOG)
                        .createdAt("1시간 전")
                        .modifiedAt(LocalDateTime.now())
                        .logTitle("로그 제목")
                        .logContent("로그 내용")
                        .logViewCount(10L)
                        .commentCount(15L)
                        .build();

        final TeamLogResponseDTO.TeamLogItem secondTeamLogItem =
                TeamLogItem.builder()
                        .isTeamManager(true)
                        .teamLogId(1L)
                        .isLogPublic(true)
                        .logType(REPRESENTATIVE_LOG)
                        .createdAt("1시간 전")
                        .modifiedAt(LocalDateTime.now())
                        .logTitle("로그 제목")
                        .logContent("로그 내용")
                        .logViewCount(10L)
                        .commentCount(15L)
                        .build();

        final TeamLogResponseDTO.TeamLogItems teamLogItems =
                new TeamLogItems(Arrays.asList(firstTeamLogItem, secondTeamLogItem));

        // when
        when(teamLogService.getTeamLogItems(any(), any())).thenReturn(teamLogItems);

        final ResultActions resultActions = performGetTeamLogItems("liaison");

        // then
        final MvcResult mvcResult =
                resultActions
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.isSuccess").value("true"))
                        .andExpect(jsonPath("$.code").value("1000"))
                        .andExpect(jsonPath("$.message").value("요청에 성공하였습니다."))
                        .andDo(
                                restDocs.document(
                                        pathParameters(
                                                parameterWithName("teamCode")
                                                        .description("팀 아이디 (팀 코드)")),
                                        responseFields(
                                                fieldWithPath("isSuccess")
                                                        .type(JsonFieldType.BOOLEAN)
                                                        .description("요청 성공 여부")
                                                        .attributes(
                                                                field("constraint", "boolean 값")),
                                                fieldWithPath("code")
                                                        .type(JsonFieldType.STRING)
                                                        .description("요청 성공 코드")
                                                        .attributes(field("constraint", "문자열")),
                                                fieldWithPath("message")
                                                        .type(JsonFieldType.STRING)
                                                        .description("요청 성공 메시지")
                                                        .attributes(field("constraint", "문자열")),
                                                subsectionWithPath("result.teamLogItems[]")
                                                        .type(JsonFieldType.ARRAY)
                                                        .description("팀 로그 아이템 배열"),
                                                fieldWithPath("result.teamLogItems[].teamLogId")
                                                        .type(JsonFieldType.NUMBER)
                                                        .description("내 로그 ID"),
                                                fieldWithPath("result.teamLogItems[].isLogPublic")
                                                        .type(JsonFieldType.BOOLEAN)
                                                        .description("로그 공개 여부"),
                                                fieldWithPath("result.teamLogItems[].logType")
                                                        .type(JsonFieldType.STRING)
                                                        .description("로그 유형 (대표글 여부)"),
                                                fieldWithPath("result.teamLogItems[].modifiedAt")
                                                        .type(JsonFieldType.STRING)
                                                        .description("로그 수정 시간"),
                                                fieldWithPath("result.teamLogItems[].logTitle")
                                                        .type(JsonFieldType.STRING)
                                                        .description("로그 제목"),
                                                fieldWithPath("result.teamLogItems[].logContent")
                                                        .type(JsonFieldType.STRING)
                                                        .description("로그 내용"))))
                        .andReturn();

        // JSON 응답에서 result 객체를 추출 및 검증
        final String jsonResponse = mvcResult.getResponse().getContentAsString();
        final CommonResponse<TeamLogResponseDTO.TeamLogItems> actual =
                objectMapper.readValue(
                        jsonResponse, new TypeReference<CommonResponse<TeamLogItems>>() {});

        final CommonResponse<TeamLogItems> expected = CommonResponse.onSuccess(teamLogItems);

        // then
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @DisplayName("회원이 팀 로그를 상세 조회할 수 있다.")
    @Test
    void getTeamLogItem() throws Exception {
        // given
        final TeamLogResponseDTO.TeamLogItem teamLogItem =
                TeamLogItem.builder()
                        .isTeamManager(true)
                        .teamLogId(1L)
                        .isLogPublic(true)
                        .logType(REPRESENTATIVE_LOG)
                        .createdAt("1시간 전")
                        .modifiedAt(LocalDateTime.now())
                        .logTitle("로그 제목")
                        .logContent("로그 내용")
                        .logViewCount(10L)
                        .commentCount(15L)
                        .build();

        // when
        when(teamLogService.getTeamLogItem(any(), any(), anyLong())).thenReturn(teamLogItem);

        final ResultActions resultActions = performGetTeamLogItem("liaison", 1L);

        // then
        final MvcResult mvcResult =
                resultActions
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.isSuccess").value("true"))
                        .andExpect(jsonPath("$.code").value("1000"))
                        .andExpect(jsonPath("$.message").value("요청에 성공하였습니다."))
                        .andDo(
                                restDocs.document(
                                        pathParameters(
                                                parameterWithName("teamCode")
                                                        .description("팀 아이디 (팀 코드)"),
                                                parameterWithName("teamLogId")
                                                        .description("팀 로그 ID")),
                                        responseFields(
                                                fieldWithPath("isSuccess")
                                                        .type(JsonFieldType.BOOLEAN)
                                                        .description("요청 성공 여부")
                                                        .attributes(
                                                                field("constraint", "boolean 값")),
                                                fieldWithPath("code")
                                                        .type(JsonFieldType.STRING)
                                                        .description("요청 성공 코드")
                                                        .attributes(field("constraint", "문자열")),
                                                fieldWithPath("message")
                                                        .type(JsonFieldType.STRING)
                                                        .description("요청 성공 메시지")
                                                        .attributes(field("constraint", "문자열")),
                                                fieldWithPath("result.isTeamManager")
                                                        .type(JsonFieldType.BOOLEAN)
                                                        .description("팀 오너/관리자 여부"),
                                                fieldWithPath("result.teamLogId")
                                                        .type(JsonFieldType.NUMBER)
                                                        .description("내 로그 ID"),
                                                fieldWithPath("result.isLogPublic")
                                                        .type(JsonFieldType.BOOLEAN)
                                                        .description("로그 공개 여부"),
                                                fieldWithPath("result.logType")
                                                        .type(JsonFieldType.STRING)
                                                        .description("로그 유형 (대표글 여부)"),
                                                fieldWithPath("result.createdAt")
                                                        .type(JsonFieldType.STRING)
                                                        .description("로그 생성 시간 (동적)"),
                                                fieldWithPath("result.modifiedAt")
                                                        .type(JsonFieldType.STRING)
                                                        .description("로그 수정 시간"),
                                                fieldWithPath("result.logTitle")
                                                        .type(JsonFieldType.STRING)
                                                        .description("로그 제목"),
                                                fieldWithPath("result.logContent")
                                                        .type(JsonFieldType.STRING)
                                                        .description("로그 내용"),
                                                fieldWithPath("result.logViewCount")
                                                        .type(JsonFieldType.NUMBER)
                                                        .description("로그 조회수"),
                                                fieldWithPath("result.commentCount")
                                                        .type(JsonFieldType.NUMBER)
                                                        .description("로그 댓글수"))))
                        .andReturn();
        final String jsonResponse = mvcResult.getResponse().getContentAsString();
        final CommonResponse<TeamLogResponseDTO.TeamLogItem> actual =
                objectMapper.readValue(
                        jsonResponse, new TypeReference<CommonResponse<TeamLogItem>>() {});

        final CommonResponse<TeamLogItem> expected = CommonResponse.onSuccess(teamLogItem);

        // then
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @DisplayName("회원이 대표글을 조회할 수 있다.")
    @Test
    void getRepresentTeamLogItem() throws Exception {
        // TeamLogItem 생성
        TeamLogResponseDTO.TeamLogItem teamLogItem =
                TeamLogResponseDTO.TeamLogItem.builder()
                        .isTeamManager(true)
                        .teamLogId(1L)
                        .isLogPublic(true)
                        .logType(REPRESENTATIVE_LOG)
                        .modifiedAt(LocalDateTime.now()) // String으로
                        .logTitle("로그 제목")
                        .logContent("로그 내용")
                        .build();

        // TeamLogRepresentItem 생성
        final TeamLogResponseDTO.TeamLogRepresentItem teamLogRepresentItem =
                TeamLogResponseDTO.TeamLogRepresentItem.builder()
                        .isTeamManager(true) // isTeamManger가 아닌 isTeamManager로 수정
                        .isMyTeam(true)
                        .teamLogItems(List.of(teamLogItem)) // Arrays.asList 대신 List.of 사용
                        .build();

        when(teamLogService.getRepresentTeamLogItem(any(), any())).thenReturn(teamLogRepresentItem);

        final ResultActions resultActions = performGetRepresentTeamLogItem("liaison");

        final MvcResult mvcResult =
                resultActions
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.isSuccess").value("true"))
                        .andExpect(jsonPath("$.code").value("1000"))
                        .andExpect(jsonPath("$.message").value("요청에 성공하였습니다."))
                        .andExpect(jsonPath("$.result.isTeamManager").value(true))
                        .andExpect(jsonPath("$.result.teamLogItems[0].teamLogId").value(1))
                        .andExpect(jsonPath("$.result.teamLogItems[0].isLogPublic").value(true))
                        .andDo(
                                restDocs.document(
                                        pathParameters(
                                                parameterWithName("teamCode")
                                                        .description("팀 아이디 (팀 코드)")),
                                        responseFields(
                                                fieldWithPath("isSuccess")
                                                        .type(JsonFieldType.BOOLEAN)
                                                        .description("요청 성공 여부")
                                                        .attributes(
                                                                field("constraint", "boolean 값")),
                                                fieldWithPath("code")
                                                        .type(JsonFieldType.STRING)
                                                        .description("요청 성공 코드")
                                                        .attributes(field("constraint", "문자열")),
                                                fieldWithPath("message")
                                                        .type(JsonFieldType.STRING)
                                                        .description("요청 성공 메시지")
                                                        .attributes(field("constraint", "문자열")),
                                                fieldWithPath("result.isTeamManager")
                                                        .type(JsonFieldType.BOOLEAN)
                                                        .description("팀 오너/관리자 여부"),
                                                fieldWithPath("result.isMyTeam")
                                                        .type(JsonFieldType.BOOLEAN)
                                                        .description("팀원 여부"),
                                                fieldWithPath("result.teamLogItems")
                                                        .type(JsonFieldType.ARRAY)
                                                        .description("팀 로그 목록"),
                                                fieldWithPath("result.teamLogItems[].teamLogId")
                                                        .type(JsonFieldType.NUMBER)
                                                        .description("로그 ID"),
                                                fieldWithPath("result.teamLogItems[].isTeamManager")
                                                        .type(JsonFieldType.BOOLEAN)
                                                        .description("팀 관리자 여부"),
                                                fieldWithPath("result.teamLogItems[].isLogPublic")
                                                        .type(JsonFieldType.BOOLEAN)
                                                        .description("로그 공개 여부"),
                                                fieldWithPath("result.teamLogItems[].logType")
                                                        .type(JsonFieldType.STRING)
                                                        .description("로그 유형 (대표글 여부)"),
                                                fieldWithPath("result.teamLogItems[].modifiedAt")
                                                        .type(JsonFieldType.STRING)
                                                        .description("로그 수정 시간"),
                                                fieldWithPath("result.teamLogItems[].logTitle")
                                                        .type(JsonFieldType.STRING)
                                                        .description("로그 제목"),
                                                fieldWithPath("result.teamLogItems[].logContent")
                                                        .type(JsonFieldType.STRING)
                                                        .description("로그 내용"))))
                        .andReturn();

        final String jsonResponse = mvcResult.getResponse().getContentAsString();
        final CommonResponse<TeamLogResponseDTO.TeamLogRepresentItem> actual =
                objectMapper.readValue(
                        jsonResponse,
                        new TypeReference<
                                CommonResponse<TeamLogResponseDTO.TeamLogRepresentItem>>() {});

        final CommonResponse<TeamLogResponseDTO.TeamLogRepresentItem> expected =
                CommonResponse.onSuccess(teamLogRepresentItem);

        // then
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @DisplayName("회원이 로그를 추가할 수 있다.")
    @Test
    void addTeamLog() throws Exception {
        // given
        final TeamLogRequestDTO.AddTeamLogRequest addTeamLogRequest =
                new AddTeamLogRequest("제목제목제목", "내용내용내용", true);

        final TeamLogResponseDTO.AddTeamLogResponse addTeamLogResponse =
                new AddTeamLogResponse(
                        1L, "제목제목제목", "내용내용내용", LocalDateTime.now(), GENERAL_LOG, true);
        // when
        when(teamLogService.addTeamLog(anyLong(), any(), any())).thenReturn(addTeamLogResponse);

        final ResultActions resultActions = performPostTeamLog("liaison", addTeamLogRequest);

        // then
        final MvcResult mvcResult =
                resultActions
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.isSuccess").value("true"))
                        .andExpect(jsonPath("$.code").value("1000"))
                        .andExpect(jsonPath("$.message").value("요청에 성공하였습니다."))
                        .andDo(
                                restDocs.document(
                                        pathParameters(
                                                parameterWithName("teamCode")
                                                        .description("팀 아이디 (팀 코드)")),
                                        requestFields(
                                                fieldWithPath("logTitle")
                                                        .type(JsonFieldType.STRING)
                                                        .description("로그 제목"),
                                                fieldWithPath("logContent")
                                                        .type(JsonFieldType.STRING)
                                                        .description("로그 내용"),
                                                fieldWithPath("isLogPublic")
                                                        .type(JsonFieldType.BOOLEAN)
                                                        .description("로그 공개 여부")),
                                        responseFields(
                                                fieldWithPath("isSuccess")
                                                        .type(JsonFieldType.BOOLEAN)
                                                        .description("요청 성공 여부")
                                                        .attributes(
                                                                field("constraint", "boolean 값")),
                                                fieldWithPath("code")
                                                        .type(JsonFieldType.STRING)
                                                        .description("요청 성공 코드")
                                                        .attributes(field("constraint", "문자열")),
                                                fieldWithPath("message")
                                                        .type(JsonFieldType.STRING)
                                                        .description("요청 성공 메시지")
                                                        .attributes(field("constraint", "문자열")),
                                                fieldWithPath("result.teamLogId")
                                                        .type(JsonFieldType.NUMBER)
                                                        .description("내 로그 ID"),
                                                fieldWithPath("result.logTitle")
                                                        .type(JsonFieldType.STRING)
                                                        .description("로그 제목"),
                                                fieldWithPath("result.logContent")
                                                        .type(JsonFieldType.STRING)
                                                        .description("로그 내용"),
                                                fieldWithPath("result.createdAt")
                                                        .type(JsonFieldType.STRING)
                                                        .description("로그 생성 시간"),
                                                fieldWithPath("result.logType")
                                                        .type(JsonFieldType.STRING)
                                                        .description("로그 유형 (대표글 여부)"),
                                                fieldWithPath("result.isLogPublic")
                                                        .type(JsonFieldType.BOOLEAN)
                                                        .description("로그 공개 여부"))))
                        .andReturn();

        // JSON 응답에서 result 객체를 추출 및 검증
        final String jsonResponse = mvcResult.getResponse().getContentAsString();
        final CommonResponse<TeamLogResponseDTO.AddTeamLogResponse> actual =
                objectMapper.readValue(
                        jsonResponse, new TypeReference<CommonResponse<AddTeamLogResponse>>() {});

        final CommonResponse<AddTeamLogResponse> expected =
                CommonResponse.onSuccess(addTeamLogResponse);

        // then
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @DisplayName("회원이 로그를 추가할 수 있다.")
    @Test
    void updateTeamLog() throws Exception {
        // given
        final TeamLogRequestDTO.UpdateTeamLogRequest updateTeamLogRequest =
                new UpdateTeamLogRequest("제목제목제목", "내용내용내용", true);

        final TeamLogResponseDTO.UpdateTeamLogResponse updateTeamLogResponse =
                new UpdateTeamLogResponse(
                        1L, "제목제목제목", "내용내용내용", LocalDateTime.now(), GENERAL_LOG, true);
        // when
        when(teamLogService.updateTeamLog(anyLong(), any(), anyLong(), any()))
                .thenReturn(updateTeamLogResponse);

        final ResultActions resultActions =
                performUpdateTeamLog("liaison", 1L, updateTeamLogRequest);

        // then
        final MvcResult mvcResult =
                resultActions
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.isSuccess").value("true"))
                        .andExpect(jsonPath("$.code").value("1000"))
                        .andExpect(jsonPath("$.message").value("요청에 성공하였습니다."))
                        .andDo(
                                restDocs.document(
                                        pathParameters(
                                                parameterWithName("teamCode")
                                                        .description("팀 아이디 (팀 코드)"),
                                                parameterWithName("teamLogId")
                                                        .description("팀 로그 ID")),
                                        requestFields(
                                                fieldWithPath("logTitle")
                                                        .type(JsonFieldType.STRING)
                                                        .description("로그 제목"),
                                                fieldWithPath("logContent")
                                                        .type(JsonFieldType.STRING)
                                                        .description("로그 내용"),
                                                fieldWithPath("isLogPublic")
                                                        .type(JsonFieldType.BOOLEAN)
                                                        .description("로그 공개 여부")),
                                        responseFields(
                                                fieldWithPath("isSuccess")
                                                        .type(JsonFieldType.BOOLEAN)
                                                        .description("요청 성공 여부")
                                                        .attributes(
                                                                field("constraint", "boolean 값")),
                                                fieldWithPath("code")
                                                        .type(JsonFieldType.STRING)
                                                        .description("요청 성공 코드")
                                                        .attributes(field("constraint", "문자열")),
                                                fieldWithPath("message")
                                                        .type(JsonFieldType.STRING)
                                                        .description("요청 성공 메시지")
                                                        .attributes(field("constraint", "문자열")),
                                                fieldWithPath("result.teamLogId")
                                                        .type(JsonFieldType.NUMBER)
                                                        .description("내 로그 ID"),
                                                fieldWithPath("result.logTitle")
                                                        .type(JsonFieldType.STRING)
                                                        .description("로그 제목"),
                                                fieldWithPath("result.logContent")
                                                        .type(JsonFieldType.STRING)
                                                        .description("로그 내용"),
                                                fieldWithPath("result.createdAt")
                                                        .type(JsonFieldType.STRING)
                                                        .description("로그 생성 시간"),
                                                fieldWithPath("result.logType")
                                                        .type(JsonFieldType.STRING)
                                                        .description("로그 유형 (대표글 여부)"),
                                                fieldWithPath("result.isLogPublic")
                                                        .type(JsonFieldType.BOOLEAN)
                                                        .description("로그 공개 여부"))))
                        .andReturn();

        // JSON 응답에서 result 객체를 추출 및 검증
        final String jsonResponse = mvcResult.getResponse().getContentAsString();
        final CommonResponse<TeamLogResponseDTO.UpdateTeamLogResponse> actual =
                objectMapper.readValue(
                        jsonResponse,
                        new TypeReference<CommonResponse<UpdateTeamLogResponse>>() {});

        final CommonResponse<UpdateTeamLogResponse> expected =
                CommonResponse.onSuccess(updateTeamLogResponse);

        // then
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @DisplayName("회원이 나의 로그를 단일 삭제할 수 있다.")
    @Test
    void removeTeamLog() throws Exception {
        // given
        final TeamLogResponseDTO.RemoveTeamLogResponse removeTeamLogResponse =
                new TeamLogResponseDTO.RemoveTeamLogResponse(1L);

        // when
        when(teamLogService.removeTeamLog(anyLong(), any(), anyLong()))
                .thenReturn(removeTeamLogResponse);

        final ResultActions resultActions = performDeleteTeamLog("liaison", 1L);

        // then
        final MvcResult mvcResult =
                resultActions
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.isSuccess").value("true"))
                        .andExpect(jsonPath("$.code").value("1000"))
                        .andExpect(jsonPath("$.message").value("요청에 성공하였습니다."))
                        .andDo(
                                restDocs.document(
                                        pathParameters(
                                                parameterWithName("teamCode")
                                                        .description("팀 아이디 (팀 코드)"),
                                                parameterWithName("teamLogId")
                                                        .description("팀 로그 ID")),
                                        responseFields(
                                                fieldWithPath("isSuccess")
                                                        .type(JsonFieldType.BOOLEAN)
                                                        .description("요청 성공 여부")
                                                        .attributes(
                                                                field("constraint", "boolean 값")),
                                                fieldWithPath("code")
                                                        .type(JsonFieldType.STRING)
                                                        .description("요청 성공 코드")
                                                        .attributes(field("constraint", "문자열")),
                                                fieldWithPath("message")
                                                        .type(JsonFieldType.STRING)
                                                        .description("요청 성공 메시지")
                                                        .attributes(field("constraint", "문자열")),
                                                fieldWithPath("result.teamLogId")
                                                        .type(JsonFieldType.NUMBER)
                                                        .description("해당 팀 로그 ID"))))
                        .andReturn();
        // JSON 응답에서 result 객체를 추출 및 검증
        final String jsonResponse = mvcResult.getResponse().getContentAsString();
        final CommonResponse<TeamLogResponseDTO.RemoveTeamLogResponse> actual =
                objectMapper.readValue(
                        jsonResponse,
                        new TypeReference<CommonResponse<RemoveTeamLogResponse>>() {});

        final CommonResponse<RemoveTeamLogResponse> expected =
                CommonResponse.onSuccess(removeTeamLogResponse);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @DisplayName("회원이 나의 로그를 대표글 설정을 변경할 수 있다.")
    @Test
    void updateTeamLogType() throws Exception {
        // given

        final TeamLogResponseDTO.UpdateTeamLogTypeResponse updateTeamLogTypeResponse =
                new UpdateTeamLogTypeResponse(1L, GENERAL_LOG);

        // when
        when(teamLogService.updateTeamLogType(anyLong(), any(), anyLong()))
                .thenReturn(updateTeamLogTypeResponse);

        final ResultActions resultActions = performUpdateTeamLogType(1L, "liaison");

        // then
        final MvcResult mvcResult =
                resultActions
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.isSuccess").value("true"))
                        .andExpect(jsonPath("$.code").value("1000"))
                        .andExpect(jsonPath("$.message").value("요청에 성공하였습니다."))
                        .andDo(
                                restDocs.document(
                                        pathParameters(
                                                parameterWithName("teamCode")
                                                        .description("팀 아이디 (팀 코드)"),
                                                parameterWithName("teamLogId")
                                                        .description("팀 로그 ID")),
                                        responseFields(
                                                fieldWithPath("isSuccess")
                                                        .type(JsonFieldType.BOOLEAN)
                                                        .description("요청 성공 여부")
                                                        .attributes(
                                                                field("constraint", "boolean 값")),
                                                fieldWithPath("code")
                                                        .type(JsonFieldType.STRING)
                                                        .description("요청 성공 코드")
                                                        .attributes(field("constraint", "문자열")),
                                                fieldWithPath("message")
                                                        .type(JsonFieldType.STRING)
                                                        .description("요청 성공 메시지")
                                                        .attributes(field("constraint", "문자열")),
                                                fieldWithPath("result.teamLogId")
                                                        .type(JsonFieldType.NUMBER)
                                                        .description("해당 로그 ID"),
                                                fieldWithPath("result.logType")
                                                        .type(JsonFieldType.STRING)
                                                        .description("해당 로그 대표글 설정 여부"))))
                        .andReturn();
        // JSON 응답에서 result 객체를 추출 및 검증
        final String jsonResponse = mvcResult.getResponse().getContentAsString();
        final CommonResponse<TeamLogResponseDTO.UpdateTeamLogTypeResponse> actual =
                objectMapper.readValue(
                        jsonResponse,
                        new TypeReference<CommonResponse<UpdateTeamLogTypeResponse>>() {});

        final CommonResponse<UpdateTeamLogTypeResponse> expected =
                CommonResponse.onSuccess(updateTeamLogTypeResponse);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @DisplayName("회원이 팀의 로그를 공개 여부 설정을 변경할 수 있다.")
    @Test
    void updateTeamLogPublicState() throws Exception {
        // given
        final TeamLogResponseDTO.UpdateTeamLogPublicStateResponse updateTeamLogPublicStateResponse =
                new UpdateTeamLogPublicStateResponse(1L, true);

        // when
        when(teamLogService.updateTeamLogPublicState(anyLong(), any(), anyLong()))
                .thenReturn(updateTeamLogPublicStateResponse);

        final ResultActions resultActions = performUpdateTeamLogPublicState("liaison", 1L);

        // then
        final MvcResult mvcResult =
                resultActions
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.isSuccess").value("true"))
                        .andExpect(jsonPath("$.code").value("1000"))
                        .andExpect(jsonPath("$.message").value("요청에 성공하였습니다."))
                        .andDo(
                                restDocs.document(
                                        pathParameters(
                                                parameterWithName("teamCode")
                                                        .description("팀 아이디 (팀 코드)"),
                                                parameterWithName("teamLogId")
                                                        .description("팀 로그 ID")),
                                        responseFields(
                                                fieldWithPath("isSuccess")
                                                        .type(JsonFieldType.BOOLEAN)
                                                        .description("요청 성공 여부")
                                                        .attributes(
                                                                field("constraint", "boolean 값")),
                                                fieldWithPath("code")
                                                        .type(JsonFieldType.STRING)
                                                        .description("요청 성공 코드")
                                                        .attributes(field("constraint", "문자열")),
                                                fieldWithPath("message")
                                                        .type(JsonFieldType.STRING)
                                                        .description("요청 성공 메시지")
                                                        .attributes(field("constraint", "문자열")),
                                                fieldWithPath("result.teamLogId")
                                                        .type(JsonFieldType.NUMBER)
                                                        .description("해당 팀 로그 ID"),
                                                fieldWithPath("result.isLogPublic")
                                                        .type(JsonFieldType.BOOLEAN)
                                                        .description("해당 로그 변경된 로그 공개 여부"))))
                        .andReturn();
        // JSON 응답에서 result 객체를 추출 및 검증
        final String jsonResponse = mvcResult.getResponse().getContentAsString();
        final CommonResponse<TeamLogResponseDTO.UpdateTeamLogPublicStateResponse> actual =
                objectMapper.readValue(
                        jsonResponse,
                        new TypeReference<CommonResponse<UpdateTeamLogPublicStateResponse>>() {});

        final CommonResponse<UpdateTeamLogPublicStateResponse> expected =
                CommonResponse.onSuccess(updateTeamLogPublicStateResponse);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }
}
