package liaison.linkit.search.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;

import jakarta.servlet.http.Cookie;

import com.fasterxml.jackson.databind.ObjectMapper;
import liaison.linkit.common.presentation.RegionResponseDTO.RegionDetail;
import liaison.linkit.global.ControllerTest;
import liaison.linkit.login.domain.MemberTokens;
import liaison.linkit.search.business.service.TeamSearchService;
import liaison.linkit.search.presentation.dto.cursor.CursorRequest;
import liaison.linkit.search.presentation.dto.cursor.CursorResponse;
import liaison.linkit.search.presentation.dto.team.TeamListResponseDTO;
import liaison.linkit.team.presentation.team.dto.TeamResponseDTO.TeamCurrentStateItem;
import liaison.linkit.team.presentation.team.dto.TeamResponseDTO.TeamInformMenu;
import liaison.linkit.team.presentation.team.dto.TeamResponseDTO.TeamScaleItem;
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
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@WebMvcTest(TeamSearchController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
public class TeamSearchControllerTest extends ControllerTest {

    private static final MemberTokens MEMBER_TOKENS =
            new MemberTokens("refreshToken", "accessToken");
    private static final Cookie COOKIE =
            new Cookie("refreshToken", MEMBER_TOKENS.getRefreshToken());

    @Autowired private ObjectMapper objectMapper;

    @MockBean private TeamSearchService teamSearchService;

    // 테스트 헬퍼 메서드도 업데이트 필요
    private ResultActions performSearchTeams(
            List<String> scaleNames,
            List<String> cityNames,
            List<String> teamStateNames,
            CursorRequest cursorRequest)
            throws Exception {

        MockHttpServletRequestBuilder requestBuilder =
                RestDocumentationRequestBuilders.get("/api/v1/team/search");

        // 커서 페이징 요청이 있을 경우
        if (cursorRequest != null) {
            // size 설정
            requestBuilder.param("size", String.valueOf(cursorRequest.size()));

            // cursor가 존재한다면 cursor 파라미터 추가
            if (cursorRequest.cursor() != null) {
                requestBuilder.param("cursor", cursorRequest.cursor());
            }
        }

        // 파라미터가 비어있지 않다면 해당 파라미터를 추가
        if (scaleNames != null && !scaleNames.isEmpty()) {
            scaleNames.forEach(scaleName -> requestBuilder.param("scaleName", scaleName));
        }
        if (cityNames != null && !cityNames.isEmpty()) {
            cityNames.forEach(cityName -> requestBuilder.param("cityName", cityName));
        }
        if (teamStateNames != null && !teamStateNames.isEmpty()) {
            teamStateNames.forEach(
                    teamStateName -> requestBuilder.param("teamStateName", teamStateName));
        }

        return mockMvc.perform(requestBuilder);
    }

    private ResultActions performFeaturedTeams() throws Exception {
        MockHttpServletRequestBuilder requestBuilder =
                RestDocumentationRequestBuilders.get("/api/v1/team/search/featured");

        return mockMvc.perform(requestBuilder);
    }

    @DisplayName("회원/비회원이 팀을 검색할 수 있다. (필터링 가능)")
    @Test
    void searchTeams() throws Exception {
        // given
        CursorRequest cursorRequest = new CursorRequest("teamId", 20);

        TeamInformMenu teamInformMenu1 =
                TeamInformMenu.builder()
                        .teamCurrentStates(
                                Arrays.asList(
                                        TeamCurrentStateItem.builder()
                                                .teamStateName("대회 준비 중")
                                                .build(),
                                        TeamCurrentStateItem.builder()
                                                .teamStateName("투자 유치 중")
                                                .build()))
                        .isTeamScrap(true)
                        .teamScrapCount(100)
                        .teamName("팀 이름 1")
                        .teamCode("팀 아이디 (팀 코드)")
                        .teamShortDescription("팀 한 줄 소개 1")
                        .teamLogoImagePath("팀 로고 이미지 경로 1")
                        .teamScaleItem(TeamScaleItem.builder().teamScaleName("1인").build())
                        .regionDetail(
                                RegionDetail.builder()
                                        .cityName("활동지역 시/도")
                                        .divisionName("활동지역 시/군/구")
                                        .build())
                        .build();

        TeamInformMenu teamInformMenu2 =
                TeamInformMenu.builder()
                        .teamCurrentStates(
                                Arrays.asList(
                                        TeamCurrentStateItem.builder()
                                                .teamStateName("대회 준비 중")
                                                .build(),
                                        TeamCurrentStateItem.builder()
                                                .teamStateName("투자 유치 중")
                                                .build()))
                        .isTeamScrap(false)
                        .teamScrapCount(200)
                        .teamName("팀 이름 2")
                        .teamCode("팀 아이디 2(팀 코드)")
                        .teamShortDescription("팀 한 줄 소개 2")
                        .teamLogoImagePath("팀 로고 이미지 경로 2")
                        .teamScaleItem(TeamScaleItem.builder().teamScaleName("2~5인").build())
                        .regionDetail(
                                RegionDetail.builder()
                                        .cityName("활동지역 시/도")
                                        .divisionName("활동지역 시/군/구")
                                        .build())
                        .build();

        List<TeamInformMenu> teams = Arrays.asList(teamInformMenu1, teamInformMenu2);

        // 커서 기반 페이지네이션으로 변경
        CursorResponse<TeamInformMenu> teamCursorResponse =
                CursorResponse.<TeamInformMenu>builder()
                        .content(teams)
                        .nextCursor("nextTeamId") // 다음 커서 값 설정
                        .hasNext(true) // 다음 페이지가 있음
                        .build();

        CursorResponse<TeamInformMenu> cursorResponse =
                CursorResponse.of(teams, "nextTeamId"); // CursorResponse로 변경

        when(teamSearchService.searchTeamsWithCursor(any(), any(), any(CursorRequest.class)))
                .thenReturn(cursorResponse);

        // when
        final ResultActions resultActions =
                performSearchTeams(
                        Arrays.asList("1인", "2~5인"),
                        Arrays.asList("서울특별시", "부산광역시"),
                        Arrays.asList("팀원 찾는 중", "투자 유치 중"),
                        cursorRequest // cursor 값 사용
                        ); // size는 그대로 유지

        // then
        final MvcResult mvcResult =
                resultActions
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.isSuccess").value(true))
                        .andExpect(jsonPath("$.code").value("1000"))
                        .andExpect(jsonPath("$.message").value("요청에 성공하였습니다."))
                        .andDo(
                                restDocs.document(
                                        queryParameters(
                                                parameterWithName("scaleName")
                                                        .optional()
                                                        .description("팀 규모 (선택적)"),
                                                parameterWithName("cityName")
                                                        .optional()
                                                        .description("시/도 이름 (선택적)"),
                                                parameterWithName("teamStateName")
                                                        .optional()
                                                        .description("팀 상태 이름 (선택적)"),
                                                parameterWithName("cursor")
                                                        .optional()
                                                        .description("마지막으로 조회한 팀의 ID (선택적)"),
                                                parameterWithName("size")
                                                        .optional()
                                                        .description("페이지 크기 (기본값: 20)")),
                                        responseFields(
                                                fieldWithPath("isSuccess")
                                                        .type(JsonFieldType.BOOLEAN)
                                                        .description("요청 성공 여부"),
                                                fieldWithPath("code")
                                                        .type(JsonFieldType.STRING)
                                                        .description("요청 성공 코드"),
                                                fieldWithPath("message")
                                                        .type(JsonFieldType.STRING)
                                                        .description("요청 성공 메시지"),

                                                // ✅ 실제 응답에 맞춰 'defaultTeams' 대신 바로 result.content 로
                                                // 문서화
                                                fieldWithPath("result.content")
                                                        .type(JsonFieldType.ARRAY)
                                                        .description("팀 목록"),
                                                fieldWithPath("result.content[].teamCurrentStates")
                                                        .type(JsonFieldType.ARRAY)
                                                        .description("팀 현재 상태 목록"),
                                                fieldWithPath(
                                                                "result.content[].teamCurrentStates[].teamStateName")
                                                        .type(JsonFieldType.STRING)
                                                        .description("팀 상태 이름"),
                                                fieldWithPath("result.content[].isTeamScrap")
                                                        .type(JsonFieldType.BOOLEAN)
                                                        .description("팀 스크랩 여부"),
                                                fieldWithPath("result.content[].teamScrapCount")
                                                        .type(JsonFieldType.NUMBER)
                                                        .description("팀 스크랩 수"),
                                                fieldWithPath("result.content[].teamName")
                                                        .type(JsonFieldType.STRING)
                                                        .description("팀 이름"),
                                                fieldWithPath("result.content[].teamCode")
                                                        .type(JsonFieldType.STRING)
                                                        .description("팀 아이디 (팀 코드)"),
                                                fieldWithPath(
                                                                "result.content[].teamShortDescription")
                                                        .type(JsonFieldType.STRING)
                                                        .description("팀 한 줄 소개"),
                                                fieldWithPath("result.content[].teamLogoImagePath")
                                                        .type(JsonFieldType.STRING)
                                                        .description("팀 로고 이미지 경로"),
                                                fieldWithPath("result.content[].teamScaleItem")
                                                        .type(JsonFieldType.OBJECT)
                                                        .description("팀 규모 정보"),
                                                fieldWithPath(
                                                                "result.content[].teamScaleItem.teamScaleName")
                                                        .type(JsonFieldType.STRING)
                                                        .description("팀 규모 이름"),
                                                fieldWithPath("result.content[].regionDetail")
                                                        .type(JsonFieldType.OBJECT)
                                                        .description("지역 상세 정보"),
                                                fieldWithPath(
                                                                "result.content[].regionDetail.cityName")
                                                        .type(JsonFieldType.STRING)
                                                        .description("지역 시/도 이름"),
                                                fieldWithPath(
                                                                "result.content[].regionDetail.divisionName")
                                                        .type(JsonFieldType.STRING)
                                                        .description("지역 시/군/구 이름"),

                                                // ✅ 커서 기반 페이지네이션 정보
                                                fieldWithPath("result.nextCursor")
                                                        .type(JsonFieldType.STRING)
                                                        .description("다음 페이지 조회를 위한 커서 값 (팀 코드)"),
                                                fieldWithPath("result.hasNext")
                                                        .type(JsonFieldType.BOOLEAN)
                                                        .description("다음 페이지 존재 여부"))))
                        .andReturn();
    }

    @DisplayName("회원/비회원이 정적 팀을 조회할 수 있다. (필터링 불가)")
    @Test
    void getFeaturedTeams() throws Exception {
        // given
        TeamListResponseDTO teamListResponseDTO =
                TeamListResponseDTO.builder()
                        .ventureTeams(
                                Arrays.asList(
                                        TeamInformMenu.builder()
                                                .teamCurrentStates(
                                                        Arrays.asList(
                                                                TeamCurrentStateItem.builder()
                                                                        .teamStateName("대회 준비 중")
                                                                        .build(),
                                                                TeamCurrentStateItem.builder()
                                                                        .teamStateName("투자 유치 중")
                                                                        .build()))
                                                .isTeamScrap(true)
                                                .teamScrapCount(100)
                                                .teamName("팀 이름 1")
                                                .teamCode("팀 아이디 (팀 코드)")
                                                .teamShortDescription("팀 한 줄 소개 1")
                                                .teamLogoImagePath("팀 로고 이미지 경로 1")
                                                .teamScaleItem(
                                                        TeamScaleItem.builder()
                                                                .teamScaleName("1인")
                                                                .build())
                                                .regionDetail(
                                                        RegionDetail.builder()
                                                                .cityName("활동지역 시/도")
                                                                .divisionName("활동지역 시/군/구")
                                                                .build())
                                                .build(),
                                        TeamInformMenu.builder()
                                                .teamCurrentStates(
                                                        Arrays.asList(
                                                                TeamCurrentStateItem.builder()
                                                                        .teamStateName("대회 준비 중")
                                                                        .build(),
                                                                TeamCurrentStateItem.builder()
                                                                        .teamStateName("투자 유치 중")
                                                                        .build()))
                                                .isTeamScrap(false)
                                                .teamScrapCount(200)
                                                .teamName("팀 이름 2")
                                                .teamCode("팀 아이디 2(팀 코드)")
                                                .teamShortDescription("팀 한 줄 소개 2")
                                                .teamLogoImagePath("팀 로고 이미지 경로 2")
                                                .teamScaleItem(
                                                        TeamScaleItem.builder()
                                                                .teamScaleName("2~5인")
                                                                .build())
                                                .regionDetail(
                                                        RegionDetail.builder()
                                                                .cityName("활동지역 시/도")
                                                                .divisionName("활동지역 시/군/구")
                                                                .build())
                                                .build()))
                        .supportProjectTeams(
                                Arrays.asList(
                                        TeamInformMenu.builder()
                                                .teamCurrentStates(
                                                        Arrays.asList(
                                                                TeamCurrentStateItem.builder()
                                                                        .teamStateName("대회 준비 중")
                                                                        .build(),
                                                                TeamCurrentStateItem.builder()
                                                                        .teamStateName("투자 유치 중")
                                                                        .build()))
                                                .isTeamScrap(true)
                                                .teamScrapCount(100)
                                                .teamName("팀 이름 1")
                                                .teamCode("팀 아이디 (팀 코드)")
                                                .teamShortDescription("팀 한 줄 소개 1")
                                                .teamLogoImagePath("팀 로고 이미지 경로 1")
                                                .teamScaleItem(
                                                        TeamScaleItem.builder()
                                                                .teamScaleName("1인")
                                                                .build())
                                                .regionDetail(
                                                        RegionDetail.builder()
                                                                .cityName("활동지역 시/도")
                                                                .divisionName("활동지역 시/군/구")
                                                                .build())
                                                .build(),
                                        TeamInformMenu.builder()
                                                .teamCurrentStates(
                                                        Arrays.asList(
                                                                TeamCurrentStateItem.builder()
                                                                        .teamStateName("대회 준비 중")
                                                                        .build(),
                                                                TeamCurrentStateItem.builder()
                                                                        .teamStateName("투자 유치 중")
                                                                        .build()))
                                                .isTeamScrap(false)
                                                .teamScrapCount(200)
                                                .teamName("팀 이름 2")
                                                .teamCode("팀 아이디 2(팀 코드)")
                                                .teamShortDescription("팀 한 줄 소개 2")
                                                .teamLogoImagePath("팀 로고 이미지 경로 2")
                                                .teamScaleItem(
                                                        TeamScaleItem.builder()
                                                                .teamScaleName("2~5인")
                                                                .build())
                                                .regionDetail(
                                                        RegionDetail.builder()
                                                                .cityName("활동지역 시/도")
                                                                .divisionName("활동지역 시/군/구")
                                                                .build())
                                                .build()))
                        .build();

        when(teamSearchService.getFeaturedTeams(any())).thenReturn(teamListResponseDTO);

        // when
        final ResultActions resultActions = performFeaturedTeams();

        // then
        final MvcResult mvcResult =
                resultActions
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.isSuccess").value(true))
                        .andExpect(jsonPath("$.code").value("1000"))
                        .andExpect(jsonPath("$.message").value("요청에 성공하였습니다."))
                        .andDo(
                                restDocs.document(
                                        responseFields(
                                                fieldWithPath("isSuccess")
                                                        .type(JsonFieldType.BOOLEAN)
                                                        .description("요청 성공 여부"),
                                                fieldWithPath("code")
                                                        .type(JsonFieldType.STRING)
                                                        .description("요청 성공 코드"),
                                                fieldWithPath("message")
                                                        .type(JsonFieldType.STRING)
                                                        .description("요청 성공 메시지"),

                                                // ✅ 상단: 창업을 위한 팀원을 찾고 있어요 4팀
                                                fieldWithPath("result.ventureTeams")
                                                        .type(JsonFieldType.ARRAY)
                                                        .description(
                                                                "창업을 위한 팀원을 찾고 있어요 팀 목록 (최대 4팀)"),
                                                fieldWithPath(
                                                                "result.ventureTeams[].teamCurrentStates")
                                                        .type(JsonFieldType.ARRAY)
                                                        .description("팀 현재 상태 목록"),
                                                fieldWithPath(
                                                                "result.ventureTeams[].teamCurrentStates[].teamStateName")
                                                        .type(JsonFieldType.STRING)
                                                        .description("팀 상태 이름"),
                                                fieldWithPath("result.ventureTeams[].isTeamScrap")
                                                        .type(JsonFieldType.BOOLEAN)
                                                        .description("팀 스크랩 여부"),
                                                fieldWithPath(
                                                                "result.ventureTeams[].teamScrapCount")
                                                        .type(JsonFieldType.NUMBER)
                                                        .description("팀 스크랩 수"),
                                                fieldWithPath("result.ventureTeams[].teamName")
                                                        .type(JsonFieldType.STRING)
                                                        .description("팀 이름"),
                                                fieldWithPath("result.ventureTeams[].teamCode")
                                                        .type(JsonFieldType.STRING)
                                                        .description("팀 아이디 (팀 코드)"),
                                                fieldWithPath(
                                                                "result.ventureTeams[].teamShortDescription")
                                                        .type(JsonFieldType.STRING)
                                                        .description("팀 한 줄 소개"),
                                                fieldWithPath(
                                                                "result.ventureTeams[].teamLogoImagePath")
                                                        .type(JsonFieldType.STRING)
                                                        .description("팀 로고 이미지 경로"),
                                                fieldWithPath("result.ventureTeams[].teamScaleItem")
                                                        .type(JsonFieldType.OBJECT)
                                                        .description("팀 규모 정보"),
                                                fieldWithPath(
                                                                "result.ventureTeams[].teamScaleItem.teamScaleName")
                                                        .type(JsonFieldType.STRING)
                                                        .description("팀 규모 이름"),
                                                fieldWithPath("result.ventureTeams[].regionDetail")
                                                        .type(JsonFieldType.OBJECT)
                                                        .description("지역 상세 정보"),
                                                fieldWithPath(
                                                                "result.ventureTeams[].regionDetail.cityName")
                                                        .type(JsonFieldType.STRING)
                                                        .description("지역 시/도 이름"),
                                                fieldWithPath(
                                                                "result.ventureTeams[].regionDetail.divisionName")
                                                        .type(JsonFieldType.STRING)
                                                        .description("지역 시/군/구 이름"),

                                                // ✅ 중단: 지원사업을 준비 중인 팀이에요 4팀
                                                fieldWithPath("result.supportProjectTeams")
                                                        .type(JsonFieldType.ARRAY)
                                                        .description("지원 사업을 준비 중인 팀 목록 (최대 4팀)"),
                                                fieldWithPath(
                                                                "result.supportProjectTeams[].teamCurrentStates")
                                                        .type(JsonFieldType.ARRAY)
                                                        .description("팀 현재 상태 목록"),
                                                fieldWithPath(
                                                                "result.supportProjectTeams[].teamCurrentStates[].teamStateName")
                                                        .type(JsonFieldType.STRING)
                                                        .description("팀 상태 이름"),
                                                fieldWithPath(
                                                                "result.supportProjectTeams[].isTeamScrap")
                                                        .type(JsonFieldType.BOOLEAN)
                                                        .description("팀 스크랩 여부"),
                                                fieldWithPath(
                                                                "result.supportProjectTeams[].teamScrapCount")
                                                        .type(JsonFieldType.NUMBER)
                                                        .description("팀 스크랩 수"),
                                                fieldWithPath(
                                                                "result.supportProjectTeams[].teamName")
                                                        .type(JsonFieldType.STRING)
                                                        .description("팀 이름"),
                                                fieldWithPath(
                                                                "result.supportProjectTeams[].teamCode")
                                                        .type(JsonFieldType.STRING)
                                                        .description("팀 아이디 (팀 코드)"),
                                                fieldWithPath(
                                                                "result.supportProjectTeams[].teamShortDescription")
                                                        .type(JsonFieldType.STRING)
                                                        .description("팀 한 줄 소개"),
                                                fieldWithPath(
                                                                "result.supportProjectTeams[].teamLogoImagePath")
                                                        .type(JsonFieldType.STRING)
                                                        .description("팀 로고 이미지 경로"),
                                                fieldWithPath(
                                                                "result.supportProjectTeams[].teamScaleItem")
                                                        .type(JsonFieldType.OBJECT)
                                                        .description("팀 규모 정보"),
                                                fieldWithPath(
                                                                "result.supportProjectTeams[].teamScaleItem.teamScaleName")
                                                        .type(JsonFieldType.STRING)
                                                        .description("팀 규모 이름"),
                                                fieldWithPath(
                                                                "result.supportProjectTeams[].regionDetail")
                                                        .type(JsonFieldType.OBJECT)
                                                        .description("지역 상세 정보"),
                                                fieldWithPath(
                                                                "result.supportProjectTeams[].regionDetail.cityName")
                                                        .type(JsonFieldType.STRING)
                                                        .description("지역 시/도 이름"),
                                                fieldWithPath(
                                                                "result.supportProjectTeams[].regionDetail.divisionName")
                                                        .type(JsonFieldType.STRING)
                                                        .description("지역 시/군/구 이름"))))
                        .andReturn();
    }
}
