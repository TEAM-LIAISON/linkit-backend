package liaison.linkit.profile.presentation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import groovy.util.logging.Slf4j;
import jakarta.servlet.http.Cookie;
import java.util.Arrays;
import java.util.List;
import liaison.linkit.common.presentation.CommonResponse;
import liaison.linkit.common.presentation.RegionResponseDTO.RegionDetail;
import liaison.linkit.global.ControllerTest;
import liaison.linkit.login.domain.MemberTokens;
import liaison.linkit.profile.presentation.miniProfile.dto.MiniProfileResponseDTO.ProfileCurrentStateItem;
import liaison.linkit.profile.presentation.profile.ProfileController;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO.ProfileBooleanMenu;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO.ProfileCompletionMenu;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO.ProfileInformMenu;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO.ProfileLeftMenu;
import liaison.linkit.profile.service.ProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
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

    @DisplayName("회원이 나의 프로필 왼쪽 메뉴를 조회할 수 있다.")
    @Test
    void getProfileLeftMenu() throws Exception {

        // given
        final ProfileCompletionMenu profileCompletionMenu = new ProfileCompletionMenu(100);

        final ProfileCurrentStateItem firstProfileCurrentStateItem = new ProfileCurrentStateItem("팀 찾는 중");
        final ProfileCurrentStateItem secondProfileCurrentStateItem = new ProfileCurrentStateItem("공모전 준비 중");

        final List<ProfileCurrentStateItem> profileCurrentStates = Arrays.asList(firstProfileCurrentStateItem, secondProfileCurrentStateItem);

        final RegionDetail regionDetail = new RegionDetail("서울특별시", "강남구");

        final ProfileInformMenu profileInformMenu =
                new ProfileInformMenu(
                        profileCurrentStates,
                        "프로필 이미지 경로",
                        "권동민",
                        true,
                        "포지션 대분류",
                        regionDetail
                );

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
}
