package liaison.linkit.team.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import groovy.util.logging.Slf4j;
import jakarta.servlet.http.Cookie;
import liaison.linkit.global.ControllerTest;
import liaison.linkit.login.domain.MemberTokens;
import liaison.linkit.team.dto.request.onBoarding.OnBoardingFieldTeamInformRequest;
import liaison.linkit.team.dto.response.TeamProfileOnBoardingIsValueResponse;
import liaison.linkit.team.dto.response.TeamProfileTeamBuildingFieldResponse;
import liaison.linkit.team.dto.response.activity.ActivityMethodResponse;
import liaison.linkit.team.dto.response.activity.ActivityRegionResponse;
import liaison.linkit.team.dto.response.activity.ActivityResponse;
import liaison.linkit.team.dto.response.miniProfile.TeamMiniProfileEarlyOnBoardingResponse;
import liaison.linkit.team.dto.response.miniProfile.TeamMiniProfileResponse;
import liaison.linkit.team.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static liaison.linkit.global.restdocs.RestDocsConfiguration.field;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TeamOnBoardingController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
@Slf4j
class TeamOnBoardingControllerTest extends ControllerTest {

    private static final MemberTokens MEMBER_TOKENS = new MemberTokens("refreshToken", "accessToken");
    private static final Cookie COOKIE = new Cookie("refresh-token", MEMBER_TOKENS.getRefreshToken());

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TeamOnBoardingService teamOnBoardingService;

    @MockBean
    private TeamMiniProfileService teamMiniProfileService;
    @MockBean
    private TeamProfileTeamBuildingFieldService teamProfileTeamBuildingFieldService;
    @MockBean
    private ActivityService activityService;

    private ResultActions performGetOnBoardingTeamProfileRequest() throws Exception {
        return mockMvc.perform(
                get("/onBoarding/team")
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
                        .contentType(APPLICATION_JSON)
        );
    }

