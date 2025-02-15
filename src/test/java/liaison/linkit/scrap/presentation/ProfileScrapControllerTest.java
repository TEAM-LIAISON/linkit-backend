package liaison.linkit.scrap.presentation;

import static liaison.linkit.global.restdocs.RestDocsConfiguration.field;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import java.util.Arrays;
import liaison.linkit.common.presentation.CommonResponse;
import liaison.linkit.common.presentation.RegionResponseDTO.RegionDetail;
import liaison.linkit.global.ControllerTest;
import liaison.linkit.login.domain.MemberTokens;
import liaison.linkit.profile.presentation.miniProfile.dto.MiniProfileResponseDTO.ProfileCurrentStateItem;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO.ProfileInformMenu;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO.ProfileInformMenus;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO.ProfileTeamInform;
import liaison.linkit.scrap.business.service.ProfileScrapService;
import liaison.linkit.scrap.presentation.dto.profileScrap.ProfileScrapRequestDTO.UpdateProfileScrapRequest;
import liaison.linkit.scrap.presentation.dto.profileScrap.ProfileScrapResponseDTO;
import liaison.linkit.scrap.presentation.dto.profileScrap.ProfileScrapResponseDTO.UpdateProfileScrap;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(ProfileScrapController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
public class ProfileScrapControllerTest extends ControllerTest {

    private static final MemberTokens MEMBER_TOKENS = new MemberTokens("refreshToken", "accessToken");
    private static final Cookie COOKIE = new Cookie("refreshToken", MEMBER_TOKENS.getRefreshToken());

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProfileScrapService profileScrapService;

    private ResultActions performUpdateProfileScrap(final String emailId, final UpdateProfileScrapRequest request) throws Exception {
        return mockMvc.perform(
            RestDocumentationRequestBuilders.post("/api/v1/profile/scrap/{emailId}", emailId)
                .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                .cookie(COOKIE)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(objectMapper.writeValueAsString(request))
        );
    }

    private ResultActions performGetProfileScraps() throws Exception {
        return mockMvc.perform(
            get("/api/v1/profile/scrap")
                .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                .cookie(COOKIE)
        );
    }

    @DisplayName("회원이 다른 회원의 프로필을 스크랩/스크랩취소를 진행한다.")
    @Test
    void updateProfileScrap() throws Exception {
        // given
        final UpdateProfileScrapRequest updateProfileScrapRequest = UpdateProfileScrapRequest.builder()
            .changeScrapValue(true)
            .build();

        final ProfileScrapResponseDTO.UpdateProfileScrap updateProfileScrap = UpdateProfileScrap.builder()
            .emailId("이메일 아이디")
            .isProfileScrap(true)
            .build();

        // when
        when(profileScrapService.updateProfileScrap(anyLong(), any(), any())).thenReturn(updateProfileScrap);

        final ResultActions resultActions = performUpdateProfileScrap("liaison", updateProfileScrapRequest);

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
                            .description("이메일 아이디")
                    ),
                    requestFields(
                        fieldWithPath("changeScrapValue")
                            .type(JsonFieldType.BOOLEAN)
                            .description("변경하고자 하는 스크랩 상태 (스크랩하기 -> true, 스크랩취소 -> false)")
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

                        fieldWithPath("result.emailId")
                            .type(JsonFieldType.STRING)
                            .description("이메일 ID"),
                        fieldWithPath("result.isProfileScrap")
                            .type(JsonFieldType.BOOLEAN)
                            .description("변경된 프로필 스크랩 여부 (true -> 스크랩한 상태 / false -> 스크랩하지 않은 상태)")
                    )
                )).andReturn();

        final String jsonResponse = mvcResult.getResponse().getContentAsString();
        final CommonResponse<UpdateProfileScrap> actual = objectMapper.readValue(
            jsonResponse,
            new TypeReference<CommonResponse<UpdateProfileScrap>>() {
            }
        );

        final CommonResponse<UpdateProfileScrap> expected = CommonResponse.onSuccess(updateProfileScrap);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @DisplayName("회원이 자신이 스크랩한 프로필을 모두 조회할 수 있다.")
    @Test
    void getProfileScraps() throws Exception {
        // given
        final ProfileResponseDTO.ProfileInformMenus profileInformMenus = ProfileInformMenus.builder()
            .profileInformMenus(
                Arrays.asList(
                    ProfileInformMenu.builder()
                        .profileCurrentStates(
                            Arrays.asList(
                                ProfileCurrentStateItem.builder()
                                    .profileStateName("팀원 찾는 중")
                                    .build(),
                                ProfileCurrentStateItem.builder()
                                    .profileStateName("팀 찾는 중")
                                    .build()
                            )
                        )
                        .isProfileScrap(true)
                        .profileScrapCount(100)
                        .profileImagePath("프로필 이미지 경로")
                        .memberName("회원 이름")
                        .emailId("이메일 ID")
                        .isProfilePublic(true)
                        .majorPosition("포지션 대분류")
                        .subPosition("포지션 소분류")
                        .regionDetail(
                            RegionDetail.builder()
                                .cityName("활동지역 시/도")
                                .divisionName("활동지역 시/군/구")
                                .build()
                        )
                        .profileTeamInforms(
                            Arrays.asList(
                                ProfileTeamInform.builder()
                                    .teamName("소속 팀 이름 1")
                                    .teamCode("소속 팀 아이디 1")
                                    .teamLogoImagePath("소속 팀 로고 이미지 1")
                                    .build(),
                                ProfileTeamInform.builder()
                                    .teamName("소속 팀 이름 2")
                                    .teamCode("소속 팀 아이디 2")
                                    .teamLogoImagePath("소속 팀 로고 이미지 2")
                                    .build()
                            )
                        )
                        .build(),
                    ProfileInformMenu.builder()
                        .profileCurrentStates(
                            Arrays.asList(
                                ProfileCurrentStateItem.builder()
                                    .profileStateName("팀원 찾는 중")
                                    .build(),
                                ProfileCurrentStateItem.builder()
                                    .profileStateName("팀 찾는 중")
                                    .build()
                            )
                        )
                        .isProfileScrap(true)
                        .profileScrapCount(200)
                        .profileImagePath("프로필 이미지 경로 2")
                        .memberName("회원 이름 2")
                        .emailId("이메일 ID 2")
                        .isProfilePublic(true)
                        .majorPosition("포지션 대분류")
                        .subPosition("포지션 소분류")
                        .regionDetail(
                            RegionDetail.builder()
                                .cityName("활동지역 시/도")
                                .divisionName("활동지역 시/군/구")
                                .build()
                        )
                        .profileTeamInforms(
                            Arrays.asList(
                                ProfileTeamInform.builder()
                                    .teamName("소속 팀 이름 1")
                                    .teamCode("소속 팀 아이디 1")
                                    .teamLogoImagePath("소속 팀 로고 이미지 1")
                                    .build(),
                                ProfileTeamInform.builder()
                                    .teamName("소속 팀 이름 2")
                                    .teamCode("소속 팀 아이디 2")
                                    .teamLogoImagePath("소속 팀 로고 이미지 2")
                                    .build()
                            )
                        )
                        .build()
                )
            )
            .build();

        // when
        when(profileScrapService.getProfileScraps(anyLong())).thenReturn(profileInformMenus);

        final ResultActions resultActions = performGetProfileScraps();

        // then
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
                        fieldWithPath("result.profileInformMenus[].profileCurrentStates[].profileStateName")
                            .type(JsonFieldType.STRING)
                            .description("프로필 상태 이름"),
                        fieldWithPath("result.profileInformMenus[].isProfileScrap")
                            .type(JsonFieldType.BOOLEAN)
                            .description("프로필 스크랩 여부 (자기 자신이 스크랩한 목록 조회는 무조건 true)"),
                        fieldWithPath("result.profileInformMenus[].profileScrapCount")
                            .type(JsonFieldType.NUMBER)
                            .description("프로필 스크랩 전체 개수"),
                        fieldWithPath("result.profileInformMenus[].profileImagePath")
                            .type(JsonFieldType.STRING)
                            .description("프로필 이미지 경로"),
                        fieldWithPath("result.profileInformMenus[].memberName")
                            .type(JsonFieldType.STRING)
                            .description("회원 이름"),
                        fieldWithPath("result.profileInformMenus[].emailId")
                            .type(JsonFieldType.STRING)
                            .description("이메일 ID"),
                        fieldWithPath("result.profileInformMenus[].isProfilePublic")
                            .type(JsonFieldType.BOOLEAN)
                            .description("프로필 공개 여부"),
                        fieldWithPath("result.profileInformMenus[].majorPosition")
                            .type(JsonFieldType.STRING)
                            .description("포지션 대분류"),
                        fieldWithPath("result.profileInformMenus[].subPosition")
                            .type(JsonFieldType.STRING)
                            .description("포지션 소분류"),
                        fieldWithPath("result.profileInformMenus[].regionDetail.cityName")
                            .type(JsonFieldType.STRING)
                            .description("활동지역 시/도"),
                        fieldWithPath("result.profileInformMenus[].regionDetail.divisionName")
                            .type(JsonFieldType.STRING)
                            .description("활동지역 시/군/구"),
                        fieldWithPath("result.profileInformMenus[].profileTeamInforms[].teamName")
                            .type(JsonFieldType.STRING)
                            .description("소속 팀 이름"),
                        fieldWithPath("result.profileInformMenus[].profileTeamInforms[].teamCode")
                            .type(JsonFieldType.STRING)
                            .description("소속 팀 아이디 (팀 코드)"),
                        fieldWithPath("result.profileInformMenus[].profileTeamInforms[].teamLogoImagePath")
                            .type(JsonFieldType.STRING)
                            .description("소속 팀 로고 이미지 경로")
                    )
                )).andReturn();

        final String jsonResponse = mvcResult.getResponse().getContentAsString();
        final CommonResponse<ProfileInformMenus> actual = objectMapper.readValue(
            jsonResponse,
            new TypeReference<CommonResponse<ProfileInformMenus>>() {
            }
        );

        final CommonResponse<ProfileInformMenus> expected = CommonResponse.onSuccess(profileInformMenus);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }
}
