package liaison.linkit.search.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import groovy.util.logging.Slf4j;
import jakarta.servlet.http.Cookie;
import liaison.linkit.global.ControllerTest;
import liaison.linkit.login.domain.MemberTokens;
import liaison.linkit.profile.dto.response.miniProfile.MiniProfileResponse;
import liaison.linkit.search.service.SearchService;
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
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;

import static liaison.linkit.global.restdocs.RestDocsConfiguration.field;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
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

    private ResultActions performGetTeamMiniProfileRequest() throws Exception {
        return mockMvc.perform(get("/search/team/profile")
                .queryParam("teamBuildingFieldName", (String) null)
                .queryParam("jobRoleName", (String) null)
                .queryParam("skillName", (String) null)
                .queryParam("cityName", (String) null)
                .queryParam("divisionName", (String) null)
                .queryParam("activityTagName", (String) null)
                .contentType(APPLICATION_JSON));
    }

    @Test
    @DisplayName("팀 찾기를 진행할 수 있다.")
    void getTeamMiniProfile() throws Exception {
        // given
        final TeamMiniProfileResponse teamMiniProfileResponse = new TeamMiniProfileResponse(
                1L,
                "SaaS",
                "1-5인",
                "리에종",
                "팀 소개서 제목입니다.",
                LocalDate.of(2024, 10, 9),
                false,
                "https://image.linkit.im/images/linkit_logo.png",
                Arrays.asList("재택 가능", "Pre-A", "사수 있음", "스톡 제공")
        );

        Page<TeamMiniProfileResponse> page = new PageImpl<>(Collections.singletonList(teamMiniProfileResponse));
        when(searchService.findTeamMiniProfile(
                any(),
                eq(null),
                eq(null),
                eq(null),
                eq(null),
                eq(null),
                eq(null)
        )).thenReturn(page);

        // when
        final ResultActions resultActions = performGetTeamMiniProfileRequest();

        // then
        resultActions.andExpect(status().isOk())
                .andDo(
                        restDocs.document(
                                queryParameters(
                                        parameterWithName("teamBuildingFieldName").description("희망 팀빌딩 분야 필터"),
                                        parameterWithName("jobRoleName").description("희망 직무/역할 필터"),
                                        parameterWithName("skillName").description("보유 역량 필터"),
                                        parameterWithName("cityName").description("지역 (시/도) 필터"),
                                        parameterWithName("divisionName").description("지역 (시/군/구) 필터").attributes(field("constraint", "전체인 경우 시/도 필터로 적용")),
                                        parameterWithName("activityTagName").description("활동 방식 필터")
                                ),
                                responseFields(
                                        fieldWithPath("content[].id").description("팀 미니 프로필 ID"),
                                        fieldWithPath("content[].sectorName").description("부문 이름"),
                                        fieldWithPath("content[].sizeType").description("팀 크기 유형"),
                                        fieldWithPath("content[].teamName").description("팀 이름"),
                                        fieldWithPath("content[].miniProfileTitle").description("팀 소개서 제목"),
                                        fieldWithPath("content[].teamUploadPeriod").description("업로드 기간"),
                                        fieldWithPath("content[].teamUploadDeadline").description("업로드 마감일 여부"),
                                        fieldWithPath("content[].teamLogoImageUrl").description("팀 로고 이미지 URL"),
                                        fieldWithPath("content[].teamKeywordNames").description("팀 키워드"),
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

    // ----------------------------------- 팀원 찾기 테스트 코드 - 팀 찾기 테스트 구분 라인 ----------------------------------------

    private ResultActions performGetPrivateMiniProfileRequest() throws Exception {
        return mockMvc.perform(get("/search/private/profile")
                .queryParam("teamBuildingFieldName", (String) null)
                .queryParam("jobRoleName", (String) null)
                .queryParam("skillName",(String) null)
                .queryParam("cityName", (String) null)
                .queryParam("divisionName", (String) null)
                .contentType(APPLICATION_JSON));
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
                "권동민"
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
                                        parameterWithName("teamBuildingFieldName").description("희망 팀빌딩 분야 필터"),
                                        parameterWithName("jobRoleName").description("희망 직무/역할 필터"),
                                        parameterWithName("skillName").description("보유 역량 필터"),
                                        parameterWithName("cityName").description("지역 (시/도) 필터"),
                                        parameterWithName("divisionName").description("지역 (시/군/구) 필터").attributes(field("constraint", "전체인 경우 시/도 필터로 적용"))
                                ),
                                responseFields(
                                        fieldWithPath("content[].id").description("개인 미니 프로필 ID"),
                                        fieldWithPath("content[].profileTitle").description("프로필 제목"),
                                        fieldWithPath("content[].miniProfileImg").description("프로필 이미지 URL"),
                                        fieldWithPath("content[].isActivate").description("프로필 활성화 여부"),
                                        fieldWithPath("content[].myKeywordNames").description("키워드 목록"),
                                        fieldWithPath("content[].memberName").description("회원 이름"),
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