    private ResultActions performPostRequest(final OnBoardingFieldTeamInformRequest onBoardingFieldTeamInformRequest) throws Exception {
        return mockMvc.perform(
                post("/team/teamBuildingField/basicInform")
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(onBoardingFieldTeamInformRequest))
        );
    }

    @BeforeEach
    void setUp() {
        given(refreshTokenRepository.existsById(any())).willReturn(true);
        doNothing().when(jwtProvider).validateTokens(any());
        given(jwtProvider.getSubject(any())).willReturn("1");
        doNothing().when(teamOnBoardingService).validateTeamProfileByMember(1L);
    }

    @DisplayName("팀 소개서 온보딩 과정의 모든 정보를 조회할 수 있다.")
    @Test
    void getOnBoardingTeamProfile() throws Exception {
        // given
        final TeamProfileOnBoardingIsValueResponse teamProfileOnBoardingIsValueResponse = new TeamProfileOnBoardingIsValueResponse(
                true,
                true,
                true
        );

        System.out.println("teamProfileOnBoardingIsValueResponse = " + teamProfileOnBoardingIsValueResponse);

        given(teamOnBoardingService.getTeamProfileOnBoardingIsValue(1L))
                .willReturn(teamProfileOnBoardingIsValueResponse);

        List<String> teamProfileTeamBuildingFieldNames = Arrays.asList("공모전", "대회", "창업");
        final TeamProfileTeamBuildingFieldResponse teamProfileTeamBuildingFieldResponse = new TeamProfileTeamBuildingFieldResponse(
                teamProfileTeamBuildingFieldNames
        );

        final TeamMiniProfileEarlyOnBoardingResponse teamMiniProfileEarlyOnBoardingResponse = new TeamMiniProfileEarlyOnBoardingResponse(
                "리에종",
                "플랫폼",
                "1-5인"
        );

        given(teamProfileTeamBuildingFieldService.getAllTeamProfileTeamBuildingFields(1L))
                .willReturn(teamProfileTeamBuildingFieldResponse);
        given(teamMiniProfileService.getTeamMiniProfileEarlyOnBoarding(1L))
                .willReturn(teamMiniProfileEarlyOnBoardingResponse);

        System.out.println("teamMiniProfileEarlyOnBoardingResponse = " + teamMiniProfileEarlyOnBoardingResponse);

        List<String> activityTagNames = Arrays.asList("사무실 있음", "대면 활동 선호");
        final ActivityMethodResponse activityMethodResponse = new ActivityMethodResponse(
                activityTagNames
        );

        final ActivityRegionResponse activityRegionResponse = new ActivityRegionResponse(
                "서울특별시",
                "강남구"
        );

        final ActivityResponse activityResponse = new ActivityResponse(
                activityMethodResponse,
                activityRegionResponse
        );

        given(activityService.getAllActivityMethods(1L)).willReturn(activityMethodResponse);
        given(activityService.getActivityRegion(1L)).willReturn(activityRegionResponse);
        given(activityService.getActivity(1L)).willReturn(activityResponse);

        final TeamMiniProfileResponse teamMiniProfileResponse = new TeamMiniProfileResponse(
                "플랫폼",
                "1-5인",
                "리에종",
                "사이드 프로젝트 함께 할 개발자를 찾고 있어요",
                LocalDate.of(2024, 10, 20),
                true,
                "https://image.linkit.im/images/linkit_logo.png",
                "빠르게 성장하는 팀, 최단기간 튜자 유치",
                "#해커톤 #사무실 있음 #서울시"
        );

        given(teamMiniProfileService.getPersonalTeamMiniProfile(1L)).willReturn(teamMiniProfileResponse);
        System.out.println("teamMiniProfileResponse = " + teamMiniProfileResponse);
        // when
        final ResultActions resultActions = performGetOnBoardingTeamProfileRequest();

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
                                responseFields(
                                        subsectionWithPath("onBoardingFieldTeamInformResponse").description("희망 팀빌딩 분야 항목과 미니 프로필 일부 정보").attributes(field("constraint", "객체")),
                                        fieldWithPath("onBoardingFieldTeamInformResponse.teamBuildingFieldNames").description("희망 팀빌딩 분야 이름").attributes(field("constraint", "문자열(배열)")),
                                        fieldWithPath("onBoardingFieldTeamInformResponse.teamName").description("팀명").attributes(field("constraint", "문자열")),
                                        fieldWithPath("onBoardingFieldTeamInformResponse.sectorName").description("팀 분야").attributes(field("constraint", "문자열")),
                                        fieldWithPath("onBoardingFieldTeamInformResponse.sizeType").description("팀 규모").attributes(field("constraint", "문자열")),

                                        subsectionWithPath("activityResponse").description("활동 방식 및 지역/위치 정보").attributes(field("constraint", "객체")),
                                        fieldWithPath("activityResponse.activityTagName").description("활동 방식").attributes(field("constraint", "문자열")),
                                        fieldWithPath("activityResponse.cityName").description("시/도").attributes(field("constraint", "문자열")),
                                        fieldWithPath("activityResponse.divisionName").description("시/군/구").attributes(field("constraint", "문자열")),

                                        subsectionWithPath("teamMiniProfileResponse").description("팀 소개서 미니 프로필").attributes(field("constraint", "객체")),
                                        fieldWithPath("teamMiniProfileResponse.sectorName").description("팀 분야").attributes(field("constraint", "문자열")),
                                        fieldWithPath("teamMiniProfileResponse.sizeType").description("팀 규모").attributes(field("constraint", "문자열")),
                                        fieldWithPath("teamMiniProfileResponse.teamName").description("팀명").attributes(field("constraint", "문자열")),
                                        fieldWithPath("teamMiniProfileResponse.miniProfileTitle").description("미니 프로필 제목").attributes(field("constraint", "문자열")),
                                        fieldWithPath("teamMiniProfileResponse.teamUploadPeriod").description("팀 미니 프로필 공고 업로드 기간").attributes(field("constraint", "LocalDate")),
                                        fieldWithPath("teamMiniProfileResponse.teamUploadDeadline").description("공고 마감 선택 여부").attributes(field("constraint", "boolean")),
                                        fieldWithPath("teamMiniProfileResponse.teamLogoImageUrl").description("팀 미니 프로필 이미지 경로").attributes(field("constraint", "문자열")),
                                        fieldWithPath("teamMiniProfileResponse.teamValue").description("팀 가치").attributes(field("constraint", "문자열")),
                                        fieldWithPath("teamMiniProfileResponse.teamDetailInform").description("팀 세부 정보").attributes(field("constraint", "문자열"))
                                )
                        )
                );
    }

    @DisplayName("희망 팀빌딩 분야 및 미니프로필 일부 정보 생성")
    @Test
    void postOnBoardingFieldTeamInform() throws Exception {
        // given
        List<String> teamBuildingFieldNames = Arrays.asList("공모전", "대회");

        final OnBoardingFieldTeamInformRequest onBoardingFieldTeamInformRequest = new OnBoardingFieldTeamInformRequest(
                teamBuildingFieldNames,
                "리에종",
                "1-5인",
                "플랫폼"
        );

        // when
        final ResultActions resultActions = performPostRequest(onBoardingFieldTeamInformRequest);

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
                                        fieldWithPath("teamBuildingFieldNames")
                                                .type(JsonFieldType.ARRAY)
                                                .description("희망 팀빌딩 분야(7가지 항목)")
                                                .attributes(field("constraint", "문자열의 배열")),
                                        fieldWithPath("teamName")
                                                .type(JsonFieldType.STRING)
                                                .description("팀이름")
                                                .attributes(field("constraint", "문자열")),
                                        fieldWithPath("sizeType")
                                                .type(JsonFieldType.STRING)
                                                .description("팀 규모")
                                                .attributes(field("constraint", "문자열")),
                                        fieldWithPath("sectorName")
                                                .type(JsonFieldType.STRING)
                                                .description("분야 이름")
                                                .attributes(field("constraint", "문자열"))
                                )
                        )
                );

    }
}
