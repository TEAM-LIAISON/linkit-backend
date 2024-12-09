package liaison.linkit.team.presentation;

import static liaison.linkit.global.restdocs.RestDocsConfiguration.field;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestPartFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import liaison.linkit.common.presentation.CommonResponse;
import liaison.linkit.common.presentation.RegionResponseDTO.RegionDetail;
import liaison.linkit.global.ControllerTest;
import liaison.linkit.login.domain.MemberTokens;
import liaison.linkit.team.presentation.team.TeamController;
import liaison.linkit.team.presentation.team.dto.TeamRequestDTO.AddTeamRequest;
import liaison.linkit.team.presentation.team.dto.TeamRequestDTO.UpdateTeamRequest;
import liaison.linkit.team.presentation.team.dto.TeamResponseDTO;
import liaison.linkit.team.presentation.team.dto.TeamResponseDTO.AddTeamResponse;
import liaison.linkit.team.presentation.team.dto.TeamResponseDTO.TeamCurrentStateItem;
import liaison.linkit.team.presentation.team.dto.TeamResponseDTO.TeamDetail;
import liaison.linkit.team.presentation.team.dto.TeamResponseDTO.TeamInformMenu;
import liaison.linkit.team.presentation.team.dto.TeamResponseDTO.TeamScaleItem;
import liaison.linkit.team.presentation.team.dto.TeamResponseDTO.UpdateTeamResponse;
import liaison.linkit.team.service.TeamService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(TeamController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
public class TeamControllerTest extends ControllerTest {

    private static final MemberTokens MEMBER_TOKENS = new MemberTokens("refreshToken", "accessToken");
    private static final Cookie COOKIE = new Cookie("refreshToken", MEMBER_TOKENS.getRefreshToken());

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TeamService teamService;

    private ResultActions performGetTeamDetail(final String teamName) throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders.get("/api/v1/team/{teamName}", teamName)
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
        );
    }

    @DisplayName("회원이 팀을 신규 생성한다.")
    @Test
    void createTeam() throws Exception {
        // given
        final AddTeamRequest addTeamRequest = AddTeamRequest.builder()
                .teamName("리에종")
                .teamShortDescription("팀 한 줄 소개")
                .scaleName("팀 규모")
                .cityName("팀 활동지역 시/도")
                .divisionName("팀 활동지역 시/군/구")
                .teamStateNames(Arrays.asList("팀원 찾는 중", "투자 유치 중"))
                .isTeamPublic(true)
                .build();

        final MockMultipartFile teamLogoImage = new MockMultipartFile(
                "teamLogoImage",
                "teamLogo.png",
                "multipart/form-data",
                "./src/test/resources/static/images/logo.png".getBytes()
        );

        final MockMultipartFile createRequest = new MockMultipartFile(
                "addTeamRequest",
                null,
                "application/json",
                objectMapper.writeValueAsString(addTeamRequest).getBytes(StandardCharsets.UTF_8)
        );

        final TeamResponseDTO.AddTeamResponse addTeamResponse = AddTeamResponse.builder()
                .teamId(1L)
                .teamLogoImagePath("팀 로고 이미지 경로")
                .teamName("팀 이름")
                .teamShortDescription("팀 한 줄 소개")
                .teamScaleItem(
                        TeamScaleItem.builder()
                                .teamScaleName("팀 규모 이름")
                                .build()
                )
                .regionDetail(
                        RegionDetail.builder()
                                .cityName("서울특별시")
                                .divisionName("강남구")
                                .build()
                )
                .teamCurrentStates(
                        Arrays.asList(
                                TeamCurrentStateItem.builder()
                                        .teamStateName("팀원 찾는 중")
                                        .build(),
                                TeamCurrentStateItem.builder()
                                        .teamStateName("투자 유치 중")
                                        .build()
                        )
                )
                .isTeamPublic(true)
                .build();

        // when

        when(teamService.createTeam(any(), any(), any())).thenReturn(addTeamResponse);

        final ResultActions resultActions = mockMvc.perform(multipart(HttpMethod.POST, "/api/v1/team")
                .file(teamLogoImage)
                .file(createRequest)
                .accept(APPLICATION_JSON)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .characterEncoding("UTF-8")
                .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                .cookie(COOKIE));

        // then
        final MvcResult mvcResult = resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value("true"))
                .andExpect(jsonPath("$.code").value("1000"))
                .andExpect(jsonPath("$.message").value("요청에 성공하였습니다."))
                .andDo(
                        restDocs.document(
                                requestParts(
                                        partWithName("addTeamRequest").description("팀 생성 요청 객체"),
                                        partWithName("teamLogoImage")
                                                .description("팀 로고 이미지 파일. 지원되는 형식은 .png, .jpg 등이 있습니다.")
                                ),
                                requestPartFields("addTeamRequest",
                                        fieldWithPath("teamName")
                                                .type(JsonFieldType.STRING)
                                                .description("팀 이름")
                                                .attributes(field("constraint", "문자열")),
                                        fieldWithPath("teamShortDescription")
                                                .type(JsonFieldType.STRING)
                                                .description("팀 한 줄 소개")
                                                .attributes(field("constraint", "문자열")),
                                        fieldWithPath("scaleName")
                                                .type(JsonFieldType.STRING)
                                                .description("팀 규모")
                                                .attributes(field("constraint", "문자열")),
                                        fieldWithPath("cityName")
                                                .type(JsonFieldType.STRING)
                                                .description("활동 지역 시/도 이름")
                                                .attributes(field("constraint", "문자열")),
                                        fieldWithPath("divisionName")
                                                .type(JsonFieldType.STRING)
                                                .description("활동 지역 시/군/구 이름")
                                                .attributes(field("constraint", "문자열")),
                                        fieldWithPath("teamStateNames")
                                                .type(JsonFieldType.ARRAY)
                                                .description("현재 상태 배열"),
                                        fieldWithPath("isTeamPublic")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("팀 공개 여부")
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
                                        fieldWithPath("result.teamId")
                                                .type(JsonFieldType.NUMBER)
                                                .description("팀 ID"),

                                        // 추가된 필드들
                                        fieldWithPath("result.teamLogoImagePath")
                                                .type(JsonFieldType.STRING)
                                                .description("팀 로고 이미지 경로"),
                                        fieldWithPath("result.teamName")
                                                .type(JsonFieldType.STRING)
                                                .description("팀 이름"),
                                        fieldWithPath("result.teamShortDescription")
                                                .type(JsonFieldType.STRING)
                                                .description("팀 한 줄 소개"),
                                        fieldWithPath("result.teamScaleItem")
                                                .type(JsonFieldType.OBJECT)
                                                .description("팀 스케일 정보"),
                                        fieldWithPath("result.teamScaleItem.teamScaleName")
                                                .type(JsonFieldType.STRING)
                                                .description("팀 스케일 이름"),
                                        fieldWithPath("result.regionDetail")
                                                .type(JsonFieldType.OBJECT)
                                                .description("지역 상세 정보"),
                                        fieldWithPath("result.regionDetail.cityName")
                                                .type(JsonFieldType.STRING)
                                                .description("도시 이름"),
                                        fieldWithPath("result.regionDetail.divisionName")
                                                .type(JsonFieldType.STRING)
                                                .description("구/군 이름"),
                                        fieldWithPath("result.teamCurrentStates")
                                                .type(JsonFieldType.ARRAY)
                                                .description("팀 현재 상태 목록"),
                                        fieldWithPath("result.teamCurrentStates[].teamStateName")
                                                .type(JsonFieldType.STRING)
                                                .description("팀 상태 이름"),
                                        fieldWithPath("result.isTeamPublic")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("팀 공개 여부")
                                )
                        )).andReturn();

        // JSON 응답에서 result 객체를 추출 및 검증
        final String jsonResponse = mvcResult.getResponse().getContentAsString();
        final CommonResponse<AddTeamResponse> actual = objectMapper.readValue(
                jsonResponse,
                new TypeReference<CommonResponse<AddTeamResponse>>() {
                }
        );

        final CommonResponse<AddTeamResponse> expected = CommonResponse.onSuccess(addTeamResponse);

        // then
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @DisplayName("회원이 팀 기본 정보를 수정한다.")
    @Test
    void updateTeam() throws Exception {
        // given
        final UpdateTeamRequest updateTeamRequest = UpdateTeamRequest.builder()
                .teamName("리에종")
                .teamShortDescription("팀 한 줄 소개")
                .scaleName("팀 규모")
                .cityName("팀 활동지역 시/도")
                .divisionName("팀 활동지역 시/군/구")
                .teamStateNames(Arrays.asList("팀원 찾는 중", "투자 유치 중"))
                .isTeamPublic(true)
                .build();

        final MockMultipartFile teamLogoImage = new MockMultipartFile(
                "teamLogoImage",
                "teamLogo.png",
                "multipart/form-data",
                "./src/test/resources/static/images/logo.png".getBytes()
        );

        final MockMultipartFile updateRequest = new MockMultipartFile(
                "updateTeamRequest",
                null,
                "application/json",
                objectMapper.writeValueAsString(updateTeamRequest).getBytes(StandardCharsets.UTF_8)
        );

        final TeamResponseDTO.UpdateTeamResponse updateTeamResponse = UpdateTeamResponse.builder()
                .teamId(1L)
                .teamLogoImagePath("팀 로고 이미지 경로")
                .teamName("팀 이름")
                .teamShortDescription("팀 한 줄 소개")
                .teamScaleItem(
                        TeamScaleItem.builder()
                                .teamScaleName("팀 규모 이름")
                                .build()
                )
                .regionDetail(
                        RegionDetail.builder()
                                .cityName("서울특별시")
                                .divisionName("강남구")
                                .build()
                )
                .teamCurrentStates(
                        Arrays.asList(
                                TeamCurrentStateItem.builder()
                                        .teamStateName("팀원 찾는 중")
                                        .build(),
                                TeamCurrentStateItem.builder()
                                        .teamStateName("투자 유치 중")
                                        .build()
                        )
                )
                .isTeamPublic(true)
                .build();

        // when

        when(teamService.updateTeam(anyLong(), any(), any(), any())).thenReturn(updateTeamResponse);

        final ResultActions resultActions = mockMvc.perform(
                RestDocumentationRequestBuilders.multipart("/api/v1/team/{teamName}", "liaison")
                        .file(teamLogoImage)
                        .file(updateRequest)
                        .accept(APPLICATION_JSON)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .characterEncoding("UTF-8")
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE));

        // then
        final MvcResult mvcResult = resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value("true"))
                .andExpect(jsonPath("$.code").value("1000"))
                .andExpect(jsonPath("$.message").value("요청에 성공하였습니다."))
                .andDo(
                        restDocs.document(
                                pathParameters(
                                        parameterWithName("teamName")
                                                .description("팀 이름")
                                ),
                                requestParts(
                                        partWithName("updateTeamRequest").description("팀 기본 정보 수정 요청 객체"),
                                        partWithName("teamLogoImage")
                                                .description("팀 로고 이미지 파일. 지원되는 형식은 .png, .jpg 등이 있습니다.")
                                ),
                                requestPartFields("updateTeamRequest",
                                        fieldWithPath("teamName")
                                                .type(JsonFieldType.STRING)
                                                .description("팀 이름")
                                                .attributes(field("constraint", "문자열")),
                                        fieldWithPath("teamShortDescription")
                                                .type(JsonFieldType.STRING)
                                                .description("팀 한 줄 소개")
                                                .attributes(field("constraint", "문자열")),
                                        fieldWithPath("scaleName")
                                                .type(JsonFieldType.STRING)
                                                .description("팀 규모")
                                                .attributes(field("constraint", "문자열")),
                                        fieldWithPath("cityName")
                                                .type(JsonFieldType.STRING)
                                                .description("활동 지역 시/도 이름")
                                                .attributes(field("constraint", "문자열")),
                                        fieldWithPath("divisionName")
                                                .type(JsonFieldType.STRING)
                                                .description("활동 지역 시/군/구 이름")
                                                .attributes(field("constraint", "문자열")),
                                        fieldWithPath("teamStateNames")
                                                .type(JsonFieldType.ARRAY)
                                                .description("현재 상태 배열"),
                                        fieldWithPath("isTeamPublic")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("팀 공개 여부")
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
                                        fieldWithPath("result.teamId")
                                                .type(JsonFieldType.NUMBER)
                                                .description("팀 ID"),

                                        // 추가된 필드들
                                        fieldWithPath("result.teamLogoImagePath")
                                                .type(JsonFieldType.STRING)
                                                .description("팀 로고 이미지 경로"),
                                        fieldWithPath("result.teamName")
                                                .type(JsonFieldType.STRING)
                                                .description("팀 이름"),
                                        fieldWithPath("result.teamShortDescription")
                                                .type(JsonFieldType.STRING)
                                                .description("팀 한 줄 소개"),
                                        fieldWithPath("result.teamScaleItem")
                                                .type(JsonFieldType.OBJECT)
                                                .description("팀 스케일 정보"),
                                        fieldWithPath("result.teamScaleItem.teamScaleName")
                                                .type(JsonFieldType.STRING)
                                                .description("팀 스케일 이름"),
                                        fieldWithPath("result.regionDetail")
                                                .type(JsonFieldType.OBJECT)
                                                .description("지역 상세 정보"),
                                        fieldWithPath("result.regionDetail.cityName")
                                                .type(JsonFieldType.STRING)
                                                .description("도시 이름"),
                                        fieldWithPath("result.regionDetail.divisionName")
                                                .type(JsonFieldType.STRING)
                                                .description("구/군 이름"),
                                        fieldWithPath("result.teamCurrentStates")
                                                .type(JsonFieldType.ARRAY)
                                                .description("팀 현재 상태 목록"),
                                        fieldWithPath("result.teamCurrentStates[].teamStateName")
                                                .type(JsonFieldType.STRING)
                                                .description("팀 상태 이름"),
                                        fieldWithPath("result.isTeamPublic")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("팀 공개 여부")
                                )
                        )).andReturn();

        // JSON 응답에서 result 객체를 추출 및 검증
        final String jsonResponse = mvcResult.getResponse().getContentAsString();
        final CommonResponse<UpdateTeamResponse> actual = objectMapper.readValue(
                jsonResponse,
                new TypeReference<CommonResponse<UpdateTeamResponse>>() {
                }
        );

        final CommonResponse<UpdateTeamResponse> expected = CommonResponse.onSuccess(updateTeamResponse);

        // then
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @DisplayName("회원이 팀 상세 정보를 조회한다.")
    @Test
    void getTeamDetail() throws Exception {
        // given
        final TeamInformMenu teamInformMenu = TeamInformMenu.builder()
                .teamCurrentStates(Arrays.asList(
                                TeamCurrentStateItem.builder()
                                        .teamStateName("투자 유치 중")
                                        .build(),
                                TeamCurrentStateItem.builder()
                                        .teamStateName("공모전 준비 중")
                                        .build()
                        )
                )
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
                .build();

        final TeamDetail teamDetail = TeamDetail.builder()
                .isMyTeam(true)
                .teamInformMenu(teamInformMenu)
                .build();

        // when
        when(teamService.getLoggedOutTeamDetail(any())).thenReturn(teamDetail);

        final ResultActions resultActions = performGetTeamDetail("liaison");

        // then
        final MvcResult mvcResult = resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value("true"))
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
                                        fieldWithPath("result.isMyTeam")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("로그인 사용자가 팀의 멤버인지 여부"),
                                        fieldWithPath("result.teamInformMenu")
                                                .type(JsonFieldType.OBJECT)
                                                .description("팀 정보 메뉴 객체"),
                                        fieldWithPath("result.teamInformMenu.teamCurrentStates")
                                                .type(JsonFieldType.ARRAY)
                                                .description("팀 현재 상태 목록"),
                                        fieldWithPath("result.teamInformMenu.teamCurrentStates[].teamStateName")
                                                .type(JsonFieldType.STRING)
                                                .description("팀 상태 이름"),
                                        fieldWithPath("result.teamInformMenu.teamName")
                                                .type(JsonFieldType.STRING)
                                                .description("팀 이름"),
                                        fieldWithPath("result.teamInformMenu.teamShortDescription")
                                                .type(JsonFieldType.STRING)
                                                .description("팀 한 줄 소개"),
                                        fieldWithPath("result.teamInformMenu.teamLogoImagePath")
                                                .type(JsonFieldType.STRING)
                                                .description("팀 로고 이미지 경로"),
                                        fieldWithPath("result.teamInformMenu.teamScaleItem")
                                                .type(JsonFieldType.OBJECT)
                                                .description("팀 규모 정보"),
                                        fieldWithPath("result.teamInformMenu.teamScaleItem.teamScaleName")
                                                .type(JsonFieldType.STRING)
                                                .description("팀 스케일 이름"),
                                        fieldWithPath("result.teamInformMenu.regionDetail")
                                                .type(JsonFieldType.OBJECT)
                                                .description("지역 상세 정보"),
                                        fieldWithPath("result.teamInformMenu.regionDetail.cityName")
                                                .type(JsonFieldType.STRING)
                                                .description("지역 시/도 이름"),
                                        fieldWithPath("result.teamInformMenu.regionDetail.divisionName")
                                                .type(JsonFieldType.STRING)
                                                .description("시/군/구 이름")
                                )
                        )).andReturn();

        // JSON 응답에서 result 객체를 추출 및 검증
        final String jsonResponse = mvcResult.getResponse().getContentAsString();
        final CommonResponse<TeamDetail> actual = objectMapper.readValue(
                jsonResponse,
                new TypeReference<CommonResponse<TeamDetail>>() {
                }
        );

        final CommonResponse<TeamDetail> expected = CommonResponse.onSuccess(teamDetail);

        // then
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);

    }
}
