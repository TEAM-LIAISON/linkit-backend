package liaison.linkit.search.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import groovy.util.logging.Slf4j;
import jakarta.servlet.http.Cookie;
import liaison.linkit.global.ControllerTest;
import liaison.linkit.login.domain.MemberTokens;
import liaison.linkit.profile.dto.response.miniProfile.MiniProfileResponse;
import liaison.linkit.search.dto.response.SearchTeamProfileResponse;
import liaison.linkit.search.service.SearchService;
import liaison.linkit.team.dto.response.announcement.TeamMemberAnnouncementResponse;
import liaison.linkit.team.dto.response.miniProfile.TeamMiniProfileResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SearchController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
@Slf4j
public class SearchControllerTest extends ControllerTest {

    private static final MemberTokens MEMBER_TOKENS = new MemberTokens("refreshToken", "accessToken");
    private static final Cookie COOKIE = new Cookie("refresh-token", MEMBER_TOKENS.getRefreshToken());

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SearchService searchService;

    @BeforeEach
    void setUp() {
        given(refreshTokenRepository.existsById(any())).willReturn(true);
        doNothing().when(jwtProvider).validateTokens(any());
        given(jwtProvider.getSubject(any())).willReturn("1");
    }

    private ResultActions performGetTeamAnnouncementAndTeamMiniProfile() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = get("/search/team/profile")
                .contentType(APPLICATION_JSON);

        // 쿼리 파라미터를 조건적으로 추가합니다.
        // 각 파라미터 값이 null인 경우 해당 파라미터를 추가하지 않습니다.
//        requestBuilder.queryParam("page", "0"); // 페이지네이션 설정 예시
//        requestBuilder.queryParam("size", "10"); // 페이지네이션 설정 예시

        // 필터링 파라미터가 null이 아니면 해당 파라미터를 추가합니다.
        // 다음과 같은 방식으로 모든 필터링 파라미터를 처리합니다.
        List<String> teamBuildingFieldNames = null; // 예를 들어, null로 설정
        if (teamBuildingFieldNames != null) {
            teamBuildingFieldNames.forEach(name -> requestBuilder.queryParam("teamBuildingFieldName", name));
        }

        String jobRoleName = null; // null로 예시 설정
        if (jobRoleName != null) {
            requestBuilder.queryParam("jobRoleName", jobRoleName);
        }

        String skillName = null; // null로 예시 설정
        if (skillName != null) {
            requestBuilder.queryParam("skillName", skillName);
        }

        String cityName = null; // null로 예시 설정
        if (cityName != null) {
            requestBuilder.queryParam("cityName", cityName);
        }

        String divisionName = null; // null로 예시 설정
        if (divisionName != null) {
            requestBuilder.queryParam("divisionName", divisionName);
        }

        List<String> activityTagNames = null; // null로 예시 설정
        if (activityTagNames != null) {
            activityTagNames.forEach(tag -> requestBuilder.queryParam("activityTagName", tag));
        }

