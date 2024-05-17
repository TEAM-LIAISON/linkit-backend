package liaison.linkit.profile.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import liaison.linkit.global.ControllerTest;
import liaison.linkit.login.domain.MemberTokens;
import liaison.linkit.profile.dto.request.*;
import liaison.linkit.profile.dto.request.skill.ProfileSkillCreateRequest;
import liaison.linkit.profile.dto.request.teamBuilding.ProfileTeamBuildingCreateRequest;
import liaison.linkit.profile.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
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

    @BeforeEach
    void setUp() {
        given(refreshTokenRepository.existsById(any())).willReturn(true);
        doNothing().when(jwtProvider).validateTokens(any());
        given(jwtProvider.getSubject(any())).willReturn("1");
        given(profileService.validateProfileByMember(1L)).willReturn(1L);
    }

    private void makeProfile() throws Exception {
        final ProfileCreateRequest createRequest = new ProfileCreateRequest(
                "자기소개를 생성합니다."
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

    @DisplayName("내 이력서 기본 입력 항목을 생성할 수 있다.")
    @Test
    void createDefaultProfile() throws Exception {
        // given
        List<String> teamBuildingFieldNames = Arrays.asList("공모전", "대회", "창업");
        final ProfileTeamBuildingCreateRequest profileTeamBuildingCreateRequest = new ProfileTeamBuildingCreateRequest(teamBuildingFieldNames);

        List<String> skillNames = Arrays.asList("Figma", "Notion");
        final ProfileSkillCreateRequest profileSkillCreateRequest = new ProfileSkillCreateRequest(skillNames);

        final List<EducationCreateRequest> educationCreateRequests = new ArrayList<>();
        final EducationCreateRequest firstEduRequest = new EducationCreateRequest(
                2022,
                1,
                2024,
                9,
                "홍익대학교 학생",
                "홍익대학교",
                "학사",
                "컴퓨터공학과"
        );
        final EducationCreateRequest secondEduRequest = new EducationCreateRequest(
                2021,
                3,
                2025,
                9,
                "홍익대학교 졸업생",
                "홍익대학교",
                "졸업",
                "예술학과"
        );
        educationCreateRequests.add(firstEduRequest);
        educationCreateRequests.add(secondEduRequest);

        final List<AntecedentsCreateRequest> antecedentsCreateRequests = new ArrayList<>();
        final AntecedentsCreateRequest firstRequest = new AntecedentsCreateRequest(
                "linkit",
                "프로젝트 매니저",
                2023,
                3,
                2024,
                10,
                "팀빌딩 매칭 서비스"
        );
        final AntecedentsCreateRequest secondRequest = new AntecedentsCreateRequest(
                "오더이즈",
                "프로젝트 매니저",
                2023,
                3,
                2023,
                6,
                "QR 코드 활용 키오스크 주문 플랫폼"
        );
        antecedentsCreateRequests.add(firstRequest);
        antecedentsCreateRequests.add(secondRequest);

        final DefaultProfileCreateRequest defaultProfileCreateRequest = new DefaultProfileCreateRequest(
                profileTeamBuildingCreateRequest,
                profileSkillCreateRequest,
                educationCreateRequests,
                antecedentsCreateRequests
        );

        // when
        final ResultActions resultActions = performPostDefaultRequest(defaultProfileCreateRequest);

        // then
        resultActions.andExpect(status().isOk())
                .andDo(restDocs.document(
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
                                subsectionWithPath("profileTeamBuildingResponse").description("희망 팀빌딩 분야 항목").attributes(field("constraint", "객체")),
                                fieldWithPath("profileTeamBuildingResponse.teamBuildingFieldNames").description("희망 팀빌딩 분야 이름").attributes(field("constraint", "문자열(배열)")),
                                subsectionWithPath("profileSkillCreateRequest").description("보유 기술 항목").attributes(field("constraint", "객체")),
                                fieldWithPath("profileSkillCreateRequest.skillNames").description("보유 기술 이름").attributes(field("constraint", "문자열(배열)")),
                                subsectionWithPath("educationCreateRequest").description("학력 항목").attributes(field("constraint", "객체 (배열)")),
                                fieldWithPath("educationCreateRequest[].admissionYear").description("입학 연도").attributes(field("constraint", "양의 정수")),
                                fieldWithPath("educationCreateRequest[].admissionMonth").description("입학 월").attributes(field("constraint", "양의 정수")),
                                fieldWithPath("educationCreateRequest[].graduationYear").description("졸업 연도").attributes(field("constraint", "양의 정수")),
                                fieldWithPath("educationCreateRequest[].graduationMonth").description("졸업 월").attributes(field("constraint", "양의 정수")),
                                fieldWithPath("educationCreateRequest[].educationDescription").description("학력 설명").attributes(field("constraint", "문자열")),
                                fieldWithPath("educationCreateRequest[].schoolName").description("학교 이름").attributes(field("constraint", "문자열")),
                                fieldWithPath("educationCreateRequest[].degreeName").description("학위 이름").attributes(field("constraint", "문자열")),
                                fieldWithPath("educationCreateRequest[].majorName").description("전공 이름").attributes(field("constraint", "문자열")),
                                subsectionWithPath("antecedentsCreateRequest").description("이력 항목").attributes(field("constraint", "객체(배열)")),
                                fieldWithPath("antecedentsCreateRequest[].projectName").description("프로젝트명/회사명").attributes(field("constraint", "문자열")),
                                fieldWithPath("antecedentsCreateRequest[].projectRole").description("포지션").attributes(field("constraint", "문자열")),
                                fieldWithPath("antecedentsCreateRequest[].startYear").description("프로젝트 시작 기간").attributes(field("constraint", "양의 정수")),
                                fieldWithPath("antecedentsCreateRequest[].startMonth").description("프로젝트 시작 월").attributes(field("constraint", "양의 정수")),
                                fieldWithPath("antecedentsCreateRequest[].endYear").description("프로젝트 종료 기간").attributes(field("constraint", "양의 정수")),
                                fieldWithPath("antecedentsCreateRequest[].endMonth").description("프로젝트 종료 월").attributes(field("constraint", "양의 정수")),
                                fieldWithPath("antecedentsCreateRequest[].antecedentsDescription").description("이력 항목 설명").attributes(field("constraint", "문자열"))
                        )
                ));
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
