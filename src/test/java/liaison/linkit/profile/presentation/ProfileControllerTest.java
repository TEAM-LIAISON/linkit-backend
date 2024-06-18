package liaison.linkit.profile.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import groovy.util.logging.Slf4j;
import jakarta.servlet.http.Cookie;
import liaison.linkit.global.ControllerTest;
import liaison.linkit.login.domain.MemberTokens;
import liaison.linkit.profile.dto.request.DefaultProfileCreateRequest;
import liaison.linkit.profile.dto.request.ProfileCreateRequest;
import liaison.linkit.profile.dto.request.ProfileUpdateRequest;
import liaison.linkit.profile.dto.response.*;
import liaison.linkit.profile.dto.response.Attach.AttachFileResponse;
import liaison.linkit.profile.dto.response.Attach.AttachResponse;
import liaison.linkit.profile.dto.response.Attach.AttachUrlResponse;
import liaison.linkit.profile.dto.response.IsValue.ProfileIsValueResponse;
import liaison.linkit.profile.dto.response.IsValue.ProfileOnBoardingIsValueResponse;
import liaison.linkit.profile.service.*;
import liaison.linkit.region.dto.response.ProfileRegionResponse;
import liaison.linkit.region.service.ProfileRegionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProfileController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
@Slf4j
class ProfileControllerTest extends ControllerTest {

    private static final MemberTokens MEMBER_TOKENS = new MemberTokens("refreshToken", "accessToken");
    private static final Cookie COOKIE = new Cookie("refresh-token", MEMBER_TOKENS.getRefreshToken());

    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ProfileService profileService;
    @MockBean
    private MiniProfileService miniProfileService;
    @MockBean
    private CompletionService completionService;
    @MockBean
    private ProfileSkillService profileSkillService;
    @MockBean
    private ProfileTeamBuildingFieldService profileTeamBuildingFieldService;
    @MockBean
    private AntecedentsService antecedentsService;
    @MockBean
    private EducationService educationService;
    @MockBean
    private AwardsService awardsService;
    @MockBean
    private AttachService attachService;
    @MockBean
    private ProfileRegionService profileRegionService;

    @BeforeEach
    void setUp() {
        given(refreshTokenRepository.existsById(any())).willReturn(true);
        doNothing().when(jwtProvider).validateTokens(any());
        given(jwtProvider.getSubject(any())).willReturn("1");

        // 서비스 계층 -> validate -> 리턴하는 값 (1L) 지정.
        doNothing().when(profileService).validateProfileByMember(1L);

        // 미니 프로필 리턴 Value 1L
        doNothing().when(miniProfileService).validateMiniProfileByMember(1L);
    }

    private void makeProfile() throws Exception {
        final ProfileCreateRequest createRequest = new ProfileCreateRequest(
                "자기소개를 생성합니다."
        );
    }

    private ResultActions performGetOnBoardingProfileRequest() throws Exception {
        return mockMvc.perform(
                get("/profile/onBoarding")
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
                        .contentType(APPLICATION_JSON)
        );
    }

    private ResultActions performGetProfileRequest() throws Exception {
        return mockMvc.perform(
                get("/profile")
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
                        .contentType(APPLICATION_JSON)
        );
    }

