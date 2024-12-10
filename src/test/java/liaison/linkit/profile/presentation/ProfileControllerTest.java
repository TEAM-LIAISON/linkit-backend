package liaison.linkit.profile.presentation;

import static liaison.linkit.global.restdocs.RestDocsConfiguration.field;
import static liaison.linkit.profile.domain.type.LogType.REPRESENTATIVE_LOG;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import groovy.util.logging.Slf4j;
import jakarta.servlet.http.Cookie;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import liaison.linkit.common.presentation.CommonResponse;
import liaison.linkit.common.presentation.RegionResponseDTO.RegionDetail;
import liaison.linkit.global.ControllerTest;
import liaison.linkit.login.domain.MemberTokens;
import liaison.linkit.profile.domain.portfolio.ProjectSize;
import liaison.linkit.profile.presentation.activity.dto.ProfileActivityResponseDTO;
import liaison.linkit.profile.presentation.activity.dto.ProfileActivityResponseDTO.ProfileActivityItem;
import liaison.linkit.profile.presentation.awards.dto.ProfileAwardsResponseDTO;
import liaison.linkit.profile.presentation.awards.dto.ProfileAwardsResponseDTO.ProfileAwardsItem;
import liaison.linkit.profile.presentation.education.dto.ProfileEducationResponseDTO;
import liaison.linkit.profile.presentation.education.dto.ProfileEducationResponseDTO.ProfileEducationItem;
import liaison.linkit.profile.presentation.license.dto.ProfileLicenseResponseDTO;
import liaison.linkit.profile.presentation.license.dto.ProfileLicenseResponseDTO.ProfileLicenseItem;
import liaison.linkit.profile.presentation.link.dto.ProfileLinkResponseDTO;
import liaison.linkit.profile.presentation.link.dto.ProfileLinkResponseDTO.ProfileLinkItem;
import liaison.linkit.profile.presentation.log.dto.ProfileLogResponseDTO;
import liaison.linkit.profile.presentation.log.dto.ProfileLogResponseDTO.ProfileLogItem;
import liaison.linkit.profile.presentation.miniProfile.dto.MiniProfileResponseDTO.ProfileCurrentStateItem;
import liaison.linkit.profile.presentation.portfolio.dto.ProfilePortfolioResponseDTO;
import liaison.linkit.profile.presentation.portfolio.dto.ProfilePortfolioResponseDTO.ProfilePortfolioItem;
import liaison.linkit.profile.presentation.profile.ProfileController;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO.ProfileTeamInform;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO.ProfileBooleanMenu;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO.ProfileCompletionMenu;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO.ProfileDetail;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO.ProfileInformMenu;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO.ProfileLeftMenu;
import liaison.linkit.profile.presentation.skill.dto.ProfileSkillResponseDTO;
import liaison.linkit.profile.presentation.skill.dto.ProfileSkillResponseDTO.ProfileSkillItem;
import liaison.linkit.profile.service.ProfileService;
import org.junit.jupiter.api.BeforeEach;
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

