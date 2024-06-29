//package liaison.linkit.profile.presentation.onBoarding;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import groovy.util.logging.Slf4j;
//import jakarta.servlet.http.Cookie;
//import liaison.linkit.global.ControllerTest;
//import liaison.linkit.login.domain.MemberTokens;
//import liaison.linkit.member.service.MemberService;
//import liaison.linkit.profile.dto.response.MemberNameResponse;
//import liaison.linkit.profile.dto.response.OnBoardingProfileResponse;
//import liaison.linkit.profile.dto.response.antecedents.AntecedentsResponse;
//import liaison.linkit.profile.dto.response.education.EducationResponse;
//import liaison.linkit.profile.dto.response.isValue.ProfileOnBoardingIsValueResponse;
//import liaison.linkit.profile.dto.response.miniProfile.MiniProfileResponse;
//import liaison.linkit.profile.dto.response.skill.ProfileSkillResponse;
//import liaison.linkit.profile.dto.response.teamBuilding.ProfileTeamBuildingFieldResponse;
//import liaison.linkit.profile.service.*;
//import liaison.linkit.profile.service.onBoarding.OnBoardingService;
//import liaison.linkit.region.dto.response.ProfileRegionResponse;
//import liaison.linkit.region.service.ProfileRegionService;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
//import org.springframework.restdocs.payload.JsonFieldType;
//import org.springframework.test.web.servlet.ResultActions;
//
//import java.time.LocalDate;
//import java.util.Arrays;
//import java.util.List;
//
//import static liaison.linkit.global.restdocs.RestDocsConfiguration.field;
//import static org.mockito.BDDMockito.given;
//import static org.springframework.http.HttpHeaders.AUTHORIZATION;
//import static org.springframework.http.MediaType.APPLICATION_JSON;
//import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
//import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
//import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
//import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
//import static org.springframework.restdocs.payload.PayloadDocumentation.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//
//@WebMvcTest(OnBoardingController.class)
//@MockBean(JpaMetamodelMappingContext.class)
//@AutoConfigureRestDocs
//@Slf4j
//public class OnBoardingControllerTest extends ControllerTest {
//
//    private static final MemberTokens MEMBER_TOKENS = new MemberTokens("refreshToken", "accessToken");
//    private static final Cookie COOKIE = new Cookie("refresh-token", MEMBER_TOKENS.getRefreshToken());
//
//    @Autowired
//    private ObjectMapper objectMapper;
//    @MockBean
//    private OnBoardingService onBoardingService;
//    @MockBean
//    private ProfileService profileService;
//    @MockBean
//    public MiniProfileService miniProfileService;
//    @MockBean
//    public ProfileTeamBuildingFieldService profileTeamBuildingFieldService;
//    @MockBean
//    public ProfileRegionService profileRegionService;
//    @MockBean
//    public ProfileSkillService profileSkillService;
//    @MockBean
//    public EducationService educationService;
//    @MockBean
//    public AntecedentsService antecedentsService;
//    @MockBean
//    public MemberService memberService;
//
//    private ResultActions performGetOnBoardingProfileRequest() throws Exception {
//        return mockMvc.perform(
//                get("/onBoarding/private")
//                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
//                        .cookie(COOKIE)
//                        .contentType(APPLICATION_JSON)
//        );
//    }
//
//    @DisplayName("내 이력서 온보딩 과정의 모든 정보를 조회할 수 있다.")
//    @Test
//    void getOnBoardingProfile() throws Exception {
//        // given
//        final ProfileOnBoardingIsValueResponse profileOnBoardingIsValueResponse = new ProfileOnBoardingIsValueResponse(
//                true,
//                true,
//                true,
//                true,
//                true,
//                true
//        );
//
//        given(onBoardingService.getProfileOnBoardingIsValue(1L)).willReturn(profileOnBoardingIsValueResponse);
//        // 1. 희망 팀빌딩 분야
//        List<String> teamBuildingFieldNames = Arrays.asList("공모전", "대회", "창업");
//        final ProfileTeamBuildingFieldResponse profileTeamBuildingFieldResponse = new ProfileTeamBuildingFieldResponse(
//                teamBuildingFieldNames
//        );
//
//        given(profileTeamBuildingFieldService.getAllProfileTeamBuildings(1L))
//                .willReturn(profileTeamBuildingFieldResponse);
//
//        // 2. 역할 및 기술
//        List<String> skillNames = Arrays.asList("Java", "React");
//        final ProfileSkillResponse profileSkillResponse = ProfileSkillResponse.of(skillNames);
//
//        given(profileSkillService.getAllProfileSkills(1L)).willReturn(profileSkillResponse);
//
//
//        // 3. 지역 및 위치 정보
//        final ProfileRegionResponse profileRegionResponse = new ProfileRegionResponse(
//                "서울특별시",
//                "강남구"
//        );
//
//        given(profileRegionService.getPersonalProfileRegion(1L)).willReturn(profileRegionResponse);
//
//        // 3. 학교 정보
//        final EducationResponse educationResponse1 = new EducationResponse(
//                1L,
//                2022,
//                2024,
//                "홍익대학교",
//                "컴퓨터공학과",
//                "재학 중"
//        );
//
//        final EducationResponse educationResponse2 = new EducationResponse(
//                2L,
//                2021,
//                2025,
//                "홍익대학교",
//                "예술학과",
//                "졸업"
//        );
//
//        List<EducationResponse> educationResponses = Arrays.asList(educationResponse1, educationResponse2);
//
//        given(educationService.getAllEducations(1L))
//                .willReturn(educationResponses);
//
//        // 4. 이력 정보
//        final AntecedentsResponse antecedentsResponse1 = new AntecedentsResponse(
//                1L,
//                "linkit",
//                "프로젝트 매니저",
//                2023,
//                3,
//                2024,
//                10,
//                false,
//                "경력 설명입니다."
//        );
//
//        final AntecedentsResponse antecedentsResponse2 = new AntecedentsResponse(
//                2L,
//                "오더이즈",
//                "프로젝트 매니저",
//                2023,
//                3,
//                2023,
//                6,
//                false,
//                "경력 설명입니다."
//        );
//
//        List<AntecedentsResponse> antecedentsResponses = Arrays.asList(antecedentsResponse1, antecedentsResponse2);
//
//        given(antecedentsService.getAllAntecedents(1L)).willReturn(antecedentsResponses);
//
//        // 5. 미니 프로필 정보
//        final MiniProfileResponse miniProfileResponse = new MiniProfileResponse(
//                "시니어 소프트웨어 개발자",
//                LocalDate.of(2024, 10, 20),
//                true,
//                "https://image.linkit.im/images/linkit_logo.png",
//                "혁신, 팀워크, 의지",
//                "Java, Spring, AWS, Microservices, Docker"
//        );
//
//        given(miniProfileService.getPersonalMiniProfile(1L)).willReturn(miniProfileResponse);
//
//        final MemberNameResponse memberNameResponse = new MemberNameResponse(
//                "권동민"
//        );
//        given(memberService.getMemberName(1L)).willReturn(memberNameResponse);
//
//        // 온보딩 응답
//        final OnBoardingProfileResponse onBoardingProfileResponse = new OnBoardingProfileResponse(
//                profileTeamBuildingFieldResponse,
//                profileSkillResponse,
//                profileRegionResponse,
//                educationResponses,
//                antecedentsResponses,
//                miniProfileResponse,
//                memberNameResponse
//        );
//
//        given(onBoardingService.getOnBoardingProfile(
//                profileTeamBuildingFieldResponse,
//                profileSkillResponse,
//                profileRegionResponse,
//                educationResponses,
//                antecedentsResponses,
//                miniProfileResponse,
//                memberNameResponse))
//                .willReturn(onBoardingProfileResponse);
//
//        // when
//        final ResultActions resultActions = performGetOnBoardingProfileRequest();
//
//        // then
//        resultActions.andExpect(status().isOk())
//                .andDo(
//                        restDocs.document(
//                                requestCookies(
//                                        cookieWithName("refresh-token")
//                                                .description("갱신 토큰")
//                                ),
//                                requestHeaders(
//                                        headerWithName("Authorization")
//                                                .description("access token")
//                                                .attributes(field("constraint", "문자열(jwt)"))
//                                ),
//                                responseFields(
//                                        subsectionWithPath("profileTeamBuildingFieldResponse").description("희망 팀빌딩 분야 항목").attributes(field("constraint", "객체")),
//                                        fieldWithPath("profileTeamBuildingFieldResponse.teamBuildingFieldNames").description("희망 팀빌딩 분야 이름").attributes(field("constraint", "문자열(배열)")),
//
//                                        subsectionWithPath("profileSkillResponse").description("보유 기술 및 역할 목록").attributes(field("constraint", "객체")),
////                                        fieldWithPath("profileSkillResponse.roleFields[]").description("보유한 역할").attributes(field("constraint", "문자열(배열)")),
//                                        fieldWithPath("profileSkillResponse.skillNames[]").description("보유한 기술 이름").attributes(field("constraint", "문자열(배열)")),
//
//                                        subsectionWithPath("profileRegionResponse").description("지역 및 위치 항목").attributes(field("constraint", "객체")),
//                                        fieldWithPath("profileRegionResponse.cityName").description("시/구 이름").attributes(field("constraint", "문자열")),
//                                        fieldWithPath("profileRegionResponse.divisionName").description("시/군/구 이름").attributes(field("constraint", "문자열")),
//
//                                        subsectionWithPath("educationResponses").description("학력 항목").attributes(field("constraint", "객체 (배열)")),
//                                        fieldWithPath("educationResponses[].id").description("학력 ID").attributes(field("constraint", "양의 정수")),
//                                        fieldWithPath("educationResponses[].admissionYear").description("입학 연도").attributes(field("constraint", "양의 정수")),
//                                        fieldWithPath("educationResponses[].graduationYear").description("졸업 연도").attributes(field("constraint", "양의 정수")),
//                                        fieldWithPath("educationResponses[].universityName").description("학교 이름").attributes(field("constraint", "문자열")),
//                                        fieldWithPath("educationResponses[].majorName").description("전공 이름").attributes(field("constraint", "문자열")),
//                                        fieldWithPath("educationResponses[].degreeName").description("학위 이름").attributes(field("constraint", "문자열")),
//
//                                        subsectionWithPath("antecedentsResponses").description("이력 항목").attributes(field("constraint", "객체(배열)")),
//                                        fieldWithPath("antecedentsResponses[].projectName").description("회사 이름").attributes(field("constraint", "문자열")),
//                                        fieldWithPath("antecedentsResponses[].projectRole").description("포지션").attributes(field("constraint", "문자열")),
//                                        fieldWithPath("antecedentsResponses[].startYear").description("프로젝트 시작 기간").attributes(field("constraint", "양의 정수")),
//                                        fieldWithPath("antecedentsResponses[].startMonth").description("프로젝트 시작 월").attributes(field("constraint", "양의 정수")),
//                                        fieldWithPath("antecedentsResponses[].endYear").description("프로젝트 종료 기간").attributes(field("constraint", "양의 정수")),
//                                        fieldWithPath("antecedentsResponses[].endMonth").description("프로젝트 종료 월").attributes(field("constraint", "양의 정수")),
//                                        fieldWithPath("antecedentsResponses[].retirement").description("재직 여부").attributes(field("constraint", "boolean")),
//
//                                        subsectionWithPath("miniProfileResponse").description("미니 프로필(내 이력서) 항목").attributes(field("constraint", "객체 (배열)")),
//                                        fieldWithPath("miniProfileResponse.profileTitle").description("프로필 제목"),
//                                        fieldWithPath("miniProfileResponse.uploadPeriod").description("프로필 업로드 기간").attributes(field("constraint", "LocalDate")),
//                                        fieldWithPath("miniProfileResponse.uploadDeadline").description("마감 선택 여부"),
//                                        fieldWithPath("miniProfileResponse.myValue").description("협업 시 중요한 나의 가치"),
//                                        fieldWithPath("miniProfileResponse.skillSets").description("나의 스킬셋"),
//
//                                        // memberNameResponse
//                                        subsectionWithPath("memberNameResponse").description("회원 이름 정보"),
//                                        fieldWithPath("memberNameResponse.memberName").type(JsonFieldType.STRING).description("회원(기본 정보)에 해당하는 회원 이름")
//                                )
//                        )
//                );
//    }
//}
