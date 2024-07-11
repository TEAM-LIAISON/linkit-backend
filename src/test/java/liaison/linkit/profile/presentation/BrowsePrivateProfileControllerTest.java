//package liaison.linkit.profile.presentation;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import groovy.util.logging.Slf4j;
//import jakarta.servlet.http.Cookie;
//import liaison.linkit.global.ControllerTest;
//import liaison.linkit.login.domain.MemberTokens;
//import liaison.linkit.profile.browse.ProfileBrowseAccessInterceptor;
//import liaison.linkit.profile.dto.response.ProfileIntroductionResponse;
//import liaison.linkit.profile.dto.response.ProfileResponse;
//import liaison.linkit.profile.dto.response.antecedents.AntecedentsResponse;
//import liaison.linkit.profile.dto.response.attach.AttachResponse;
//import liaison.linkit.profile.dto.response.attach.AttachUrlResponse;
//import liaison.linkit.profile.dto.response.awards.AwardsResponse;
//import liaison.linkit.profile.dto.response.completion.CompletionResponse;
//import liaison.linkit.profile.dto.response.education.EducationResponse;
//import liaison.linkit.profile.dto.response.isValue.ProfileIsValueResponse;
//import liaison.linkit.profile.dto.response.miniProfile.MiniProfileResponse;
//import liaison.linkit.profile.dto.response.onBoarding.JobAndSkillResponse;
//import liaison.linkit.profile.dto.response.profileRegion.ProfileRegionResponse;
//import liaison.linkit.profile.dto.response.teamBuilding.ProfileTeamBuildingFieldResponse;
//import liaison.linkit.profile.service.*;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
//import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
//import org.springframework.restdocs.payload.JsonFieldType;
//import org.springframework.test.web.servlet.ResultActions;
//
//import java.time.LocalDate;
//import java.util.Arrays;
//import java.util.List;
//
//import static liaison.linkit.global.restdocs.RestDocsConfiguration.field;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.BDDMockito.given;
//import static org.mockito.Mockito.doNothing;
//import static org.springframework.http.HttpHeaders.AUTHORIZATION;
//import static org.springframework.http.MediaType.APPLICATION_JSON;
//import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
//import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
//import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
//import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
//import static org.springframework.restdocs.payload.PayloadDocumentation.*;
//import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@WebMvcTest(BrowsePrivateProfileController.class)
//@MockBean(JpaMetamodelMappingContext.class)
//@AutoConfigureRestDocs
//@Slf4j
//public class BrowsePrivateProfileControllerTest extends ControllerTest {
//    private static final MemberTokens MEMBER_TOKENS = new MemberTokens("refreshToken", "accessToken");
//    private static final Cookie COOKIE = new Cookie("refresh-token", MEMBER_TOKENS.getRefreshToken());
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @MockBean
//    public ProfileOnBoardingService profileOnBoardingService;
//    @MockBean
//    public ProfileService profileService;
//    @MockBean
//    public MiniProfileService miniProfileService;
//    @MockBean
//    public CompletionService completionService;
//    @MockBean
//    public TeamBuildingFieldService teamBuildingFieldService;
//    @MockBean
//    public AntecedentsService antecedentsService;
//    @MockBean
//    public EducationService educationService;
//    @MockBean
//    public AwardsService awardsService;
//    @MockBean
//    public AttachService attachService;
//    @MockBean
//    public ProfileRegionService profileRegionService;
//    @MockBean
//    public BrowsePrivateProfileService browsePrivateProfileService;
//
//    @MockBean
//    public ProfileBrowseAccessInterceptor profileBrowseAccessInterceptor;
//
//    @BeforeEach
//    void setUp() {
//        given(refreshTokenRepository.existsById(any())).willReturn(true);
//        doNothing().when(jwtProvider).validateTokens(any());
//        given(jwtProvider.getSubject(any())).willReturn("1");
//        doNothing().when(profileService).validateProfileByMember(1L);
//        doNothing().when(miniProfileService).validateMiniProfileByMember(1L);
//        doNothing().when(browsePrivateProfileService).validatePrivateProfileByMiniProfile(1L);
//
//    }
//
//    private ResultActions performGetBrowseProfileRequest(
//            final int miniProfileId
//    ) throws Exception {
//        return mockMvc.perform(
//                RestDocumentationRequestBuilders.get("/browse/private/profile/{miniProfileId}", miniProfileId)
//                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
//                        .cookie(COOKIE)
//                        .contentType(APPLICATION_JSON)
//        );
//    }
//
//    @DisplayName("다른 사용자의 내 이력서를 열람할 수 있다.")
//    @Test
//    void getBrowsePrivateProfile() throws Exception {
//
//        // given
//
//        given(browsePrivateProfileService.getTargetPrivateProfileIdByMiniProfileId(1L)).willReturn(1L);
//
//        final ProfileIsValueResponse profileIsValueResponse = new ProfileIsValueResponse(
//                true,
//                true,
//                true,
//                true,
//                true,
//                true,
//                true,
//                true,
//                true
//        );
//
//        given(browsePrivateProfileService.getProfileIsValue(1L)).willReturn(profileIsValueResponse);
//
//        // 1. 미니 프로필
//        final MiniProfileResponse miniProfileResponse = new MiniProfileResponse(
//                1L,
//                "시니어 소프트웨어 개발자",
//                LocalDate.of(2024, 10, 20),
//                true,
//                "https://image.linkit.im/images/linkit_logo.png",
//                "혁신, 팀워크, 의지",
//                Arrays.asList("2024 레드닷 수상", "스타트업 경력", "서울대 디자인", "대기업 경력 3년"),
//                "권동민"
//        );
//        given(miniProfileService.getPersonalMiniProfile(1L)).willReturn(miniProfileResponse);
//
//        // 2. 완성도 & 존재 여부 (V)
//        final CompletionResponse completionResponse = new CompletionResponse(
//                "100.0",
//                true,
//                true,
//                true,
//                true,
//                true,
//                true,
//                true,
//                true
//        );
//        given(completionService.getCompletion(1L)).willReturn(completionResponse);
//
//        // 3. 자기소개
//        final ProfileIntroductionResponse profileIntroductionResponse = new ProfileIntroductionResponse(
//                "안녕하세요, 저는 다양한 프로젝트와 혁신적인 아이디어를 구현하는 데 열정을 가진 기획자입니다. 대학에서 경영학을 전공하고, 여러 기업에서 프로젝트 매니저와 기획자로서의 경험을 쌓아왔습니다.."
//        );
//
//        given(profileService.getProfileIntroduction(1L)).willReturn(profileIntroductionResponse);
//
//        // 4. 보유기술 (V)
//        List<String> jobRoleNames = Arrays.asList("공모전, 대회, 창업");
//        List<String> skillNames = Arrays.asList("Notion, Figma");
//        final JobAndSkillResponse jobAndSkillResponse = new JobAndSkillResponse(jobRoleNames, skillNames);
//        given(profileOnBoardingService.getJobAndSkill(1L)).willReturn(jobAndSkillResponse);
//
//        // 5. 희망 팀빌딩 분야 (V)
//        List<String> teamBuildingFieldNames = Arrays.asList("공모전", "대회", "창업");
//        final ProfileTeamBuildingFieldResponse profileTeamBuildingFieldResponse = new ProfileTeamBuildingFieldResponse(
//                teamBuildingFieldNames
//        );
//
//        given(teamBuildingFieldService.getAllProfileTeamBuildingFields(1L))
//                .willReturn(profileTeamBuildingFieldResponse);
//
//        // 6. 활동 지역 및 위치 (V)
//        final ProfileRegionResponse profileRegionResponse = new ProfileRegionResponse(
//                "서울특별시",
//                "강남구"
//        );
//
//        given(profileRegionService.getPersonalProfileRegion(1L)).willReturn(profileRegionResponse);
//
//        // 7. 이력 (V)
//        final AntecedentsResponse antecedentsResponse1 = new AntecedentsResponse(
//                2L,
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
//                3L,
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
//        // 8. 학력
//        final EducationResponse educationResponse1 = new EducationResponse(
//                2L,
//                2022,
//                2024,
//                "홍익대학교",
//                "컴퓨터공학과",
//                "재학 중"
//        );
//
//        final EducationResponse educationResponse2 = new EducationResponse(
//                3L,
//                2021,
//                2025,
//                "홍익대학교",
//                "예술학과",
//                "졸업"
//        );
//
//        List<EducationResponse> educationResponses = Arrays.asList(educationResponse1, educationResponse2);
//        given(educationService.getAllEducations(1L))
//                .willReturn(educationResponses);
//
//        // 9. 수상
//        final AwardsResponse firstAwardsResponse = new AwardsResponse(
//                2L,
//                "홍익대학교 창업경진대회",
//                "대상",
//                "홍익대학교 창업교육센터",
//                2024,
//                5,
//                "홍익대학교 창업경진대회에서 1등이라는 성과를 이뤄냈습니다."
//        );
//
//        final AwardsResponse secondAwardsResponse = new AwardsResponse(
//                3L,
//                "성균관대학교 캠퍼스타운 입주경진대회",
//                "선정",
//                "성균관대학교 캠퍼스타운",
//                2024,
//                3,
//                "성균관대학교 캠퍼스타운에 최종 선정되었습니다."
//        );
//        final List<AwardsResponse> awardsResponses = Arrays.asList(firstAwardsResponse, secondAwardsResponse);
//        given(awardsService.getAllAwards(1L)).willReturn(awardsResponses);
//
//        // 10. 첨부
//        final AttachUrlResponse firstAttachUrlResponse = new AttachUrlResponse(
//                2L,
//                "깃허브",
//                "https://github.com/TEAM-LIAISON"
//        );
//
//        final AttachUrlResponse secondAttachUrlResponse = new AttachUrlResponse(
//                3L,
//                "노션",
//                "https://www.notion.so/ko-kr"
//        );
//
//        final List<AttachUrlResponse> attachUrlResponseList = Arrays.asList(firstAttachUrlResponse, secondAttachUrlResponse);
//        final AttachResponse attachResponses = new AttachResponse(
//                attachUrlResponseList
//        );
//
//        given(attachService.getAttachList(1L)).willReturn(attachResponses);
//
//        final ProfileResponse profileResponse = new ProfileResponse(
//                miniProfileResponse,
//                completionResponse,
//                profileIntroductionResponse,
//                jobAndSkillResponse,
//                profileTeamBuildingFieldResponse,
//                profileRegionResponse,
//                antecedentsResponses,
//                educationResponses,
//                awardsResponses,
//                attachResponses
//        );
//
//        given(browsePrivateProfileService.getProfileResponse(
//                miniProfileResponse,
//                completionResponse,
//                profileIntroductionResponse,
//                jobAndSkillResponse,
//                profileTeamBuildingFieldResponse,
//                profileRegionResponse,
//                antecedentsResponses,
//                educationResponses,
//                awardsResponses,
//                attachResponses
//        )).willReturn(profileResponse);
//
//        // when
//        final ResultActions resultActions = performGetBrowseProfileRequest(1);
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
//                                        // miniProfileResponse
//                                        subsectionWithPath("miniProfileResponse").description("사용자의 미니 프로필 정보"),
//                                        fieldWithPath("miniProfileResponse.profileTitle").type(JsonFieldType.STRING).description("프로필의 제목"),
//                                        fieldWithPath("miniProfileResponse.uploadPeriod").type(JsonFieldType.STRING).description("프로필 업로드 주기"),
//                                        fieldWithPath("miniProfileResponse.uploadDeadline").type(JsonFieldType.BOOLEAN).description("업로드 마감일 여부"),
//                                        fieldWithPath("miniProfileResponse.miniProfileImg").type(JsonFieldType.STRING).description("미니 프로필 이미지 URL"),
//                                        fieldWithPath("miniProfileResponse.myValue").type(JsonFieldType.STRING).description("사용자의 가치"),
//                                        fieldWithPath("miniProfileResponse.myKeywordNames").type(JsonFieldType.ARRAY).description("나를 소개하는 키워드 목록"),
//                                        fieldWithPath("miniProfileResponse.memberName").type(JsonFieldType.STRING).description("회원 이름")
//                                )));
//    }
//}
