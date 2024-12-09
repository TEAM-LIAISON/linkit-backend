package liaison.linkit.team.presentation;

import static liaison.linkit.global.restdocs.RestDocsConfiguration.field;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
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
import liaison.linkit.team.presentation.teamMember.TeamMemberController;
import liaison.linkit.team.presentation.teamMember.dto.TeamMemberResponseDTO;
import liaison.linkit.team.presentation.teamMember.dto.TeamMemberResponseDTO.TeamMemberItems;
import liaison.linkit.team.service.teamMember.TeamMemberService;
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

@WebMvcTest(TeamMemberController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
public class TeamMemberControllerTest extends ControllerTest {
    private static final MemberTokens MEMBER_TOKENS = new MemberTokens("accessToken", "refreshToken");
    private static final Cookie COOKIE = new Cookie("refreshToken", MEMBER_TOKENS.getRefreshToken());

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TeamMemberService teamMemberService;

    @BeforeEach
    void setUp() {
        given(refreshTokenRepository.existsById(any())).willReturn(true);
        doNothing().when(jwtProvider).validateTokens(any());
        given(jwtProvider.getSubject(any())).willReturn("1");
    }

    private ResultActions performGetTeamMemberItems(final String teamName) throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders.get("/api/v1/team/{teamName}/members", teamName)
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
        );
    }


    @DisplayName("회원이 팀의 팀 구성원을 전체 조회할 수 있다.")
    @Test
    void getTeamMemberItems() throws Exception {
        // given
        final TeamMemberItems teamMemberItems = TeamMemberItems
                .builder()
                .profileInformMenus(
                        Arrays.asList(
                                TeamMemberResponseDTO.ProfileInformMenu
                                        .builder()
                                        .profileCurrentStates(
                                                Arrays.asList(
                                                        ProfileCurrentStateItem
                                                                .builder()
                                                                .profileStateName("팀원 찾는 중")
                                                                .build(),
                                                        ProfileCurrentStateItem
                                                                .builder()
                                                                .profileStateName("대회 준비 중")
                                                                .build()
                                                )
                                        )
                                        .profileImagePath("프로필 이미지 경로")
                                        .memberName("회원 이름")
                                        .isProfilePublic(true)
                                        .majorPosition("포지션 대분류")
                                        .regionDetail(
                                                RegionDetail.builder()
                                                        .cityName("활동지역 시/도")
                                                        .divisionName("활동지역 시/군/구")
                                                        .build()
                                        )
                                        .build(),
                                TeamMemberResponseDTO.ProfileInformMenu
                                        .builder()
                                        .profileCurrentStates(
                                                Arrays.asList(
                                                        ProfileCurrentStateItem
                                                                .builder()
                                                                .profileStateName("팀원 찾는 중")
                                                                .build(),
                                                        ProfileCurrentStateItem
                                                                .builder()
                                                                .profileStateName("대회 준비 중")
                                                                .build()
                                                )
                                        )
                                        .profileImagePath("프로필 이미지 경로 2")
                                        .memberName("회원 이름 2")
                                        .isProfilePublic(true)
                                        .majorPosition("포지션 대분류")
                                        .regionDetail(
                                                RegionDetail.builder()
                                                        .cityName("활동지역 시/도")
                                                        .divisionName("활동지역 시/군/구")
                                                        .build()
                                        )
                                        .build()
                        )
                ).build();

        // when
        when(teamMemberService.getTeamMemberItems(anyLong(), any())).thenReturn(teamMemberItems);

        final ResultActions resultActions = performGetTeamMemberItems("liaison");

        // then
        final MvcResult mvcResult = resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true)) // BOOLEAN 값으로 수정
                .andExpect(jsonPath("$.code").value("1000"))
                .andExpect(jsonPath("$.message").value("요청에 성공하였습니다."))
                .andDo(
                        restDocs.document(
                                pathParameters(
                                        parameterWithName("teamName")
                                                .description("팀 이름")
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
                                        fieldWithPath("result").type(JsonFieldType.OBJECT).description("결과 데이터"),
                                        fieldWithPath("result.profileInformMenus").type(JsonFieldType.ARRAY).description("프로필 정보 목록"),
                                        fieldWithPath("result.profileInformMenus[].profileCurrentStates").type(JsonFieldType.ARRAY).description("프로필 상태 목록"),
                                        fieldWithPath("result.profileInformMenus[].profileCurrentStates[].profileStateName").type(JsonFieldType.STRING).description("프로필 상태 이름"),
                                        fieldWithPath("result.profileInformMenus[].profileImagePath").type(JsonFieldType.STRING).description("프로필 이미지 경로"),
                                        fieldWithPath("result.profileInformMenus[].memberName").type(JsonFieldType.STRING).description("회원 이름"),
                                        fieldWithPath("result.profileInformMenus[].isProfilePublic").type(JsonFieldType.BOOLEAN).description("프로필 공개 여부"),
                                        fieldWithPath("result.profileInformMenus[].majorPosition").type(JsonFieldType.STRING).description("포지션 대분류"),
                                        fieldWithPath("result.profileInformMenus[].regionDetail").type(JsonFieldType.OBJECT).description("지역 상세 정보"),
                                        fieldWithPath("result.profileInformMenus[].regionDetail.cityName").type(JsonFieldType.STRING).description("활동지역 시/도"),
                                        fieldWithPath("result.profileInformMenus[].regionDetail.divisionName").type(JsonFieldType.STRING).description("활동지역 시/군/구")
                                )
                        )).andReturn();

        final String jsonResponse = mvcResult.getResponse().getContentAsString();
        final CommonResponse<TeamMemberItems> actual = objectMapper.readValue(
                jsonResponse,
                new TypeReference<CommonResponse<TeamMemberItems>>() {
                }
        );

        final CommonResponse<TeamMemberItems> expected = CommonResponse.onSuccess(teamMemberItems);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }
}