    private ResultActions performPostDefaultRequest(
            DefaultProfileCreateRequest defaultProfileCreateRequest) throws Exception
    {
        return mockMvc.perform(
                post("/profile/default")
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(defaultProfileCreateRequest))
        );
    }

    private ResultActions performGetRequest() throws Exception {
        return mockMvc.perform(
                get("/profile/introduction")
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
        );
    }

    private ResultActions performPatchRequest(final ProfileUpdateRequest updateRequest) throws Exception {
        return mockMvc.perform(
                patch("/profile/introduction")
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest))
        );
    }

    private ResultActions performDeleteRequest() throws Exception {
        return mockMvc.perform(
                delete("/profile")
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
        );
    }

    @DisplayName("내 이력서 온보딩 과정의 모든 정보를 조회할 수 있다.")
    @Test
    void getOnBoardingProfile() throws Exception {
        // given
        final ProfileOnBoardingIsValueResponse profileOnBoardingIsValueResponse = new ProfileOnBoardingIsValueResponse(
                true,
                true,
                true,
                true,
                true,
                true
        );

        given(profileService.getProfileOnBoardingIsValue(1L)).willReturn(profileOnBoardingIsValueResponse);
        // 1. 희망 팀빌딩 분야
        List<String> teamBuildingFieldNames = Arrays.asList("공모전", "대회", "창업");
        final ProfileTeamBuildingFieldResponse profileTeamBuildingFieldResponse = new ProfileTeamBuildingFieldResponse(
                teamBuildingFieldNames
        );

        given(profileTeamBuildingFieldService.getAllProfileTeamBuildings(1L))
                .willReturn(profileTeamBuildingFieldResponse);

        // 2. 역할 및 기술
        List<String> roleFields = Arrays.asList("SW 개발자", "SW 개발자");
        List<String> skillNames = Arrays.asList("Java", "React");
        final ProfileSkillResponse profileSkillResponse = ProfileSkillResponse.of(roleFields, skillNames);

        given(profileSkillService.getAllProfileSkills(1L)).willReturn(profileSkillResponse);


        // 3. 지역 및 위치 정보
        final ProfileRegionResponse profileRegionResponse = new ProfileRegionResponse(
                "서울특별시",
                "강남구"
        );

        given(profileRegionService.getPersonalProfileRegion(1L)).willReturn(profileRegionResponse);

        // 3. 학교 정보
         final EducationResponse educationResponse1 = new EducationResponse(
                 1L,
                 2022,
                 2024,
                 "홍익대학교",
                 "컴퓨터공학과",
                 "재학 중"
         );

        final EducationResponse educationResponse2 = new EducationResponse(
                2L,
                2021,
                2025,
                "홍익대학교",
                "예술학과",
                "졸업"
        );

        List<EducationResponse> educationResponses = Arrays.asList(educationResponse1, educationResponse2);

        given(educationService.getAllEducations(1L))
                .willReturn(educationResponses);

        // 4. 이력 정보
        final AntecedentsResponse antecedentsResponse1 = new AntecedentsResponse(
            1L,
                "linkit",
                "프로젝트 매니저",
                2023,
                3,
                2024,
                10,
                false
        );

        final AntecedentsResponse antecedentsResponse2 = new AntecedentsResponse(
                2L,
                "오더이즈",
                "프로젝트 매니저",
                2023,
                3,
                2023,
                6,
                false
        );

        List<AntecedentsResponse> antecedentsResponses = Arrays.asList(antecedentsResponse1, antecedentsResponse2);

        given(antecedentsService.getAllAntecedents(1L)).willReturn(antecedentsResponses);

        // 5. 미니 프로필 정보
        final MiniProfileResponse miniProfileResponse = new MiniProfileResponse(
                "시니어 소프트웨어 개발자",
                LocalDate.of(2024, 10,20),
                true,
                "https://image.linkit.im/images/linkit_logo.png",
                "혁신, 팀워크, 의지",
                "Java, Spring, AWS, Microservices, Docker"
        );

        given(miniProfileService.getPersonalMiniProfile(1L)).willReturn(miniProfileResponse);

        // 온보딩 응답
        final OnBoardingProfileResponse onBoardingProfileResponse = new OnBoardingProfileResponse(
                profileTeamBuildingFieldResponse,
                profileSkillResponse,
                profileRegionResponse,
                educationResponses,
                antecedentsResponses,
                miniProfileResponse
        );

        given(profileService.getOnBoardingProfile(
                profileTeamBuildingFieldResponse,
                profileSkillResponse,
                profileRegionResponse,
                educationResponses,
                antecedentsResponses,
                miniProfileResponse))
                .willReturn(onBoardingProfileResponse);

        // when
        final ResultActions resultActions = performGetOnBoardingProfileRequest();

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
                                        subsectionWithPath("profileTeamBuildingFieldResponse").description("희망 팀빌딩 분야 항목").attributes(field("constraint", "객체")),
                                        fieldWithPath("profileTeamBuildingFieldResponse.teamBuildingFieldNames").description("희망 팀빌딩 분야 이름").attributes(field("constraint", "문자열(배열)")),

                                        subsectionWithPath("profileSkillResponse").description("보유 기술 및 역할 목록").attributes(field("constraint", "객체")),
                                        fieldWithPath("profileSkillResponse.roleFields[]").description("보유한 역할").attributes(field("constraint", "문자열(배열)")),
                                        fieldWithPath("profileSkillResponse.skillNames[]").description("보유한 기술 이름").attributes(field("constraint", "문자열(배열)")),

                                        subsectionWithPath("profileRegionResponse").description("지역 및 위치 항목").attributes(field("constraint", "객체")),
                                        fieldWithPath("profileRegionResponse.cityName").description("시/구 이름").attributes(field("constraint", "문자열")),
                                        fieldWithPath("profileRegionResponse.divisionName").description("시/군/구 이름").attributes(field("constraint", "문자열")),

                                        subsectionWithPath("educationResponses").description("학력 항목").attributes(field("constraint", "객체 (배열)")),
                                        fieldWithPath("educationResponses[].id").description("학력 ID").attributes(field("constraint", "양의 정수")),
                                        fieldWithPath("educationResponses[].admissionYear").description("입학 연도").attributes(field("constraint", "양의 정수")),
                                        fieldWithPath("educationResponses[].graduationYear").description("졸업 연도").attributes(field("constraint", "양의 정수")),
                                        fieldWithPath("educationResponses[].universityName").description("학교 이름").attributes(field("constraint", "문자열")),
                                        fieldWithPath("educationResponses[].majorName").description("전공 이름").attributes(field("constraint", "문자열")),
                                        fieldWithPath("educationResponses[].degreeName").description("학위 이름").attributes(field("constraint", "문자열")),

                                        subsectionWithPath("antecedentsResponses").description("이력 항목").attributes(field("constraint", "객체(배열)")),
                                        fieldWithPath("antecedentsResponses[].projectName").description("회사 이름").attributes(field("constraint", "문자열")),
                                        fieldWithPath("antecedentsResponses[].projectRole").description("포지션").attributes(field("constraint", "문자열")),
                                        fieldWithPath("antecedentsResponses[].startYear").description("프로젝트 시작 기간").attributes(field("constraint", "양의 정수")),
                                        fieldWithPath("antecedentsResponses[].startMonth").description("프로젝트 시작 월").attributes(field("constraint", "양의 정수")),
                                        fieldWithPath("antecedentsResponses[].endYear").description("프로젝트 종료 기간").attributes(field("constraint", "양의 정수")),
                                        fieldWithPath("antecedentsResponses[].endMonth").description("프로젝트 종료 월").attributes(field("constraint", "양의 정수")),
                                        fieldWithPath("antecedentsResponses[].retirement").description("재직 여부").attributes(field("constraint", "boolean")),

                                        subsectionWithPath("miniProfileResponse").description("미니 프로필(내 이력서) 항목").attributes(field("constraint", "객체 (배열)")),
                                        fieldWithPath("miniProfileResponse.profileTitle").description("프로필 제목"),
                                        fieldWithPath("miniProfileResponse.uploadPeriod").description("프로필 업로드 기간").attributes(field("constraint", "LocalDate")),
                                        fieldWithPath("miniProfileResponse.uploadDeadline").description("마감 선택 여부"),
                                        fieldWithPath("miniProfileResponse.myValue").description("협업 시 중요한 나의 가치"),
                                        fieldWithPath("miniProfileResponse.skillSets").description("나의 스킬셋")
                                )
                        )
                );
    }

    @DisplayName("내 이력서를 조회할 수 있다.")
    @Test
    void getProfile() throws Exception {
        // given
        final ProfileIsValueResponse profileIsValueResponse = new ProfileIsValueResponse(
                true,
                true,
                true,
                true,
                true,
                true,
                true,
                true,
                true
        );

        given(profileService.getProfileIsValue(1L)).willReturn(profileIsValueResponse);

        // 1. 미니 프로필 (V)
        final MiniProfileResponse miniProfileResponse = new MiniProfileResponse(
                "시니어 소프트웨어 개발자",
                LocalDate.of(2024, 10,20),
                true,
                "https://image.linkit.im/images/linkit_logo.png",
                "혁신, 팀워크, 의지",
                "Java, Spring, AWS, Microservices, Docker"
        );

        given(miniProfileService.getPersonalMiniProfile(1L)).willReturn(miniProfileResponse);

        // 2. 완성도 & 존재 여부 (V)
        final CompletionResponse completionResponse = new CompletionResponse(
            100.0,
                true,
                true,
                true,
                true,
                true,
                true,
                true,
                true
        );

        given(completionService.getCompletion(1L)).willReturn(completionResponse);

        // 3. 자기소개
        final ProfileIntroductionResponse profileIntroductionResponse = new ProfileIntroductionResponse(
                "안녕하세요, 저는 다양한 프로젝트와 혁신적인 아이디어를 구현하는 데 열정을 가진 기획자입니다. 대학에서 경영학을 전공하고, 여러 기업에서 프로젝트 매니저와 기획자로서의 경험을 쌓아왔습니다.."
        );
        given(profileService.getProfileIntroduction(1L)).willReturn(profileIntroductionResponse);

        // 4. 보유기술 (V)
        List<String> roleFields = Arrays.asList("SW 개발자", "SW 개발자");
        List<String> skillNames = Arrays.asList("Java", "React");
        final ProfileSkillResponse profileSkillResponse = ProfileSkillResponse.of(roleFields, skillNames);
        given(profileSkillService.getAllProfileSkills(1L)).willReturn(profileSkillResponse);

        // 5. 희망 팀빌딩 분야 (V)
        List<String> teamBuildingFieldNames = Arrays.asList("공모전", "대회", "창업");
        final ProfileTeamBuildingFieldResponse profileTeamBuildingFieldResponse = new ProfileTeamBuildingFieldResponse(
                teamBuildingFieldNames
        );

        given(profileTeamBuildingFieldService.getAllProfileTeamBuildings(1L))
                .willReturn(profileTeamBuildingFieldResponse);

        // 6. 활동 지역 및 위치 (V)
        final ProfileRegionResponse profileRegionResponse = new ProfileRegionResponse(
                "서울특별시",
                "강남구"
        );

        given(profileRegionService.getPersonalProfileRegion(1L)).willReturn(profileRegionResponse);

        // 7. 이력 (V)
        final AntecedentsResponse antecedentsResponse1 = new AntecedentsResponse(
                1L,
                "linkit",
                "프로젝트 매니저",
                2023,
                3,
                2024,
                10,
                false
        );

        final AntecedentsResponse antecedentsResponse2 = new AntecedentsResponse(
                2L,
                "오더이즈",
                "프로젝트 매니저",
                2023,
                3,
                2023,
                6,
                false
        );

        List<AntecedentsResponse> antecedentsResponses = Arrays.asList(antecedentsResponse1, antecedentsResponse2);

        given(antecedentsService.getAllAntecedents(1L)).willReturn(antecedentsResponses);

        // 8. 학력
        final EducationResponse educationResponse1 = new EducationResponse(
                1L,
                2022,
                2024,
                "홍익대학교",
                "컴퓨터공학과",
                "재학 중"
        );

        final EducationResponse educationResponse2 = new EducationResponse(
                2L,
                2021,
                2025,
                "홍익대학교",
                "예술학과",
                "졸업"
        );

        List<EducationResponse> educationResponses = Arrays.asList(educationResponse1, educationResponse2);
        given(educationService.getAllEducations(1L))
                .willReturn(educationResponses);

        // 9. 수상
        final AwardsResponse firstAwardsResponse = new AwardsResponse(
            1L,
                "홍익대학교 창업경진대회",
                "대상",
                "홍익대학교 창업교육센터",
                2024,
                5,
                "홍익대학교 창업경진대회에서 1등이라는 성과를 이뤄냈습니다."
        );

        final AwardsResponse secondAwardsResponse = new AwardsResponse(
                2L,
                "성균관대학교 캠퍼스타운 입주경진대회",
                "선정",
                "성균관대학교 캠퍼스타운",
                2024,
                3,
                "성균관대학교 캠퍼스타운에 최종 선정되었습니다."
        );
        final List<AwardsResponse> awardsResponses = Arrays.asList(firstAwardsResponse, secondAwardsResponse);
        given(awardsService.getAllAwards(1L)).willReturn(awardsResponses);

        // 10. 첨부
        final AttachUrlResponse firstAttachUrlResponse = new AttachUrlResponse(
                "깃허브",
                "https://github.com/TEAM-LIAISON"
        );

        final AttachUrlResponse secondAttachUrlResponse = new AttachUrlResponse(
                "노션",
                "https://www.notion.so/ko-kr"
        );

        final AttachFileResponse firstAttachFileResponse = new AttachFileResponse(
            "https://linkit-dev-env-bucket.s3.ap-northeast-1.amazonaws.com/files/A4+-+1.pdf"
        );

        final List<AttachUrlResponse> attachUrlResponseList = Arrays.asList(firstAttachUrlResponse, secondAttachUrlResponse);
        final List<AttachFileResponse> attachFileResponseList = Arrays.asList(firstAttachFileResponse);
        final AttachResponse attachResponses = new AttachResponse(
                attachUrlResponseList,
                attachFileResponseList
        );

        final ProfileResponse profileResponse = new ProfileResponse(
                miniProfileResponse,
                completionResponse,
                profileIntroductionResponse,
                profileSkillResponse,
                profileTeamBuildingFieldResponse,
                antecedentsResponses,
                educationResponses,
                awardsResponses,
                attachResponses
        );

        given(profileService.getProfile(
                miniProfileResponse,
                completionResponse,
                profileIntroductionResponse,
                profileSkillResponse,
                profileTeamBuildingFieldResponse,
                antecedentsResponses,
                educationResponses,
                awardsResponses,
                attachResponses
        )).willReturn(profileResponse);

        // when
        final ResultActions resultActions = performGetProfileRequest();

        // then
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
//                                        subsectionWithPath("miniProfileResponse"),
//                                        subsectionWithPath("completionResponse"),
//                                        subsectionWithPath("miniProfileResponse"),
//                                        subsectionWithPath("miniProfileResponse"),
//
//
//                                )
//                        )
//                );
    }

