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
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import java.util.Arrays;
import java.util.List;
import liaison.linkit.common.presentation.CommonResponse;
import liaison.linkit.global.ControllerTest;
import liaison.linkit.login.domain.MemberTokens;
import liaison.linkit.team.presentation.announcement.TeamMemberAnnouncementController;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementRequestDTO;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementRequestDTO.AddTeamMemberAnnouncementRequest;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementRequestDTO.UpdateTeamMemberAnnouncementRequest;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO.AddTeamMemberAnnouncementResponse;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO.AnnouncementPositionItem;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO.AnnouncementSkillName;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO.RemoveTeamMemberAnnouncementResponse;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO.TeamMemberAnnouncementDetail;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO.TeamMemberAnnouncementItem;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO.TeamMemberAnnouncementItems;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO.TeamMemberAnnouncementViewItem;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO.TeamMemberAnnouncementViewItems;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO.UpdateTeamMemberAnnouncementPublicStateResponse;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO.UpdateTeamMemberAnnouncementResponse;
import liaison.linkit.team.business.service.announcement.TeamMemberAnnouncementService;
import org.jetbrains.annotations.NotNull;
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

@WebMvcTest(TeamMemberAnnouncementController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
public class TeamMemberAnnouncementControllerTest extends ControllerTest {
    private static final MemberTokens MEMBER_TOKENS = new MemberTokens("accessToken", "refreshToken");
    private static final Cookie COOKIE = new Cookie("refreshToken", MEMBER_TOKENS.getRefreshToken());

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TeamMemberAnnouncementService teamMemberAnnouncementService;

    @BeforeEach
    void setUp() {
        given(refreshTokenRepository.existsById(any())).willReturn(true);
        doNothing().when(jwtProvider).validateTokens(any());
        given(jwtProvider.getSubject(any())).willReturn("1");
    }

    // 팀원 공고 뷰어 전체 조회
    private ResultActions performGetLoggedOutTeamMemberAnnouncementViewItems(final String teamName) throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders.get("/api/v1/team/{teamName}/announcement/view", teamName)
        );
    }

    // 팀원 공고 전체 조회
    private ResultActions performGetTeamMemberAnnouncementItems(final String teamName) throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders.get("/api/v1/team/{teamName}/announcement", teamName)
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
        );
    }

    // 팀원 공고 단일 조회
    private ResultActions performGetTeamMemberAnnouncementDetail(final String teamName, final Long teamMemberAnnouncementId) throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders.get("/api/v1/team/{teamName}/announcement/{teamMemberAnnouncementId}", teamName, teamMemberAnnouncementId)
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
        );
    }

    // 팀원 공고 생성
    private ResultActions performAddTeamMemberAnnouncement(final String teamName, final AddTeamMemberAnnouncementRequest request) throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders.post("/api/v1/team/{teamName}/announcement", teamName)
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(objectMapper.writeValueAsString(request))
        );
    }

    // 팀원 공고 수정
    private ResultActions performUpdateTeamMemberAnnouncement(final String teamName, final Long teamMemberAnnouncementId, final UpdateTeamMemberAnnouncementRequest request) throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders.post("/api/v1/team/{teamName}/announcement/{teamMemberAnnouncementId}", teamName, teamMemberAnnouncementId)
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));
    }

    // 팀원 공고 삭제
    private ResultActions performRemoveTeamMemberAnnouncement(final String teamName, final Long teamMemberAnnouncementId) throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders.delete("/api/v1/team/{teamName}/announcement/{teamMemberAnnouncementId}", teamName, teamMemberAnnouncementId)
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE));
    }

    // 팀원 공고 공개/비공개 여부 수정
    private ResultActions performUpdateTeamMemberAnnouncementPublicState(final String teamName, final Long teamMemberAnnouncementId) throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders.post("/api/v1/team/{teamName}/announcement/state/{teamMemberAnnouncementId}", teamName, teamMemberAnnouncementId)
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE));
    }

    @DisplayName("회원이 팀의 팀원 공고 뷰어를 전체 조회할 수 있다.")
    @Test
    void getLoggedOutTeamMemberAnnouncementViewItems() throws Exception {
        // given
        final TeamMemberAnnouncementViewItems teamMemberAnnouncementViewItems = TeamMemberAnnouncementViewItems.builder()
                .teamMemberAnnouncementViewItems(Arrays.asList(
                        TeamMemberAnnouncementViewItem.builder()
                                .teamMemberAnnouncementId(1L)
                                .announcementDDay(19)
                                .announcementTitle("공고 제목")
                                .majorPosition("포지션 대분류")
                                .announcementSkillNames(
                                        Arrays.asList(
                                                TeamMemberAnnouncementResponseDTO.AnnouncementSkillName.builder()
                                                        .announcementSkillName("공고 스킬 이름 1")
                                                        .build(),
                                                TeamMemberAnnouncementResponseDTO.AnnouncementSkillName.builder()
                                                        .announcementSkillName("공고 스킬 이름 2")
                                                        .build()
                                        )
                                )
                                .isAnnouncementPublic(true)
                                .isAnnouncementInProgress(false)
                                .isAnnouncementScrap(true)
                                .announcementScrapCount(100)
                                .build(),
                        TeamMemberAnnouncementViewItem.builder()
                                .teamMemberAnnouncementId(2L)
                                .announcementDDay(20)
                                .announcementTitle("공고 제목 2")
                                .majorPosition("포지션 대분류")
                                .announcementSkillNames(
                                        Arrays.asList(
                                                TeamMemberAnnouncementResponseDTO.AnnouncementSkillName.builder()
                                                        .announcementSkillName("공고 스킬 이름 3")
                                                        .build(),
                                                TeamMemberAnnouncementResponseDTO.AnnouncementSkillName.builder()
                                                        .announcementSkillName("공고 스킬 이름 4")
                                                        .build()
                                        )
                                )
                                .isAnnouncementPublic(true)
                                .isAnnouncementInProgress(false)
                                .isAnnouncementScrap(true)
                                .announcementScrapCount(200)
                                .build()
                ))
                .build();

        // when
        when(teamMemberAnnouncementService.getLoggedOutTeamMemberAnnouncementViewItems(any())).thenReturn(teamMemberAnnouncementViewItems);

        final ResultActions resultActions = performGetLoggedOutTeamMemberAnnouncementViewItems("liaison");

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
                                        fieldWithPath("result")
                                                .type(JsonFieldType.OBJECT)
                                                .description("응답 데이터 객체"),
                                        fieldWithPath("result.teamMemberAnnouncementViewItems")
                                                .type(JsonFieldType.ARRAY)
                                                .description("팀원 공고 ViewItems 리스트"),
                                        fieldWithPath("result.teamMemberAnnouncementViewItems[].teamMemberAnnouncementId")
                                                .type(JsonFieldType.NUMBER)
                                                .description("팀원 공고 ID"),
                                        fieldWithPath("result.teamMemberAnnouncementViewItems[].announcementDDay")
                                                .type(JsonFieldType.NUMBER)
                                                .description("팀원 공고 마감 디데이"),
                                        fieldWithPath("result.teamMemberAnnouncementViewItems[].announcementTitle")
                                                .type(JsonFieldType.STRING)
                                                .description("공고 제목"),
                                        fieldWithPath("result.teamMemberAnnouncementViewItems[].majorPosition")
                                                .type(JsonFieldType.STRING)
                                                .description("포지션 대분류"),
                                        fieldWithPath("result.teamMemberAnnouncementViewItems[].announcementSkillNames")
                                                .type(JsonFieldType.ARRAY)
                                                .description("공고에 필요한 스킬 이름 리스트"),
                                        fieldWithPath("result.teamMemberAnnouncementViewItems[].announcementSkillNames[].announcementSkillName")
                                                .type(JsonFieldType.STRING)
                                                .description("공고 스킬 이름"),
                                        fieldWithPath("result.teamMemberAnnouncementViewItems[].isAnnouncementPublic")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("공고 공개 여부"),
                                        fieldWithPath("result.teamMemberAnnouncementViewItems[].isAnnouncementInProgress")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("공고 진행 여부"),
                                        fieldWithPath("result.teamMemberAnnouncementViewItems[].isAnnouncementScrap")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("공고 스크랩 여부"),
                                        fieldWithPath("result.teamMemberAnnouncementViewItems[].announcementScrapCount")
                                                .type(JsonFieldType.NUMBER)
                                                .description("공고 스크랩된 횟수")
                                )
                        )
                ).andReturn();

        final String jsonResponse = mvcResult.getResponse().getContentAsString();
        final CommonResponse<TeamMemberAnnouncementViewItems> actual = objectMapper.readValue(
                jsonResponse,
                new TypeReference<CommonResponse<TeamMemberAnnouncementViewItems>>() {
                }
        );

        final CommonResponse<TeamMemberAnnouncementViewItems> expected = CommonResponse.onSuccess(teamMemberAnnouncementViewItems);

        // then
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @DisplayName("회원이 팀의 팀원 공고를 전체 조회할 수 있다.")
    @Test
    void getTeamMemberAnnouncementItems() throws Exception {
        // given
        final TeamMemberAnnouncementItem firstTeamMemberAnnouncementItem = TeamMemberAnnouncementItem.builder()
                .teamMemberAnnouncementId(1L)
                .announcementTitle("팀원 공고 제목 1")
                .majorPosition("포지션 대분류")
                .announcementSkillNames(Arrays.asList(
                        AnnouncementSkillName.builder()
                                .announcementSkillName("스킬 이름 1")
                                .build(),
                        AnnouncementSkillName.builder()
                                .announcementSkillName("스킬 이름 2")
                                .build()
                ))
                .isAnnouncementPublic(true)
                .isAnnouncementInProgress(false)
                .build();

        final TeamMemberAnnouncementItem secondTeamMemberAnnouncementItem = TeamMemberAnnouncementItem.builder()
                .teamMemberAnnouncementId(1L)
                .announcementTitle("팀원 공고 제목 2")
                .majorPosition("포지션 대분류")
                .announcementSkillNames(Arrays.asList(
                        AnnouncementSkillName.builder()
                                .announcementSkillName("스킬 이름 1")
                                .build(),
                        AnnouncementSkillName.builder()
                                .announcementSkillName("스킬 이름 2")
                                .build()
                ))
                .isAnnouncementPublic(true)
                .isAnnouncementInProgress(false)
                .build();

        final TeamMemberAnnouncementResponseDTO.TeamMemberAnnouncementItems teamMemberAnnouncementItems
                = TeamMemberAnnouncementItems.builder()
                .teamMemberAnnouncementItems(Arrays.asList(firstTeamMemberAnnouncementItem, secondTeamMemberAnnouncementItem))
                .build();

        // when
        when(teamMemberAnnouncementService.getTeamMemberAnnouncementItems(anyLong(), any())).thenReturn(teamMemberAnnouncementItems);

        final ResultActions resultActions = performGetTeamMemberAnnouncementItems("liaison");

        // then
        final MvcResult mvcResult = resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.code").value("1000"))
                .andExpect(jsonPath("$.message").value("요청에 성공하였습니다."))
                .andDo(
                        restDocs.document(
                                pathParameters(
                                        parameterWithName("teamName")
                                                .description("팀 이름")
                                ),
                                responseFields(
                                        fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                        fieldWithPath("code").type(JsonFieldType.STRING).description("요청 성공 코드"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("요청 성공 메시지"),
                                        fieldWithPath("result.teamMemberAnnouncementItems").type(JsonFieldType.ARRAY).description("팀원 공고 목록"),
                                        fieldWithPath("result.teamMemberAnnouncementItems[].teamMemberAnnouncementId").type(JsonFieldType.NUMBER).description("팀원 공고 ID"),
                                        fieldWithPath("result.teamMemberAnnouncementItems[].announcementTitle").type(JsonFieldType.STRING).description("팀원 공고 제목"),
                                        fieldWithPath("result.teamMemberAnnouncementItems[].majorPosition").type(JsonFieldType.STRING).description("포지션 대분류"),
                                        fieldWithPath("result.teamMemberAnnouncementItems[].announcementSkillNames").type(JsonFieldType.ARRAY).description("공고 스킬 목록"),
                                        fieldWithPath("result.teamMemberAnnouncementItems[].announcementSkillNames[].announcementSkillName").type(JsonFieldType.STRING).description("공고 스킬 이름"),
                                        fieldWithPath("result.teamMemberAnnouncementItems[].isAnnouncementPublic").type(JsonFieldType.BOOLEAN).description("공고 공개 여부"),
                                        fieldWithPath("result.teamMemberAnnouncementItems[].isAnnouncementInProgress").type(JsonFieldType.BOOLEAN).description("공고 진행 여부")
                                )
                        )
                ).andReturn();

        final String jsonResponse = mvcResult.getResponse().getContentAsString();
        final CommonResponse<TeamMemberAnnouncementResponseDTO.TeamMemberAnnouncementItems> actual = objectMapper.readValue(
                jsonResponse,
                new TypeReference<CommonResponse<TeamMemberAnnouncementItems>>() {
                }
        );

        final CommonResponse<TeamMemberAnnouncementItems> expected = CommonResponse.onSuccess(teamMemberAnnouncementItems);

        // then
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @DisplayName("회원이 팀의 팀원 공고를 단일 조회할 수 있다.")
    @Test
    void getTeamMemberAnnouncementDetail() throws Exception {
        // given
        final TeamMemberAnnouncementDetail teamMemberAnnouncementDetail
                = TeamMemberAnnouncementDetail.builder()
                .teamMemberAnnouncementId(1L)
                .isAnnouncementScrap(true)
                .announcementScrapCount(100)
                .announcementTitle("팀원 공고 제목")
                .announcementPositionItem(
                        AnnouncementPositionItem.builder()
                                .majorPosition("포지션 대분류")
                                .subPosition("포지션 소분류")
                                .build()
                )
                .announcementSkillNames(
                        Arrays.asList(
                                AnnouncementSkillName.builder()
                                        .announcementSkillName("스킬 이름 1")
                                        .build(),
                                AnnouncementSkillName.builder()
                                        .announcementSkillName("스킬 이름 2")
                                        .build()
                        )
                )
                .announcementStartDate("공고 시작 날짜")
                .announcementEndDate("공고 종료 날짜")
                .isRegionFlexible(true)
                .mainTasks("주요 업무")
                .workMethod("업무 방식")
                .idealCandidate("이런 분을 찾고 있어요")
                .preferredQualifications("이런 분이면 더 좋아요")
                .joiningProcess("이런 과정으로 합류해요")
                .benefits("합류하면 이런 것들을 얻어갈 수 있어요")
                .build();

        // when
        when(teamMemberAnnouncementService.getTeamMemberAnnouncementDetailInLogoutState(any(), anyLong())).thenReturn(teamMemberAnnouncementDetail);

        final ResultActions resultActions = performGetTeamMemberAnnouncementDetail("liaison", 1L);

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
                                        parameterWithName("teamMemberAnnouncementId")
                                                .description("팀원 공고 ID")
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
                                        fieldWithPath("result")
                                                .type(JsonFieldType.OBJECT)
                                                .description("응답 결과"),
                                        fieldWithPath("result.teamMemberAnnouncementId")
                                                .type(JsonFieldType.NUMBER)
                                                .description("팀원 공고 ID"),
                                        fieldWithPath("result.isAnnouncementScrap")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("팀원 공고 스크랩 여부"),
                                        fieldWithPath("result.announcementScrapCount")
                                                .type(JsonFieldType.NUMBER)
                                                .description("팀원 공고 스크랩 수"),
                                        fieldWithPath("result.announcementTitle")
                                                .type(JsonFieldType.STRING)
                                                .description("공고 제목"),
                                        fieldWithPath("result.announcementPositionItem")
                                                .type(JsonFieldType.OBJECT)
                                                .description("공고 포지션 정보"),
                                        fieldWithPath("result.announcementPositionItem.majorPosition")
                                                .type(JsonFieldType.STRING)
                                                .description("공고 포지션 대분류"),
                                        fieldWithPath("result.announcementPositionItem.subPosition")
                                                .type(JsonFieldType.STRING)
                                                .description("공고 포지션 소분류"),
                                        fieldWithPath("result.announcementSkillNames")
                                                .type(JsonFieldType.ARRAY)
                                                .description("공고 스킬 목록"),
                                        fieldWithPath("result.announcementSkillNames[].announcementSkillName")
                                                .type(JsonFieldType.STRING)
                                                .description("공고 스킬 이름"),
                                        fieldWithPath("result.announcementStartDate")
                                                .type(JsonFieldType.STRING)
                                                .description("공고 시작 날짜"),
                                        fieldWithPath("result.announcementEndDate")
                                                .type(JsonFieldType.STRING)
                                                .description("공고 종료 날짜"),
                                        fieldWithPath("result.isRegionFlexible")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("지역 무관 여부"),
                                        fieldWithPath("result.mainTasks")
                                                .type(JsonFieldType.STRING)
                                                .description("주요 업무"),
                                        fieldWithPath("result.workMethod")
                                                .type(JsonFieldType.STRING)
                                                .description("업무 방식"),
                                        fieldWithPath("result.idealCandidate")
                                                .type(JsonFieldType.STRING)
                                                .description("이런 분을 찾고 있어요"),
                                        fieldWithPath("result.preferredQualifications")
                                                .type(JsonFieldType.STRING)
                                                .description("이런 분이면 더 좋아요"),
                                        fieldWithPath("result.joiningProcess")
                                                .type(JsonFieldType.STRING)
                                                .description("이런 과정으로 합류해요"),
                                        fieldWithPath("result.benefits")
                                                .type(JsonFieldType.STRING)
                                                .description("합류하면 이런 것들을 얻어 갈 수 있어요")
                                )
                        )
                ).andReturn();

        final String jsonResponse = mvcResult.getResponse().getContentAsString();
        final CommonResponse<TeamMemberAnnouncementDetail> actual = objectMapper.readValue(
                jsonResponse,
                new TypeReference<CommonResponse<TeamMemberAnnouncementDetail>>() {
                }
        );

        final CommonResponse<TeamMemberAnnouncementDetail> expected = CommonResponse.onSuccess(teamMemberAnnouncementDetail);

        // then
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @DisplayName("회원이 팀의 팀원 공고를 생성할 수 있다.")
    @Test
    void addTeamMemberAnnouncement() throws Exception {
        // given
        final List<TeamMemberAnnouncementRequestDTO.AnnouncementSkillName> announcementSkillNames
                = Arrays.asList(new TeamMemberAnnouncementRequestDTO.AnnouncementSkillName("Java"), new TeamMemberAnnouncementRequestDTO.AnnouncementSkillName("React"));

        final TeamMemberAnnouncementRequestDTO.AddTeamMemberAnnouncementRequest addTeamMemberAnnouncementRequest
                = new AddTeamMemberAnnouncementRequest("공고 제목", "대분류 포지션", "소분류 포지션", announcementSkillNames, "공고 시작 날짜", "공고 종료 날짜", false, "주요 업무", "업무 방식",
                "이런 분을 찾고 있어요", "이런 분이면 더 좋아요", "이런 과정으로 합류해요", "합류하면 이런 것들을 얻어 갈 수 있어요");

        final AddTeamMemberAnnouncementResponse addTeamMemberAnnouncementResponse = getAddTeamMemberAnnouncementResponse();

        // when
        when(teamMemberAnnouncementService.addTeamMemberAnnouncement(anyLong(), any(), any())).thenReturn(addTeamMemberAnnouncementResponse);

        final ResultActions resultActions = performAddTeamMemberAnnouncement("liaison", addTeamMemberAnnouncementRequest);

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
                                        fieldWithPath("announcementTitle")
                                                .type(JsonFieldType.STRING)
                                                .description("팀원 공고 제목"),
                                        fieldWithPath("majorPosition")
                                                .type(JsonFieldType.STRING)
                                                .description("공고 포지션 대분류"),
                                        fieldWithPath("subPosition")
                                                .type(JsonFieldType.STRING)
                                                .description("공고 포지션 소분류"),
                                        fieldWithPath("announcementSkillNames[]")
                                                .type(JsonFieldType.ARRAY)
                                                .description("공고 스킬 배열"),
                                        fieldWithPath("announcementSkillNames[].announcementSkillName")
                                                .type(JsonFieldType.STRING)
                                                .description("공고 스킬 이름"),

                                        fieldWithPath("announcementStartDate")
                                                .type(JsonFieldType.STRING)
                                                .description("공고 시작 날짜"),
                                        fieldWithPath("announcementEndDate")
                                                .type(JsonFieldType.STRING)
                                                .description("공고 종료 날짜"),

                                        fieldWithPath("isRegionFlexible")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("지역 무관 여부"),

                                        fieldWithPath("mainTasks")
                                                .type(JsonFieldType.STRING)
                                                .description("공고 주요 업무"),
                                        fieldWithPath("workMethod")
                                                .type(JsonFieldType.STRING)
                                                .description("공고 업무 방식"),
                                        fieldWithPath("idealCandidate")
                                                .type(JsonFieldType.STRING)
                                                .description("공고 이런 분을 찾고 있어요"),
                                        fieldWithPath("preferredQualifications")
                                                .type(JsonFieldType.STRING)
                                                .description("공고 이런 분이면 더 좋아요"),
                                        fieldWithPath("joiningProcess")
                                                .type(JsonFieldType.STRING)
                                                .description("공고 이런 과정으로 합류해요"),
                                        fieldWithPath("benefits")
                                                .type(JsonFieldType.STRING)
                                                .description("공고 합류하면 이런 것들을 얻어 갈 수 있어요")
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
                                        fieldWithPath("result")
                                                .type(JsonFieldType.OBJECT)
                                                .description("응답 결과"),
                                        fieldWithPath("result.teamMemberAnnouncementId")
                                                .type(JsonFieldType.NUMBER)
                                                .description("팀원 모집 공고 ID"),
                                        fieldWithPath("result.announcementTitle")
                                                .type(JsonFieldType.STRING)
                                                .description("공고 제목"),
                                        fieldWithPath("result.announcementPositionItem")
                                                .type(JsonFieldType.OBJECT)
                                                .description("공고 포지션 정보"),
                                        fieldWithPath("result.announcementPositionItem.majorPosition")
                                                .type(JsonFieldType.STRING)
                                                .description("공고 포지션 대분류"),
                                        fieldWithPath("result.announcementPositionItem.subPosition")
                                                .type(JsonFieldType.STRING)
                                                .description("공고 포지션 소분류"),
                                        fieldWithPath("result.announcementSkillNames")
                                                .type(JsonFieldType.ARRAY)
                                                .description("공고 스킬 목록"),
                                        fieldWithPath("result.announcementSkillNames[].announcementSkillName")
                                                .type(JsonFieldType.STRING)
                                                .description("공고 스킬 이름"),
                                        fieldWithPath("result.announcementStartDate")
                                                .type(JsonFieldType.STRING)
                                                .description("공고 시작 날짜"),
                                        fieldWithPath("result.announcementEndDate")
                                                .type(JsonFieldType.STRING)
                                                .description("공고 종료 날짜"),
                                        fieldWithPath("result.isRegionFlexible")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("지역 무관 여부"),
                                        fieldWithPath("result.mainTasks")
                                                .type(JsonFieldType.STRING)
                                                .description("주요 업무"),
                                        fieldWithPath("result.workMethod")
                                                .type(JsonFieldType.STRING)
                                                .description("업무 방식"),
                                        fieldWithPath("result.idealCandidate")
                                                .type(JsonFieldType.STRING)
                                                .description("이런 분을 찾고 있어요"),
                                        fieldWithPath("result.preferredQualifications")
                                                .type(JsonFieldType.STRING)
                                                .description("이런 분이면 더 좋아요"),
                                        fieldWithPath("result.joiningProcess")
                                                .type(JsonFieldType.STRING)
                                                .description("이런 과정으로 합류해요"),
                                        fieldWithPath("result.benefits")
                                                .type(JsonFieldType.STRING)
                                                .description("합류하면 이런 것들을 얻어 갈 수 있어요")
                                )
                        )
                ).andReturn();

        final String jsonResponse = mvcResult.getResponse().getContentAsString();
        final CommonResponse<AddTeamMemberAnnouncementResponse> actual = objectMapper.readValue(
                jsonResponse,
                new TypeReference<CommonResponse<AddTeamMemberAnnouncementResponse>>() {
                }
        );

        final CommonResponse<AddTeamMemberAnnouncementResponse> expected = CommonResponse.onSuccess(addTeamMemberAnnouncementResponse);

        // then
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @DisplayName("회원이 팀의 팀원 공고를 수정할 수 있다.")
    @Test
    void updateTeamMemberAnnouncement() throws Exception {
        // given
        final List<TeamMemberAnnouncementRequestDTO.AnnouncementSkillName> announcementSkillNames
                = Arrays.asList(new TeamMemberAnnouncementRequestDTO.AnnouncementSkillName("Java"), new TeamMemberAnnouncementRequestDTO.AnnouncementSkillName("React"));

        final TeamMemberAnnouncementRequestDTO.UpdateTeamMemberAnnouncementRequest updateTeamMemberAnnouncementRequest
                = new UpdateTeamMemberAnnouncementRequest("공고 제목", "대분류 포지션", "소분류 포지션", announcementSkillNames, "공고 시작 날짜", "공고 종료 날짜", false, "주요 업무", "업무 방식",
                "이런 분을 찾고 있어요", "이런 분이면 더 좋아요", "이런 과정으로 합류해요", "합류하면 이런 것들을 얻어 갈 수 있어요");

        final UpdateTeamMemberAnnouncementResponse updateTeamMemberAnnouncementResponse = getUpdateTeamMemberAnnouncementResponse();

        // when
        when(teamMemberAnnouncementService.updateTeamMemberAnnouncement(anyLong(), any(), anyLong(), any())).thenReturn(updateTeamMemberAnnouncementResponse);

        final ResultActions resultActions = performUpdateTeamMemberAnnouncement("liaison", 1L, updateTeamMemberAnnouncementRequest);

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
                                        parameterWithName("teamMemberAnnouncementId")
                                                .description("팀원 공고 ID")
                                ),
                                requestFields(
                                        fieldWithPath("announcementTitle")
                                                .type(JsonFieldType.STRING)
                                                .description("팀원 공고 제목"),
                                        fieldWithPath("majorPosition")
                                                .type(JsonFieldType.STRING)
                                                .description("공고 포지션 대분류"),
                                        fieldWithPath("subPosition")
                                                .type(JsonFieldType.STRING)
                                                .description("공고 포지션 소분류"),
                                        fieldWithPath("announcementSkillNames[]")
                                                .type(JsonFieldType.ARRAY)
                                                .description("공고 스킬 배열"),
                                        fieldWithPath("announcementSkillNames[].announcementSkillName")
                                                .type(JsonFieldType.STRING)
                                                .description("공고 스킬 이름"),

                                        fieldWithPath("announcementStartDate")
                                                .type(JsonFieldType.STRING)
                                                .description("공고 시작 날짜"),
                                        fieldWithPath("announcementEndDate")
                                                .type(JsonFieldType.STRING)
                                                .description("공고 종료 날짜"),

                                        fieldWithPath("isRegionFlexible")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("지역 무관 여부"),

                                        fieldWithPath("mainTasks")
                                                .type(JsonFieldType.STRING)
                                                .description("공고 주요 업무"),
                                        fieldWithPath("workMethod")
                                                .type(JsonFieldType.STRING)
                                                .description("공고 업무 방식"),
                                        fieldWithPath("idealCandidate")
                                                .type(JsonFieldType.STRING)
                                                .description("공고 이런 분을 찾고 있어요"),
                                        fieldWithPath("preferredQualifications")
                                                .type(JsonFieldType.STRING)
                                                .description("공고 이런 분이면 더 좋아요"),
                                        fieldWithPath("joiningProcess")
                                                .type(JsonFieldType.STRING)
                                                .description("공고 이런 과정으로 합류해요"),
                                        fieldWithPath("benefits")
                                                .type(JsonFieldType.STRING)
                                                .description("공고 합류하면 이런 것들을 얻어 갈 수 있어요")
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
                                        fieldWithPath("result")
                                                .type(JsonFieldType.OBJECT)
                                                .description("응답 결과"),
                                        fieldWithPath("result.teamMemberAnnouncementId")
                                                .type(JsonFieldType.NUMBER)
                                                .description("팀원 모집 공고 ID"),
                                        fieldWithPath("result.announcementTitle")
                                                .type(JsonFieldType.STRING)
                                                .description("공고 제목"),
                                        fieldWithPath("result.announcementPositionItem")
                                                .type(JsonFieldType.OBJECT)
                                                .description("공고 포지션 정보"),
                                        fieldWithPath("result.announcementPositionItem.majorPosition")
                                                .type(JsonFieldType.STRING)
                                                .description("공고 포지션 대분류"),
                                        fieldWithPath("result.announcementPositionItem.subPosition")
                                                .type(JsonFieldType.STRING)
                                                .description("공고 포지션 소분류"),
                                        fieldWithPath("result.announcementSkillNames")
                                                .type(JsonFieldType.ARRAY)
                                                .description("공고 스킬 목록"),
                                        fieldWithPath("result.announcementSkillNames[].announcementSkillName")
                                                .type(JsonFieldType.STRING)
                                                .description("공고 스킬 이름"),
                                        fieldWithPath("result.announcementStartDate")
                                                .type(JsonFieldType.STRING)
                                                .description("공고 시작 날짜"),
                                        fieldWithPath("result.announcementEndDate")
                                                .type(JsonFieldType.STRING)
                                                .description("공고 종료 날짜"),
                                        fieldWithPath("result.isRegionFlexible")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("지역 무관 여부"),
                                        fieldWithPath("result.mainTasks")
                                                .type(JsonFieldType.STRING)
                                                .description("주요 업무"),
                                        fieldWithPath("result.workMethod")
                                                .type(JsonFieldType.STRING)
                                                .description("업무 방식"),
                                        fieldWithPath("result.idealCandidate")
                                                .type(JsonFieldType.STRING)
                                                .description("이런 분을 찾고 있어요"),
                                        fieldWithPath("result.preferredQualifications")
                                                .type(JsonFieldType.STRING)
                                                .description("이런 분이면 더 좋아요"),
                                        fieldWithPath("result.joiningProcess")
                                                .type(JsonFieldType.STRING)
                                                .description("이런 과정으로 합류해요"),
                                        fieldWithPath("result.benefits")
                                                .type(JsonFieldType.STRING)
                                                .description("합류하면 이런 것들을 얻어 갈 수 있어요")
                                )
                        )
                ).andReturn();

        final String jsonResponse = mvcResult.getResponse().getContentAsString();
        final CommonResponse<UpdateTeamMemberAnnouncementResponse> actual = objectMapper.readValue(
                jsonResponse,
                new TypeReference<CommonResponse<UpdateTeamMemberAnnouncementResponse>>() {
                }
        );

        final CommonResponse<UpdateTeamMemberAnnouncementResponse> expected = CommonResponse.onSuccess(updateTeamMemberAnnouncementResponse);

        // then
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @DisplayName("회원이 팀의 팀원 공고룰 단일 삭제할 수 있다.")
    @Test
    void removeTeamMemberAnnouncement() throws Exception {
        // given
        final TeamMemberAnnouncementResponseDTO.RemoveTeamMemberAnnouncementResponse removeTeamMemberAnnouncementResponse
                = new RemoveTeamMemberAnnouncementResponse(1L);

        // when
        when(teamMemberAnnouncementService.removeTeamMemberAnnouncement(anyLong(), any(), anyLong())).thenReturn(removeTeamMemberAnnouncementResponse);

        final ResultActions resultActions = performRemoveTeamMemberAnnouncement("liaison", 1L);

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
                                        parameterWithName("teamMemberAnnouncementId")
                                                .description("팀원 공고 ID")
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
                                        fieldWithPath("result.teamMemberAnnouncementId")
                                                .type(JsonFieldType.NUMBER)
                                                .description("팀원 모집 공고 ID")
                                )
                        )
                ).andReturn();

        final String jsonResponse = mvcResult.getResponse().getContentAsString();
        final CommonResponse<RemoveTeamMemberAnnouncementResponse> actual = objectMapper.readValue(
                jsonResponse,
                new TypeReference<CommonResponse<RemoveTeamMemberAnnouncementResponse>>() {
                }
        );

        final CommonResponse<RemoveTeamMemberAnnouncementResponse> expected = CommonResponse.onSuccess(removeTeamMemberAnnouncementResponse);

        // then
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @DisplayName("회원이 팀원 공고를 공개 여부 설정을 변경할 수 있다.")
    @Test
    void updateTeamMemberAnnouncementPublicState() throws Exception {
        // given
        final TeamMemberAnnouncementResponseDTO.UpdateTeamMemberAnnouncementPublicStateResponse updateTeamMemberAnnouncementPublicStateResponse
                = new UpdateTeamMemberAnnouncementPublicStateResponse(1L, true);

        // when
        when(teamMemberAnnouncementService.updateTeamMemberAnnouncementPublicState(anyLong(), any(), anyLong())).thenReturn(updateTeamMemberAnnouncementPublicStateResponse);

        final ResultActions resultActions = performUpdateTeamMemberAnnouncementPublicState("liaison", 1L);

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
                                        parameterWithName("teamMemberAnnouncementId")
                                                .description("팀원 공고 ID")
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
                                        fieldWithPath("result.teamMemberAnnouncementId")
                                                .type(JsonFieldType.NUMBER)
                                                .description("해당 팀원 공고 ID"),
                                        fieldWithPath("result.isAnnouncementPublic")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("해당 팀원 공고 변경된 팀원 공고 공개 여부")
                                )
                        )).andReturn();
        // JSON 응답에서 result 객체를 추출 및 검증
        final String jsonResponse = mvcResult.getResponse().getContentAsString();
        final CommonResponse<UpdateTeamMemberAnnouncementPublicStateResponse> actual = objectMapper.readValue(
                jsonResponse,
                new TypeReference<CommonResponse<UpdateTeamMemberAnnouncementPublicStateResponse>>() {
                }
        );

        final CommonResponse<UpdateTeamMemberAnnouncementPublicStateResponse> expected = CommonResponse.onSuccess(updateTeamMemberAnnouncementPublicStateResponse);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @NotNull
    private static AddTeamMemberAnnouncementResponse getAddTeamMemberAnnouncementResponse() {
        final AnnouncementPositionItem announcementPositionItem
                = new AnnouncementPositionItem("대분류 포지션", "소분류 포지션");

        final List<TeamMemberAnnouncementResponseDTO.AnnouncementSkillName> announcementSkillNamesResponse
                = Arrays.asList(new TeamMemberAnnouncementResponseDTO.AnnouncementSkillName("Java"), new TeamMemberAnnouncementResponseDTO.AnnouncementSkillName("React"));

        return new AddTeamMemberAnnouncementResponse(1L, "공고 제목", announcementPositionItem, announcementSkillNamesResponse, "공고 시작 날짜", "공고 종료 날짜", false, "주요 업무",
                "업무 방식", "이런 분을 찾고 있어요", "이런 분이면 더 좋아요", "이런 과정으로 합류해요", "합류하면 이런 것들을 얻어 갈 수 있어요");
    }

    @NotNull
    private static UpdateTeamMemberAnnouncementResponse getUpdateTeamMemberAnnouncementResponse() {
        final AnnouncementPositionItem announcementPositionItem
                = new AnnouncementPositionItem("대분류 포지션", "소분류 포지션");

        final List<TeamMemberAnnouncementResponseDTO.AnnouncementSkillName> announcementSkillNamesResponse
                = Arrays.asList(new TeamMemberAnnouncementResponseDTO.AnnouncementSkillName("Java"), new TeamMemberAnnouncementResponseDTO.AnnouncementSkillName("React"));

        return new UpdateTeamMemberAnnouncementResponse(1L, "공고 제목", announcementPositionItem, announcementSkillNamesResponse, "공고 시작 날짜", "공고 종료 날짜", false, "주요 업무",
                "업무 방식", "이런 분을 찾고 있어요", "이런 분이면 더 좋아요", "이런 과정으로 합류해요", "합류하면 이런 것들을 얻어 갈 수 있어요");
    }
}