@WebMvcTest(ProfileController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
@Slf4j
class ProfileControllerTest extends ControllerTest {

    private static final MemberTokens MEMBER_TOKENS = new MemberTokens("accessToken", "refreshToken");
    private static final Cookie COOKIE = new Cookie("refreshToken", MEMBER_TOKENS.getRefreshToken());

    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ProfileService profileService;

    @BeforeEach
    void setUp() {
        given(refreshTokenRepository.existsById(any())).willReturn(true);
        doNothing().when(jwtProvider).validateTokens(any());
        given(jwtProvider.getSubject(any())).willReturn("1");
    }

    private ResultActions performGetProfileLeftMenu() throws Exception {
        return mockMvc.perform(
                get("/api/v1/profile/left/menu")
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
        );
    }

    private ResultActions performGetProfileDetail(final String emailId) throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders.get("/api/v1/profile/{emailId}", emailId)
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
        );
    }

    @DisplayName("회원이 나의 프로필 왼쪽 메뉴를 조회할 수 있다.")
    @Test
    void getProfileLeftMenu() throws Exception {
        // given
        final ProfileCompletionMenu profileCompletionMenu = new ProfileCompletionMenu(100);

        final ProfileCurrentStateItem firstProfileCurrentStateItem = new ProfileCurrentStateItem("팀 찾는 중");
        final ProfileCurrentStateItem secondProfileCurrentStateItem = new ProfileCurrentStateItem("공모전 준비 중");

        final List<ProfileCurrentStateItem> profileCurrentStates = Arrays.asList(firstProfileCurrentStateItem, secondProfileCurrentStateItem);

        final RegionDetail regionDetail = new RegionDetail("서울특별시", "강남구");

        final ProfileInformMenu profileInformMenu = ProfileInformMenu.builder()
                .profileCurrentStates(profileCurrentStates)
                .profileImagePath("프로필 이미지 경로")
                .memberName("권동민")
                .emailId("이메일 아이디")
                .isProfilePublic(true)
                .majorPosition("포지션 대분류")
                .regionDetail(regionDetail)
                .profileTeamInforms(Arrays.asList(
                        ProfileTeamInform.builder()
                                .teamName("리에종")
                                .teamLogoImagePath("팀 로고 이미지 경로")
                                .build(),
                        ProfileTeamInform.builder()
                                .teamName("팀명 2")
                                .teamLogoImagePath("팀 로고 이미지 경로 2")
                                .build()
                ))
                .build();

        final ProfileBooleanMenu profileBooleanMenu =
                new ProfileBooleanMenu(
                        false,
                        false,
                        false,
                        false,
                        false,
                        false,
                        false,
                        false);

        final ProfileLeftMenu profileLeftMenu =
                new ProfileLeftMenu(
                        profileCompletionMenu,
                        profileInformMenu,
                        profileBooleanMenu
                );

        // when
        when(profileService.getProfileLeftMenu(anyLong())).thenReturn(profileLeftMenu);

        final ResultActions resultActions = performGetProfileLeftMenu();

        final MvcResult mvcResult = resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value("true"))
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

                                        // ProfileCompletionMenu
                                        fieldWithPath("result.profileCompletionMenu.profileCompletion")
                                                .type(JsonFieldType.NUMBER)
                                                .description("프로필 완성도 값"),

                                        // ProfileInformMenu
                                        fieldWithPath("result.profileInformMenu.profileCurrentStates[].profileStateName")
                                                .type(JsonFieldType.STRING)
                                                .description("프로필 현재 상태 이름"),
                                        fieldWithPath("result.profileInformMenu.profileImagePath")
                                                .type(JsonFieldType.STRING)
                                                .description("프로필 이미지 경로"),
                                        fieldWithPath("result.profileInformMenu.memberName")
                                                .type(JsonFieldType.STRING)
                                                .description("프로필 사용자 이름"),
                                        fieldWithPath("result.profileInformMenu.emailId")
                                                .type(JsonFieldType.STRING)
                                                .description("회원 이메일 아이디"),
                                        fieldWithPath("result.profileInformMenu.isProfilePublic")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("프로필 공개 여부"),
                                        fieldWithPath("result.profileInformMenu.majorPosition")
                                                .type(JsonFieldType.STRING)
                                                .description("프로필 포지션 대분류"),

                                        // RegionDetail
                                        fieldWithPath("result.profileInformMenu.regionDetail.cityName")
                                                .type(JsonFieldType.STRING)
                                                .description("프로필 활동 지역 시/도"),
                                        fieldWithPath("result.profileInformMenu.regionDetail.divisionName")
                                                .type(JsonFieldType.STRING)
                                                .description("프로필 활동 지역 시/군/구"),

                                        // profileTeamInform
                                        fieldWithPath("result.profileInformMenu.profileTeamInforms[].teamName")
                                                .type(JsonFieldType.STRING)
                                                .description("프로필 회원이 속한 팀의 팀 이름"),
                                        fieldWithPath("result.profileInformMenu.profileTeamInforms[].teamLogoImagePath")
                                                .type(JsonFieldType.STRING)
                                                .description("프로필 회원이 속한 팀의 팀 로고 이미지 경로"),

                                        // profileBooleanMenu
                                        fieldWithPath("result.profileBooleanMenu.isMiniProfile")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("프로필 미니 프로필 기입 여부"),
                                        fieldWithPath("result.profileBooleanMenu.isProfileSkill")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("프로필 스킬 기입 여부"),
                                        fieldWithPath("result.profileBooleanMenu.isProfileActivity")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("프로필 이력 기입 여부"),
                                        fieldWithPath("result.profileBooleanMenu.isProfilePortfolio")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("프로필 포트폴리오 기입 여부"),
                                        fieldWithPath("result.profileBooleanMenu.isProfileEducation")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("프로필 학력 기입 여부"),
                                        fieldWithPath("result.profileBooleanMenu.isProfileAwards")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("프로필 수상 기입 여부"),
                                        fieldWithPath("result.profileBooleanMenu.isProfileLicense")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("프로필 자격증 기입 여부"),
                                        fieldWithPath("result.profileBooleanMenu.isProfileLink")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("프로필 링크 기입 여부")
                                )
                        )).andReturn();

        // JSON 응답에서 result 객체를 추출 및 검증
        final String jsonResponse = mvcResult.getResponse().getContentAsString();
        final CommonResponse<ProfileLeftMenu> actual = objectMapper.readValue(
                jsonResponse,
                new TypeReference<CommonResponse<ProfileLeftMenu>>() {
                }
        );

        final CommonResponse<ProfileLeftMenu> expected = CommonResponse.onSuccess(profileLeftMenu);

        // then
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @DisplayName("회원이 프로필을 조회할 수 있다.")
    @Test
    void getProfileDetail() throws Exception {
        // given
        final ProfileCompletionMenu profileCompletionMenu = new ProfileCompletionMenu(100);

        final ProfileCurrentStateItem firstProfileCurrentStateItem = new ProfileCurrentStateItem("팀 찾는 중");
        final ProfileCurrentStateItem secondProfileCurrentStateItem = new ProfileCurrentStateItem("공모전 준비 중");
        final List<ProfileCurrentStateItem> profileCurrentStates = Arrays.asList(firstProfileCurrentStateItem, secondProfileCurrentStateItem);

        final RegionDetail regionDetail = new RegionDetail("서울특별시", "강남구");

        final ProfileInformMenu profileInformMenu = ProfileInformMenu.builder()
                .profileCurrentStates(profileCurrentStates)
                .profileImagePath("프로필 이미지 경로")
                .memberName("권동민")
                .emailId("이메일 아이디")
                .isProfilePublic(true)
                .majorPosition("포지션 대분류")
                .regionDetail(regionDetail)
                .profileTeamInforms(Arrays.asList(
                        ProfileTeamInform.builder()
                                .teamName("리에종")
                                .teamLogoImagePath("팀 로고 이미지 경로")
                                .build(),
                        ProfileTeamInform.builder()
                                .teamName("팀명 2")
                                .teamLogoImagePath("팀 로고 이미지 경로 2")
                                .build()
                ))
                .build();

        final ProfileLogResponseDTO.ProfileLogItem profileLogItem
                = new ProfileLogItem(1L, true, REPRESENTATIVE_LOG, LocalDateTime.now(), "로그 제목", "로그 내용");

        final int profileScrapCount = 0;

        final ProfileSkillResponseDTO.ProfileSkillItem firstProfileSkillItem
                = new ProfileSkillItem(1L, "Figma", "상");

        final ProfileSkillResponseDTO.ProfileSkillItem secondProfileSkillItem
                = new ProfileSkillItem(2L, "Notion", "중상");

        final List<ProfileSkillItem> profileSkillItems
                = Arrays.asList(firstProfileSkillItem, secondProfileSkillItem);

        final ProfileActivityResponseDTO.ProfileActivityItem firstProfileActivityItem
                = new ProfileActivityItem(1L, "리에종", "PO", "2022.06", "2026.06", true, "이력 설명 1");

        final ProfileActivityResponseDTO.ProfileActivityItem secondProfileActivityItem
                = new ProfileActivityItem(2L, "리에종", "디자이너", "2024.10", "2024.12", true, "이력 설명 2");

        final List<ProfileActivityItem> profileActivityItems
                = Arrays.asList(firstProfileActivityItem, secondProfileActivityItem);

        final ProfilePortfolioResponseDTO.ProfilePortfolioItem firstProfilePortfolioItem
                = new ProfilePortfolioItem(1L, "프로젝트 이름", "프로젝트 한 줄 소개", ProjectSize.PERSONAL, "2023.02", "2023.04", false, Arrays.asList("컨텐츠 제작", "브랜딩", "계정 관리"), "logo.png");

        final ProfilePortfolioResponseDTO.ProfilePortfolioItem secondProfilePortfolioItem
                = new ProfilePortfolioItem(2L, "프로젝트 이름", "프로젝트 한 줄 소개", ProjectSize.TEAM, "2023.03", "2023.06", true, Arrays.asList("컨텐츠 제작", "브랜딩", "계정 관리"), "logo.png");

        final List<ProfilePortfolioItem> profilePortfolioItems
                = Arrays.asList(firstProfilePortfolioItem, secondProfilePortfolioItem);

        final ProfileEducationResponseDTO.ProfileEducationItem firstProfileEducationItem
                = new ProfileEducationItem(1L, "대학 이름 1", "전공 이름 1", "입학 연도 1", "졸업 연도 1", false, true, "학력 설명 1");

        final ProfileEducationResponseDTO.ProfileEducationItem secondProfileEducationItem
                = new ProfileEducationItem(2L, "대학 이름 2", "전공 이름 2", "입학 연도 2", "졸업 연도 2", false, true, "학력 설명 2");

        final List<ProfileEducationItem> profileEducationItems
                = Arrays.asList(firstProfileEducationItem, secondProfileEducationItem);

        final ProfileAwardsResponseDTO.ProfileAwardsItem firstProfileAwardsItem
                = new ProfileAwardsItem(1L, "수상 이름 1", "훈격 1", "수상 날짜 1", true, "수상 설명 1");

        final ProfileAwardsResponseDTO.ProfileAwardsItem secondProfileAwardsItem
                = new ProfileAwardsItem(2L, "수상 이름 2", "훈격 2", "수상 날짜 2", true, "수상 설명 2");

        final List<ProfileAwardsItem> profileAwardsItems
                = Arrays.asList(firstProfileAwardsItem, secondProfileAwardsItem);

        final ProfileLicenseResponseDTO.ProfileLicenseItem firstProfileLicenseItem
                = new ProfileLicenseItem(1L, "자격증 자격명 1", "자격증 관련 부처 1", "자격증 취득 시기 1", true, "자격증 설명 1");

        final ProfileLicenseResponseDTO.ProfileLicenseItem secondProfileLicenseItem
                = new ProfileLicenseItem(2L, "자격증 자격명 2", "자격증 관련 부처 2", "자격증 취득 시기 2", true, "자격증 설명 2");

        final List<ProfileLicenseItem> profileLicenseItems
                = Arrays.asList(firstProfileLicenseItem, secondProfileLicenseItem);

        final ProfileLinkResponseDTO.ProfileLinkItem firstProfileLinkItem
                = new ProfileLinkItem(1L, "링크 이름", "링크 경로");

        final ProfileLinkResponseDTO.ProfileLinkItem secondProfileLinkItem
                = new ProfileLinkItem(2L, "링크 이름", "링크 경로");

        final List<ProfileLinkItem> profileLinkItems
                = Arrays.asList(firstProfileLinkItem, secondProfileLinkItem);

        final ProfileResponseDTO.ProfileDetail profileDetail =
                new ProfileResponseDTO.ProfileDetail(
                        false,
                        profileCompletionMenu,
                        profileInformMenu,
                        profileScrapCount,
                        profileLogItem,
                        profileSkillItems,
                        profileActivityItems,
                        profilePortfolioItems,
                        profileEducationItems,
                        profileAwardsItems,
                        profileLicenseItems,
                        profileLinkItems
                );

        // when
        when(profileService.getLoggedOutProfileDetail(anyString())).thenReturn(profileDetail);

        final ResultActions resultActions = performGetProfileDetail("kwondm7");

        // then
        final MvcResult mvcResult = resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value("true"))
                .andExpect(jsonPath("$.code").value("1000"))
                .andExpect(jsonPath("$.message").value("요청에 성공하였습니다."))
                .andDo(
                        restDocs.document(
                                pathParameters(
                                        parameterWithName("emailId")
                                                .description("이메일 ID")
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
                                                .description("응답 결과 객체"),

                                        fieldWithPath("result.isMyProfile")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("내 프로필 여부"),

                                        fieldWithPath("result.profileCompletionMenu")
                                                .type(JsonFieldType.OBJECT)
                                                .description("프로필 완성도 정보"),
                                        fieldWithPath("result.profileCompletionMenu.profileCompletion")
                                                .type(JsonFieldType.NUMBER)
                                                .description("프로필 완성도 퍼센트"),

                                        fieldWithPath("result.profileInformMenu")
                                                .type(JsonFieldType.OBJECT)
                                                .description("프로필 정보 메뉴"),
                                        fieldWithPath("result.profileInformMenu.profileCurrentStates")
                                                .type(JsonFieldType.ARRAY)
                                                .description("현재 프로필 상태 목록"),
                                        fieldWithPath("result.profileInformMenu.profileCurrentStates[].profileStateName")
                                                .type(JsonFieldType.STRING)
                                                .description("프로필 상태 이름"),
                                        fieldWithPath("result.profileInformMenu.profileImagePath")
                                                .type(JsonFieldType.STRING)
                                                .description("프로필 이미지 경로"),
                                        fieldWithPath("result.profileInformMenu.memberName")
                                                .type(JsonFieldType.STRING)
                                                .description("회원 이름"),
                                        fieldWithPath("result.profileInformMenu.emailId")
                                                .type(JsonFieldType.STRING)
                                                .description("회원 이메일 아이디"),
                                        fieldWithPath("result.profileInformMenu.isProfilePublic")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("프로필 공개 여부"),
                                        fieldWithPath("result.profileInformMenu.majorPosition")
                                                .type(JsonFieldType.STRING)
                                                .description("대분류 포지션"),
                                        fieldWithPath("result.profileInformMenu.regionDetail")
                                                .type(JsonFieldType.OBJECT)
                                                .description("지역 상세 정보"),
                                        fieldWithPath("result.profileInformMenu.regionDetail.cityName")
                                                .type(JsonFieldType.STRING)
                                                .description("지역 시/도"),
                                        fieldWithPath("result.profileInformMenu.regionDetail.divisionName")
                                                .type(JsonFieldType.STRING)
                                                .description("지역 시/군/구"),

                                        // profileTeamInform
                                        fieldWithPath("result.profileInformMenu.profileTeamInforms[].teamName")
                                                .type(JsonFieldType.STRING)
                                                .description("프로필 회원이 속한 팀의 팀 이름"),
                                        fieldWithPath("result.profileInformMenu.profileTeamInforms[].teamLogoImagePath")
                                                .type(JsonFieldType.STRING)
                                                .description("프로필 회원이 속한 팀의 팀 로고 이미지 경로"),

                                        fieldWithPath("result.profileScrapCount")
                                                .type(JsonFieldType.NUMBER)
                                                .description("프로필 스크랩 수"),

                                        fieldWithPath("result.profileLogItem")
                                                .type(JsonFieldType.OBJECT)
                                                .description("프로필 로그 객체"),
                                        fieldWithPath("result.profileLogItem.profileLogId")
                                                .type(JsonFieldType.NUMBER)
                                                .description("프로필 로그 ID"),
                                        fieldWithPath("result.profileLogItem.isLogPublic")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("로그 공개 여부"),
                                        fieldWithPath("result.profileLogItem.logType")
                                                .type(JsonFieldType.STRING)
                                                .description("프로필 로그 타입"),
                                        fieldWithPath("result.profileLogItem.modifiedAt")
                                                .type(JsonFieldType.STRING)
                                                .description("로그 수정 시간"),
                                        fieldWithPath("result.profileLogItem.logTitle")
                                                .type(JsonFieldType.STRING)
                                                .description("로그 제목"),
                                        fieldWithPath("result.profileLogItem.logContent")
                                                .type(JsonFieldType.STRING)
                                                .description("로그 내용"),

                                        fieldWithPath("result.profileSkillItems")
                                                .type(JsonFieldType.ARRAY)
                                                .description("프로필 스킬 항목 배열"),
                                        fieldWithPath("result.profileSkillItems[].profileSkillId")
                                                .type(JsonFieldType.NUMBER)
                                                .description("프로필 스킬 ID"),
                                        fieldWithPath("result.profileSkillItems[].skillName")
                                                .type(JsonFieldType.STRING)
                                                .description("스킬 이름"),
                                        fieldWithPath("result.profileSkillItems[].skillLevel")
                                                .type(JsonFieldType.STRING)
                                                .description("스킬 수준"),

                                        fieldWithPath("result.profileActivityItems")
                                                .type(JsonFieldType.ARRAY)
                                                .description("프로필 활동 항목 배열"),
                                        fieldWithPath("result.profileActivityItems[].profileActivityId")
                                                .type(JsonFieldType.NUMBER)
                                                .description("프로필 활동 ID"),
                                        fieldWithPath("result.profileActivityItems[].activityName")
                                                .type(JsonFieldType.STRING)
                                                .description("활동 이름"),
                                        fieldWithPath("result.profileActivityItems[].activityRole")
                                                .type(JsonFieldType.STRING)
                                                .description("활동 역할"),
                                        fieldWithPath("result.profileActivityItems[].activityStartDate")
                                                .type(JsonFieldType.STRING)
                                                .description("활동 시작 날짜"),
                                        fieldWithPath("result.profileActivityItems[].activityEndDate")
                                                .type(JsonFieldType.STRING)
                                                .description("활동 종료 날짜"),
                                        fieldWithPath("result.profileActivityItems[].isActivityVerified")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("활동 인증 여부"),
                                        fieldWithPath("result.profileActivityItems[].activityDescription")
                                                .type(JsonFieldType.STRING)
                                                .description("이력 설명"),

                                        fieldWithPath("result.profilePortfolioItems")
                                                .type(JsonFieldType.ARRAY)
                                                .description("프로필 포트폴리오 항목 배열"),
                                        fieldWithPath("result.profilePortfolioItems[].profilePortfolioId")
                                                .type(JsonFieldType.NUMBER)
                                                .description("프로필 포트폴리오 ID"),
                                        fieldWithPath("result.profilePortfolioItems[].projectName")
                                                .type(JsonFieldType.STRING)
                                                .description("프로젝트 이름"),
                                        fieldWithPath("result.profilePortfolioItems[].projectLineDescription")
                                                .type(JsonFieldType.STRING)
                                                .description("프로젝트 한 줄 소개"),
                                        fieldWithPath("result.profilePortfolioItems[].projectSize")
                                                .type(JsonFieldType.STRING)
                                                .description("프로젝트 규모"),
                                        fieldWithPath("result.profilePortfolioItems[].projectStartDate")
                                                .type(JsonFieldType.STRING)
                                                .description("프로젝트 시작 날짜"),
                                        fieldWithPath("result.profilePortfolioItems[].projectEndDate")
                                                .type(JsonFieldType.STRING)
                                                .description("프로젝트 종료 날짜"),
                                        fieldWithPath("result.profilePortfolioItems[].isProjectInProgress")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("프로젝트 진행 중 여부"),
                                        fieldWithPath("result.profilePortfolioItems[].projectRoles")
                                                .type(JsonFieldType.ARRAY)
                                                .description("프로젝트 역할 목록"),
                                        fieldWithPath("result.profilePortfolioItems[].projectRoles[]")
                                                .type(JsonFieldType.ARRAY)
                                                .description("프로젝트 역할"),
                                        fieldWithPath("result.profilePortfolioItems[].projectRepresentImagePath")
                                                .type(JsonFieldType.STRING)
                                                .description("프로젝트 대표 이미지 경로"),

                                        fieldWithPath("result.profileEducationItems")
                                                .type(JsonFieldType.ARRAY)
                                                .description("프로필 학력 항목 배열"),
                                        fieldWithPath("result.profileEducationItems[].profileEducationId")
                                                .type(JsonFieldType.NUMBER)
                                                .description("프로필 학력 ID"),
                                        fieldWithPath("result.profileEducationItems[].universityName")
                                                .type(JsonFieldType.STRING)
                                                .description("대학교 이름"),
                                        fieldWithPath("result.profileEducationItems[].majorName")
                                                .type(JsonFieldType.STRING)
                                                .description("전공 이름"),
                                        fieldWithPath("result.profileEducationItems[].admissionYear")
                                                .type(JsonFieldType.STRING)
                                                .description("입학 연도"),
                                        fieldWithPath("result.profileEducationItems[].graduationYear")
                                                .type(JsonFieldType.STRING)
                                                .description("졸업 연도"),
                                        fieldWithPath("result.profileEducationItems[].isAttendUniversity")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("재학 여부"),
                                        fieldWithPath("result.profileEducationItems[].isEducationVerified")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("학력 인증 여부"),
                                        fieldWithPath("result.profileEducationItems[].educationDescription")
                                                .type(JsonFieldType.STRING)
                                                .description("학력 설명"),

                                        fieldWithPath("result.profileAwardsItems")
                                                .type(JsonFieldType.ARRAY)
                                                .description("프로필 수상 항목 배열"),
                                        fieldWithPath("result.profileAwardsItems[].profileAwardsId")
                                                .type(JsonFieldType.NUMBER)
                                                .description("프로필 수상 ID"),
                                        fieldWithPath("result.profileAwardsItems[].awardsName")
                                                .type(JsonFieldType.STRING)
                                                .description("수상 이름"),
                                        fieldWithPath("result.profileAwardsItems[].awardsRanking")
                                                .type(JsonFieldType.STRING)
                                                .description("수상 훈격"),
                                        fieldWithPath("result.profileAwardsItems[].awardsDate")
                                                .type(JsonFieldType.STRING)
                                                .description("수상 날짜"),
                                        fieldWithPath("result.profileAwardsItems[].isAwardsVerified")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("수상 인증 여부"),
                                        fieldWithPath("result.profileAwardsItems[].awardsDescription")
                                                .type(JsonFieldType.STRING)
                                                .description("수상 설명"),

                                        fieldWithPath("result.profileLicenseItems")
                                                .type(JsonFieldType.ARRAY)
                                                .description("프로필 자격증 항목 배열"),
                                        fieldWithPath("result.profileLicenseItems[].profileLicenseId")
                                                .type(JsonFieldType.NUMBER)
                                                .description("프로필 자격증 ID"),
                                        fieldWithPath("result.profileLicenseItems[].licenseName")
                                                .type(JsonFieldType.STRING)
                                                .description("자격증 이름"),
                                        fieldWithPath("result.profileLicenseItems[].licenseInstitution")
                                                .type(JsonFieldType.STRING)
                                                .description("자격증 기관"),
                                        fieldWithPath("result.profileLicenseItems[].licenseAcquisitionDate")
                                                .type(JsonFieldType.STRING)
                                                .description("자격증 취득 날짜"),
                                        fieldWithPath("result.profileLicenseItems[].isLicenseVerified")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("자격증 인증 여부"),
                                        fieldWithPath("result.profileLicenseItems[].licenseDescription")
                                                .type(JsonFieldType.STRING)
                                                .description("자격증 설명"),

                                        fieldWithPath("result.profileLinkItems")
                                                .type(JsonFieldType.ARRAY)
                                                .description("프로필 링크 항목 배열"),
                                        fieldWithPath("result.profileLinkItems[].profileLinkId")
                                                .type(JsonFieldType.NUMBER)
                                                .description("프로필 링크 ID"),
                                        fieldWithPath("result.profileLinkItems[].linkName")
                                                .type(JsonFieldType.STRING)
                                                .description("링크 이름"),
                                        fieldWithPath("result.profileLinkItems[].linkPath")
                                                .type(JsonFieldType.STRING)
                                                .description("링크 경로")
                                )
                        )
                ).andReturn();

        // JSON 응답에서 result 객체를 추출 및 검증
        final String jsonResponse = mvcResult.getResponse().getContentAsString();
        final CommonResponse<ProfileDetail> actual = objectMapper.readValue(
                jsonResponse,
                new TypeReference<CommonResponse<ProfileDetail>>() {
                }
        );

        final CommonResponse<ProfileDetail> expected = CommonResponse.onSuccess(profileDetail);

        // then
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }
}