//    @DisplayName("프로필 자기소개 항목을 조회할 수 있다.")
//    @Test
//    void getProfileIntroduction() throws Exception{
//        // given
//        final ProfileIntroductionResponse response = new ProfileIntroductionResponse(
//                "프로필 자기소개 항목입니다."
//        );
//
//        given(profileService.getProfileIntroduction(1L))
//                .willReturn(response);
//
//        // when
//        final ResultActions resultActions = performGetRequest();
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
//                                        fieldWithPath("introduction")
//                                                .type(JsonFieldType.STRING)
//                                                .description("자기 소개")
//                                                .attributes(field("constraint", "문자열"))
//                                )
//                        )
//                );
//    }
//
//
//    @DisplayName("프로필 자기소개 항목을 수정할 수 있다.")
//    @Test
//    void updateProfileIntroduction() throws Exception {
//        // given
//        final ProfileUpdateRequest updateRequest = new ProfileUpdateRequest(
//                "자기소개를 수정하려고 합니다."
//        );
//
//        doNothing().when(profileService).update(anyLong(), any(ProfileUpdateRequest.class));
//
//        // when
//        final ResultActions resultActions = performPatchRequest(updateRequest);
//
//        // then
//        resultActions.andExpect(status().isNoContent())
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
//                                requestFields(
//                                        fieldWithPath("introduction")
//                                                .type(JsonFieldType.STRING)
//                                                .description("자기 소개")
//                                                .attributes(field("constraint", "문자열"))
//                                )
//                        )
//                );
//    }
//
//    @DisplayName("프로필 자기소개 항목을 삭제할 수 있다.")
//    @Test
//    void deleteProfileIntroduction() throws Exception {
//        // given
//        makeProfile();
//        doNothing().when(profileService).deleteIntroduction(anyLong());
//
//        // when
//        final ResultActions resultActions = performDeleteRequest();
//        // then
//        resultActions.andExpect(status().isNoContent())
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
//                                )
//                        )
//                );
//    }
}
