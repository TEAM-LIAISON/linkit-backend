package liaison.linkit.search.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import java.util.Arrays;
import java.util.List;
import liaison.linkit.common.presentation.RegionResponseDTO.RegionDetail;
import liaison.linkit.global.ControllerTest;
import liaison.linkit.login.domain.MemberTokens;
import liaison.linkit.search.business.service.TeamSearchService;
import liaison.linkit.team.presentation.team.dto.TeamResponseDTO.TeamCurrentStateItem;
import liaison.linkit.team.presentation.team.dto.TeamResponseDTO.TeamInformMenu;
import liaison.linkit.team.presentation.team.dto.TeamResponseDTO.TeamScaleItem;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(TeamSearchController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
public class TeamSearchControllerTest extends ControllerTest {
    private static final MemberTokens MEMBER_TOKENS = new MemberTokens("refreshToken", "accessToken");
    private static final Cookie COOKIE = new Cookie("refreshToken", MEMBER_TOKENS.getRefreshToken());

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TeamSearchService teamSearchService;

    private ResultActions performSearchTeams(
            List<String> scaleName,
            Boolean isAnnouncement,
            List<String> cityName,
            List<String> teamStateName,
            int page,
            int size
    ) throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders.get("/api/v1/team/search")
                        .param("scaleName", scaleName.toArray(new String[0]))
                        .param("isAnnouncement", isAnnouncement != null ? isAnnouncement.toString() : "")
                        .param("cityName", cityName.toArray(new String[0]))
                        .param("teamStateName", teamStateName.toArray(new String[0]))
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
        );
    }

    // ...

    @DisplayName("회원/비회원이 팀을 검색할 수 있다. (필터링 가능)")
    @Test
    void searchTeams() throws Exception {
        // given
        TeamInformMenu teamInformMenu1 = TeamInformMenu.builder()
                .teamCurrentStates(Arrays.asList(
                        TeamCurrentStateItem.builder().teamStateName("대회 준비 중").build(),
                        TeamCurrentStateItem.builder().teamStateName("투자 유치 중").build()
                ))
                .isTeamScrap(true)
                .teamScrapCount(100)
                .teamName("팀 이름 1")
                .teamCode("팀 아이디 (팀 코드)")
                .teamShortDescription("팀 한 줄 소개 1")
                .teamLogoImagePath("팀 로고 이미지 경로 1")
                .teamScaleItem(TeamScaleItem.builder().teamScaleName("1인").build())
                .regionDetail(RegionDetail.builder()
                        .cityName("활동지역 시/도")
                        .divisionName("활동지역 시/군/구")
                        .build())
                .build();

        TeamInformMenu teamInformMenu2 = TeamInformMenu.builder()
                .teamCurrentStates(Arrays.asList(
                        TeamCurrentStateItem.builder().teamStateName("대회 준비 중").build(),
                        TeamCurrentStateItem.builder().teamStateName("투자 유치 중").build()
                ))
                .isTeamScrap(false)
                .teamScrapCount(200)
                .teamName("팀 이름 2")
                .teamCode("팀 아이디 2(팀 코드)")
                .teamShortDescription("팀 한 줄 소개 2")
                .teamLogoImagePath("팀 로고 이미지 경로 2")
                .teamScaleItem(TeamScaleItem.builder().teamScaleName("2~5인").build())
                .regionDetail(RegionDetail.builder()
                        .cityName("활동지역 시/도")
                        .divisionName("활동지역 시/군/구")
                        .build())
                .build();

        List<TeamInformMenu> teams = Arrays.asList(teamInformMenu1, teamInformMenu2);
        Page<TeamInformMenu> teamPage = new PageImpl<>(teams, PageRequest.of(0, 20), teams.size());

        when(teamSearchService.searchTeamsInLogoutState(any(), any(), any(), any(), any(Pageable.class))).thenReturn(teamPage);

        // when
        final ResultActions resultActions = performSearchTeams(
                Arrays.asList("1인", "2~5인"),
                true,
                Arrays.asList("서울특별시", "부산광역시"),
                Arrays.asList("팀원 찾는 중", "투자 유치 중"),
                0,
                20
        );

        // then
        final MvcResult mvcResult = resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.code").value("1000"))
                .andExpect(jsonPath("$.message").value("요청에 성공하였습니다."))
                .andDo(
                        restDocs.document(
                                queryParameters(
                                        parameterWithName("scaleName").optional().description("팀 규모 (선택적)"),
                                        parameterWithName("isAnnouncement").optional().description("공고 존재 여부 (true/false)"),
                                        parameterWithName("cityName").optional().description("시/도 이름 (선택적)"),
                                        parameterWithName("teamStateName").optional().description("팀 상태 이름 (선택적)"),
                                        parameterWithName("page").optional().description("페이지 번호 (기본값: 0)"),
                                        parameterWithName("size").optional().description("페이지 크기 (기본값: 20)")
                                ),
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
                                        fieldWithPath("result.content")
                                                .type(JsonFieldType.ARRAY)
                                                .description("팀 정보 목록"),
                                        fieldWithPath("result.content[].teamCurrentStates")
                                                .type(JsonFieldType.ARRAY)
                                                .description("팀 현재 상태 목록"),
                                        fieldWithPath("result.content[].teamCurrentStates[].teamStateName")
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
                                        fieldWithPath("result.content[].teamShortDescription")
                                                .type(JsonFieldType.STRING)
                                                .description("팀 한 줄 소개"),
                                        fieldWithPath("result.content[].teamLogoImagePath")
                                                .type(JsonFieldType.STRING)
                                                .description("팀 로고 이미지 경로"),
                                        fieldWithPath("result.content[].teamScaleItem")
                                                .type(JsonFieldType.OBJECT)
                                                .description("팀 규모 정보"),
                                        fieldWithPath("result.content[].teamScaleItem.teamScaleName")
                                                .type(JsonFieldType.STRING)
                                                .description("팀 규모 이름"),
                                        fieldWithPath("result.content[].regionDetail")
                                                .type(JsonFieldType.OBJECT)
                                                .description("지역 상세 정보"),
                                        fieldWithPath("result.content[].regionDetail.cityName")
                                                .type(JsonFieldType.STRING)
                                                .description("지역 시/도 이름"),
                                        fieldWithPath("result.content[].regionDetail.divisionName")
                                                .type(JsonFieldType.STRING)
                                                .description("지역 시/군/구 이름"),
                                        fieldWithPath("result.pageable")
                                                .type(JsonFieldType.OBJECT)
                                                .description("페이징 정보"),
                                        fieldWithPath("result.pageable.paged")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("페이징 적용 여부"),
                                        fieldWithPath("result.pageable.unpaged")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("페이징 미적용 여부"),
                                        fieldWithPath("result.pageable.sort")
                                                .type(JsonFieldType.OBJECT)
                                                .description("정렬 정보"),
                                        fieldWithPath("result.pageable.sort.sorted")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("정렬 여부"),
                                        fieldWithPath("result.pageable.sort.unsorted")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("비정렬 여부"),
                                        fieldWithPath("result.pageable.sort.empty")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("정렬 정보 없음 여부"),
                                        fieldWithPath("result.pageable.pageNumber")
                                                .type(JsonFieldType.NUMBER)
                                                .description("현재 페이지 번호"),
                                        fieldWithPath("result.pageable.pageSize")
                                                .type(JsonFieldType.NUMBER)
                                                .description("페이지 크기"),
                                        fieldWithPath("result.pageable.offset")
                                                .type(JsonFieldType.NUMBER)
                                                .description("데이터 오프셋"),
                                        fieldWithPath("result.totalPages")
                                                .type(JsonFieldType.NUMBER)
                                                .description("총 페이지 수"),
                                        fieldWithPath("result.totalElements")
                                                .type(JsonFieldType.NUMBER)
                                                .description("총 요소 수"),
                                        fieldWithPath("result.last")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("마지막 페이지 여부"),
                                        fieldWithPath("result.size")
                                                .type(JsonFieldType.NUMBER)
                                                .description("페이지 크기"),
                                        fieldWithPath("result.number")
                                                .type(JsonFieldType.NUMBER)
                                                .description("현재 페이지 번호"),
                                        fieldWithPath("result.sort")
                                                .type(JsonFieldType.OBJECT)
                                                .description("정렬 정보"),
                                        fieldWithPath("result.sort.sorted")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("정렬 여부"),
                                        fieldWithPath("result.sort.unsorted")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("비정렬 여부"),
                                        fieldWithPath("result.sort.empty")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("정렬 정보 없음 여부"),
                                        fieldWithPath("result.first")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("첫 페이지 여부"),
                                        fieldWithPath("result.numberOfElements")
                                                .type(JsonFieldType.NUMBER)
                                                .description("현재 페이지의 요소 수"),
                                        fieldWithPath("result.empty")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("요소 존재 여부")
                                )
                        )
                ).andReturn();

    }

}