        return mockMvc.perform(requestBuilder);
    }


    @Test
    @DisplayName("팀 찾기를 진행할 수 있다.")
    void getTeamAnnouncementAndTeamMiniProfile() throws Exception {
        // given
        final TeamMiniProfileResponse teamMiniProfileResponse = new TeamMiniProfileResponse(
                1L,
                "SaaS",
                "1-5인",
                "리에종",
                "팀 소개서 제목입니다.",
                true,
                "https://image.linkit.im/images/linkit_logo.png",
                Arrays.asList("재택 가능", "Pre-A", "사수 있음", "스톡 제공")
        );

        final TeamMemberAnnouncementResponse teamMemberAnnouncementResponse = new TeamMemberAnnouncementResponse(
                1L,
                "리에종",
                Arrays.asList("개발·데이터", "디자인"),
                "주요 업무입니다.",
                Arrays.asList("서버 개발", "DevOps", "게임 디자인"),
                "지원 절차입니다."
        );

        final SearchTeamProfileResponse searchTeamProfileResponse = new SearchTeamProfileResponse(
                teamMiniProfileResponse,
                teamMemberAnnouncementResponse
        );

        Page<SearchTeamProfileResponse> page = new PageImpl<>(Collections.singletonList(searchTeamProfileResponse));
        when(searchService.findTeamMemberAnnouncementsWithTeamMiniProfile(
                any(),
                eq(null),
                eq(null),
                eq(null),
                eq(null),
                eq(null),
                eq(null)
        )).thenReturn(page);

        // when
        final ResultActions resultActions = performGetTeamAnnouncementAndTeamMiniProfile();

        // then
        resultActions.andExpect(status().isOk())
                .andDo(
                        restDocs.document(
                                queryParameters(
//                                        parameterWithName("page").description("페이지 번호"),
//                                        parameterWithName("size").description("페이지당 항목 수"),
                                        parameterWithName("teamBuildingFieldName").description("희망 팀빌딩 분야 필터").optional(),
                                        parameterWithName("jobRoleName").description("직무/역할 필터").optional(),
                                        parameterWithName("skillName").description("보유 역량 필터").optional(),
                                        parameterWithName("cityName").description("지역 (시/도) 필터").optional(),
                                        parameterWithName("divisionName").description("지역 (시/군/구) 필터").optional(),
                                        parameterWithName("activityTagName").description("활동 방식 필터").optional()
                                ),

                                responseFields(
                                        fieldWithPath("content[].teamMiniProfileResponse.id").description("팀 미니 프로필 ID"),
                                        fieldWithPath("content[].teamMiniProfileResponse.sectorName").description("부문 이름"),
                                        fieldWithPath("content[].teamMiniProfileResponse.sizeType").description("팀 크기 유형"),
                                        fieldWithPath("content[].teamMiniProfileResponse.teamName").description("팀 이름"),
                                        fieldWithPath("content[].teamMiniProfileResponse.teamProfileTitle").description("팀 소개서 제목"),
                                        fieldWithPath("content[].teamMiniProfileResponse.isTeamActivate").description("팀 소개서 활성화 여부"),
                                        fieldWithPath("content[].teamMiniProfileResponse.teamLogoImageUrl").description("팀 로고 이미지 URL"),
                                        fieldWithPath("content[].teamMiniProfileResponse.teamKeywordNames").description("팀 키워드").optional(),
                                        fieldWithPath("content[].teamMemberAnnouncementResponse.id").description("팀원 공고 ID"),
                                        fieldWithPath("content[].teamMemberAnnouncementResponse.teamName").description("팀 이름"),
                                        fieldWithPath("content[].teamMemberAnnouncementResponse.jobRoleNames").description("팀원 공고 직무 이름 목록"),
                                        fieldWithPath("content[].teamMemberAnnouncementResponse.mainBusiness").description("주요 업무"),
                                        fieldWithPath("content[].teamMemberAnnouncementResponse.skillNames").description("요구되는 기술 목록"),
                                        fieldWithPath("content[].teamMemberAnnouncementResponse.applicationProcess").description("지원 절차"),
                                        // 페이지와 관련된 필드 추가
                                        subsectionWithPath("pageable").ignored(),
                                        subsectionWithPath("sort").ignored(),
                                        fieldWithPath("last").description("마지막 페이지 여부"),
                                        fieldWithPath("totalPages").description("전체 페이지 수"),
                                        fieldWithPath("totalElements").description("전체 요소 수"),
                                        fieldWithPath("first").description("첫 페이지 여부"),
                                        fieldWithPath("size").description("페이지당 요소 수"),
                                        fieldWithPath("number").description("페이지 번호"),
                                        fieldWithPath("numberOfElements").description("현재 페이지의 요소 수"),
                                        fieldWithPath("empty").description("페이지가 비어 있는지 여부")
                                )
                        )
                );
    }


    // ----------------------------------- 팀원 찾기 테스트 코드 - 팀 찾기 테스트 구분 라인 ----------------------------------------

    private ResultActions performGetPrivateMiniProfileRequest() throws Exception {
         MockHttpServletRequestBuilder requestBuilder = get("/search/private/profile")
                        .contentType(APPLICATION_JSON);

        List<String> teamBuildingFieldNames = null; // 예를 들어, null로 설정
        if (teamBuildingFieldNames != null) {
            teamBuildingFieldNames.forEach(name -> requestBuilder.queryParam("teamBuildingFieldName", name));
        }

        String jobRoleName = null; // null로 예시 설정
        if (jobRoleName != null) {
            requestBuilder.queryParam("jobRoleName", jobRoleName);
        }

        String skillName = null; // null로 예시 설정
        if (skillName != null) {
            requestBuilder.queryParam("skillName", skillName);
        }

        String cityName = null; // null로 예시 설정
        if (cityName != null) {
            requestBuilder.queryParam("cityName", cityName);
        }

        String divisionName = null; // null로 예시 설정
        if (divisionName != null) {
            requestBuilder.queryParam("divisionName", divisionName);
        }

        return mockMvc.perform(requestBuilder);
    }

    @Test
    @DisplayName("팀원 찾기를 진행할 수 있다.")
    void getPrivateMiniProfile() throws Exception {
        // given
        // 미니 프로필 응답 객체 생성
        final MiniProfileResponse miniProfileResponse = new MiniProfileResponse(
                1L,
                "시니어 소프트웨어 개발자",
                "https://image.linkit.im/images/linkit_logo.png",
                true,
                Arrays.asList("2024 레드닷 수상", "스타트업 경력", "서울대 디자인", "대기업 경력 3년"),
                "권동민",
                Arrays.asList("개발·데이터")
        );

        Page<MiniProfileResponse> page = new PageImpl<>(Collections.singletonList(miniProfileResponse));
        when(searchService.findPrivateMiniProfile(
                any(),
                eq(null),
                eq(null),
                eq(null),
                eq(null),
                eq(null)
        )).thenReturn(page);

        // when
        final ResultActions resultActions = performGetPrivateMiniProfileRequest();

        // then
        resultActions.andExpect(status().isOk())
                .andDo(
                        restDocs.document(
                                queryParameters(
//                                        parameterWithName("page").description("페이지 번호"),
//                                        parameterWithName("size").description("페이지당 항목 수"),
                                        parameterWithName("teamBuildingFieldName").description("희망 팀빌딩 분야 필터").optional(),
                                        parameterWithName("jobRoleName").description("직무/역할 필터").optional(),
                                        parameterWithName("skillName").description("보유 역량 필터").optional(),
                                        parameterWithName("cityName").description("지역 (시/도) 필터").optional(),
                                        parameterWithName("divisionName").description("지역 (시/군/구) 필터").optional()
                                ),
                                responseFields(
                                        fieldWithPath("content[].id").description("개인 미니 프로필 ID"),
                                        fieldWithPath("content[].profileTitle").description("프로필 제목"),
                                        fieldWithPath("content[].miniProfileImg").description("프로필 이미지 URL"),
                                        fieldWithPath("content[].isActivate").description("프로필 활성화 여부"),
                                        fieldWithPath("content[].myKeywordNames").description("키워드 목록"),
                                        fieldWithPath("content[].memberName").description("회원 이름"),
                                        fieldWithPath("content[].jobRoleNames").type(JsonFieldType.ARRAY).description("직무 및 역할 이름 배열"),

                                        fieldWithPath("pageable").description("페이징 처리 객체"),
                                        fieldWithPath("sort.empty").description("정렬 규칙이 비어 있는지 여부"),
                                        fieldWithPath("sort.unsorted").description("정렬이 적용되지 않았는지 여부"),
                                        fieldWithPath("sort.sorted").description("정렬이 적용되었는지 여부"),
                                        fieldWithPath("last").description("마지막 페이지 여부"),
                                        fieldWithPath("totalPages").description("전체 페이지 수"),
                                        fieldWithPath("totalElements").description("전체 요소 수"),
                                        fieldWithPath("first").description("첫 페이지 여부"),
                                        fieldWithPath("size").description("페이지당 요소 수"),
                                        fieldWithPath("number").description("페이지 번호"),
                                        fieldWithPath("numberOfElements").description("현재 페이지의 요소 수"),
                                        fieldWithPath("empty").description("페이지가 비어 있는지 여부")
                                )
                        )
                );

    }

}
