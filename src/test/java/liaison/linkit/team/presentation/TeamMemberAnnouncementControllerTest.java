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
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO.RemoveTeamMemberAnnouncementResponse;
import liaison.linkit.team.presentation.announcement.dto.TeamMemberAnnouncementResponseDTO.UpdateTeamMemberAnnouncementResponse;
import liaison.linkit.team.service.announcement.TeamMemberAnnouncementService;
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

    // 팀원 공고 생성
    private ResultActions performAddTeamMemberAnnouncement(final String teamName, final AddTeamMemberAnnouncementRequest request) throws Exception {
        String encodedTeamName = URLEncoder.encode(teamName, StandardCharsets.UTF_8.toString());

        return mockMvc.perform(
                RestDocumentationRequestBuilders.post("/api/v1/team/{teamName}/announcement", encodedTeamName)
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

    @DisplayName("회원이 팀의 팀원 공고를 생성할 수 있다.")
    @Test
    void addTeamMemberAnnouncement() throws Exception {
        // given
        final List<TeamMemberAnnouncementRequestDTO.AnnouncementSkillName> announcementSkillNames
                = Arrays.asList(new TeamMemberAnnouncementRequestDTO.AnnouncementSkillName("Java"), new TeamMemberAnnouncementRequestDTO.AnnouncementSkillName("React"));

        final TeamMemberAnnouncementRequestDTO.AddTeamMemberAnnouncementRequest addTeamMemberAnnouncementRequest
                = new AddTeamMemberAnnouncementRequest("공고 제목", "대분류 포지션", "소분류 포지션", announcementSkillNames, "공고 시작 날짜", "공고 종료 날짜", "활동 지역 시/도", "활동 지역 시/군/구", false, "주요 업무", "업무 방식",
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

                                        fieldWithPath("cityName")
                                                .type(JsonFieldType.STRING)
                                                .description("공고 활동 지역 시/도"),
                                        fieldWithPath("divisionName")
                                                .type(JsonFieldType.STRING)
                                                .description("공고 활동 지역 시/군/구"),
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
                                        fieldWithPath("result.cityName")
                                                .type(JsonFieldType.STRING)
                                                .description("활동 지역 시/도"),
                                        fieldWithPath("result.divisionName")
                                                .type(JsonFieldType.STRING)
                                                .description("활동 지역 시/군/구"),
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
                = new UpdateTeamMemberAnnouncementRequest("공고 제목", "대분류 포지션", "소분류 포지션", announcementSkillNames, "공고 시작 날짜", "공고 종료 날짜", "활동 지역 시/도", "활동 지역 시/군/구", false, "주요 업무", "업무 방식",
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

                                        fieldWithPath("cityName")
                                                .type(JsonFieldType.STRING)
                                                .description("공고 활동 지역 시/도"),
                                        fieldWithPath("divisionName")
                                                .type(JsonFieldType.STRING)
                                                .description("공고 활동 지역 시/군/구"),
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
                                        fieldWithPath("result.cityName")
                                                .type(JsonFieldType.STRING)
                                                .description("활동 지역 시/도"),
                                        fieldWithPath("result.divisionName")
                                                .type(JsonFieldType.STRING)
                                                .description("활동 지역 시/군/구"),
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

    @NotNull
    private static AddTeamMemberAnnouncementResponse getAddTeamMemberAnnouncementResponse() {
        final AnnouncementPositionItem announcementPositionItem
                = new AnnouncementPositionItem("대분류 포지션", "소분류 포지션");

        final List<TeamMemberAnnouncementResponseDTO.AnnouncementSkillName> announcementSkillNamesResponse
                = Arrays.asList(new TeamMemberAnnouncementResponseDTO.AnnouncementSkillName("Java"), new TeamMemberAnnouncementResponseDTO.AnnouncementSkillName("React"));

        return new AddTeamMemberAnnouncementResponse(1L, "공고 제목", announcementPositionItem, announcementSkillNamesResponse, "공고 시작 날짜", "공고 종료 날짜", "활동 지역 시/도", "활동 지역 시/군/구", false, "주요 업무",
                "업무 방식", "이런 분을 찾고 있어요", "이런 분이면 더 좋아요", "이런 과정으로 합류해요", "합류하면 이런 것들을 얻어 갈 수 있어요");
    }

    @NotNull
    private static UpdateTeamMemberAnnouncementResponse getUpdateTeamMemberAnnouncementResponse() {
        final AnnouncementPositionItem announcementPositionItem
                = new AnnouncementPositionItem("대분류 포지션", "소분류 포지션");

        final List<TeamMemberAnnouncementResponseDTO.AnnouncementSkillName> announcementSkillNamesResponse
                = Arrays.asList(new TeamMemberAnnouncementResponseDTO.AnnouncementSkillName("Java"), new TeamMemberAnnouncementResponseDTO.AnnouncementSkillName("React"));

        return new UpdateTeamMemberAnnouncementResponse(1L, "공고 제목", announcementPositionItem, announcementSkillNamesResponse, "공고 시작 날짜", "공고 종료 날짜", "활동 지역 시/도", "활동 지역 시/군/구", false, "주요 업무",
                "업무 방식", "이런 분을 찾고 있어요", "이런 분이면 더 좋아요", "이런 과정으로 합류해요", "합류하면 이런 것들을 얻어 갈 수 있어요");
    }
}
