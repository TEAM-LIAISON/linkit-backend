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
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
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
import liaison.linkit.scrap.business.service.TeamScrapService;
import liaison.linkit.scrap.presentation.dto.teamScrap.TeamScrapRequestDTO.UpdateTeamScrapRequest;
import liaison.linkit.scrap.presentation.dto.teamScrap.TeamScrapResponseDTO;
import liaison.linkit.scrap.presentation.dto.teamScrap.TeamScrapResponseDTO.UpdateTeamScrap;
import liaison.linkit.team.presentation.team.dto.TeamResponseDTO;
import liaison.linkit.team.presentation.team.dto.TeamResponseDTO.TeamCurrentStateItem;
import liaison.linkit.team.presentation.team.dto.TeamResponseDTO.TeamInformMenu;
import liaison.linkit.team.presentation.team.dto.TeamResponseDTO.TeamInformMenus;
import liaison.linkit.team.presentation.team.dto.TeamResponseDTO.TeamScaleItem;
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

@WebMvcTest(TeamScrapController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
public class TeamScrapControllerTest extends ControllerTest {
    private static final MemberTokens MEMBER_TOKENS = new MemberTokens("refreshToken", "accessToken");
    private static final Cookie COOKIE = new Cookie("refreshToken", MEMBER_TOKENS.getRefreshToken());

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TeamScrapService teamScrapService;

    private ResultActions performUpdateTeamScrap(final String teamCode, final UpdateTeamScrapRequest request) throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders.post("/api/v1/team/scrap/{teamCode}", teamCode)
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(objectMapper.writeValueAsString(request))
        );
    }

    private ResultActions performGetTeamScraps() throws Exception {
        return mockMvc.perform(
                get("/api/v1/team/scrap")
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
        );
    }

    @DisplayName("회원이 다른 팀을 스크랩/스크랩취소를 진행한다.")
    @Test
    void updateTeamScrap() throws Exception {
        // given
        final UpdateTeamScrapRequest updateTeamScrapRequest = UpdateTeamScrapRequest.builder()
                .changeScrapValue(true)
                .build();

        final TeamScrapResponseDTO.UpdateTeamScrap updateTeamScrap = UpdateTeamScrap.builder()
                .teamCode("팀 아이디 (팀 코드)")
                .isTeamScrap(true)
                .build();

        // when
        when(teamScrapService.updateTeamScrap(anyLong(), any(), any())).thenReturn(updateTeamScrap);

        final ResultActions resultActions = performUpdateTeamScrap("liaison", updateTeamScrapRequest);

        // then
        final MvcResult mvcResult = resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value("true"))
                .andExpect(jsonPath("$.code").value("1000"))
                .andExpect(jsonPath("$.message").value("요청에 성공하였습니다."))
                .andDo(
                        restDocs.document(
                                pathParameters(
                                        parameterWithName("teamCode")
                                                .description("팀 아이디 (팀 코드)")
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
                                        
                                        fieldWithPath("result.teamCode")
                                                .type(JsonFieldType.STRING)
                                                .description("팀 아이디 (팀 코드)"),
                                        fieldWithPath("result.isTeamScrap")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("변경된 팀 스크랩 여부 (true -> 스크랩한 상태 / false -> 스크랩하지 않은 상태)")
                                )
                        )).andReturn();

        final String jsonResponse = mvcResult.getResponse().getContentAsString();
        final CommonResponse<UpdateTeamScrap> actual = objectMapper.readValue(
                jsonResponse,
                new TypeReference<CommonResponse<UpdateTeamScrap>>() {
                }
        );

        final CommonResponse<UpdateTeamScrap> expected = CommonResponse.onSuccess(updateTeamScrap);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @DisplayName("회원이 내가 스크랩한 팀 전체를 조회한다.")
    @Test
    void getTeamScraps() throws Exception {
        // given
        final TeamResponseDTO.TeamInformMenus teamInformMenus = TeamInformMenus.builder()
                .teamInformMenus(Arrays.asList(
                        TeamInformMenu.builder()
                                .teamCurrentStates(Arrays.asList(
                                                TeamCurrentStateItem.builder()
                                                        .teamStateName("투자 유치 중")
                                                        .build(),
                                                TeamCurrentStateItem.builder()
                                                        .teamStateName("공모전 준비 중")
                                                        .build()
                                        )
                                )
                                .isTeamScrap(false)
                                .teamScrapCount(200)
                                .teamName("팀 이름")
                                .teamShortDescription("팀 한 줄 소개")
                                .teamLogoImagePath("팀 로고 이미지 경로")
                                .teamScaleItem(
                                        TeamScaleItem.builder()
                                                .teamScaleName("1인")
                                                .build()
                                )
                                .regionDetail(
                                        RegionDetail.builder()
                                                .cityName("서울특별시")
                                                .divisionName("강남구")
                                                .build()
                                )
                                .build(),
                        TeamInformMenu.builder()
                                .teamCurrentStates(Arrays.asList(
                                                TeamCurrentStateItem.builder()
                                                        .teamStateName("투자 유치 중")
                                                        .build(),
                                                TeamCurrentStateItem.builder()
                                                        .teamStateName("공모전 준비 중")
                                                        .build()
                                        )
                                )
                                .isTeamScrap(false)
                                .teamScrapCount(200)
                                .teamName("팀 이름")
                                .teamShortDescription("팀 한 줄 소개")
                                .teamLogoImagePath("팀 로고 이미지 경로")
                                .teamScaleItem(
                                        TeamScaleItem.builder()
                                                .teamScaleName("1인")
                                                .build()
                                )
                                .regionDetail(
                                        RegionDetail.builder()
                                                .cityName("서울특별시")
                                                .divisionName("강남구")
                                                .build()
                                )
                                .build()
                ))
                .build();

        // when
        when(teamScrapService.getTeamScraps(anyLong())).thenReturn(teamInformMenus);

        final ResultActions resultActions = performGetTeamScraps();

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

                                        // result
                                        subsectionWithPath("result.teamInformMenus")
                                                .type(JsonFieldType.ARRAY)
                                                .description("팀 정보 목록"),

                                        // teamInformMenus[].teamCurrentStates
                                        fieldWithPath("result.teamInformMenus[].teamCurrentStates")
                                                .type(JsonFieldType.ARRAY)
                                                .description("팀 상태(현황) 목록"),
                                        fieldWithPath("result.teamInformMenus[].teamCurrentStates[].teamStateName")
                                                .type(JsonFieldType.STRING)
                                                .description("팀 상태(현황) 이름"),

                                        // isTeamScrap
                                        fieldWithPath("result.teamInformMenus[].isTeamScrap")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("팀 스크랩 여부"),

                                        // teamScrapCount
                                        fieldWithPath("result.teamInformMenus[].teamScrapCount")
                                                .type(JsonFieldType.NUMBER)
                                                .description("팀 스크랩 총 개수"),

                                        // teamName
                                        fieldWithPath("result.teamInformMenus[].teamName")
                                                .type(JsonFieldType.STRING)
                                                .description("팀 이름"),

                                        // teamShortDescription
                                        fieldWithPath("result.teamInformMenus[].teamShortDescription")
                                                .type(JsonFieldType.STRING)
                                                .description("팀 한 줄 소개"),

                                        // teamLogoImagePath
                                        fieldWithPath("result.teamInformMenus[].teamLogoImagePath")
                                                .type(JsonFieldType.STRING)
                                                .description("팀 로고 이미지 경로"),

                                        // teamScaleItem
                                        fieldWithPath("result.teamInformMenus[].teamScaleItem")
                                                .type(JsonFieldType.OBJECT)
                                                .description("팀 규모 정보"),
                                        fieldWithPath("result.teamInformMenus[].teamScaleItem.teamScaleName")
                                                .type(JsonFieldType.STRING)
                                                .description("팀 규모 이름"),

                                        // regionDetail
                                        fieldWithPath("result.teamInformMenus[].regionDetail")
                                                .type(JsonFieldType.OBJECT)
                                                .description("팀 지역 정보"),
                                        fieldWithPath("result.teamInformMenus[].regionDetail.cityName")
                                                .type(JsonFieldType.STRING)
                                                .description("팀 활동지역 (시/도)"),
                                        fieldWithPath("result.teamInformMenus[].regionDetail.divisionName")
                                                .type(JsonFieldType.STRING)
                                                .description("팀 활동지역 (시/군/구)")
                                )
                        )
                )
                .andReturn();

        final String jsonResponse = mvcResult.getResponse().getContentAsString();
        final CommonResponse<TeamInformMenus> actual = objectMapper.readValue(
                jsonResponse,
                new TypeReference<CommonResponse<TeamInformMenus>>() {
                }
        );

        final CommonResponse<TeamInformMenus> expected = CommonResponse.onSuccess(teamInformMenus);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }
}
