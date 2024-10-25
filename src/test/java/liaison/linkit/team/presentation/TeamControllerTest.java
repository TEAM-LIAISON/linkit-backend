package liaison.linkit.team.presentation;

import static liaison.linkit.global.restdocs.RestDocsConfiguration.field;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestPartFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import liaison.linkit.global.ControllerTest;
import liaison.linkit.login.domain.MemberTokens;
import liaison.linkit.team.presentation.team.TeamController;
import liaison.linkit.team.presentation.team.dto.TeamRequestDTO.SaveTeamBasicInformRequest;
import liaison.linkit.team.presentation.team.dto.TeamResponseDTO;
import liaison.linkit.team.presentation.team.dto.TeamResponseDTO.AddTeamResponse;
import liaison.linkit.team.service.TeamService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(TeamController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
public class TeamControllerTest extends ControllerTest {

    private static final MemberTokens MEMBER_TOKENS = new MemberTokens("refreshToken", "accessToken");
    private static final Cookie COOKIE = new Cookie("refresh-token", MEMBER_TOKENS.getRefreshToken());

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TeamService teamService;

    @DisplayName("회원이 1개의 팀을 생성한다.")
    @Test
    void createTeam() throws Exception {
        // given
        final SaveTeamBasicInformRequest saveTeamBasicInformRequest = new SaveTeamBasicInformRequest(
                "리에종",
                "팀 한 줄 소개입니다.",
                "1인",
                "서울특별시",
                "강남구"
        );

        final MockMultipartFile teamLogoImage = new MockMultipartFile(
                "teamLogoImage",
                "teamLogo.png",
                "multipart/form-data",
                "./src/test/resources/static/images/logo.png".getBytes()
        );

        final MockMultipartFile createRequest = new MockMultipartFile(
                "saveTeamBasicInformRequest",
                null,
                "application/json",
                objectMapper.writeValueAsString(saveTeamBasicInformRequest).getBytes(StandardCharsets.UTF_8)
        );

        final TeamResponseDTO.AddTeamResponse addTeamResponse
                = new AddTeamResponse(1L, LocalDateTime.now());

        // when

        Mockito.when(teamService.createTeam(any(), any(), any())).thenReturn(addTeamResponse);

        final ResultActions resultActions = mockMvc.perform(multipart(HttpMethod.POST, "/api/v1/team")
                .file(teamLogoImage)
                .file(createRequest)
                .accept(APPLICATION_JSON)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .characterEncoding("UTF-8")
                .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                .cookie(COOKIE));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value("true"))
                .andExpect(jsonPath("$.code").value("1000"))
                .andExpect(jsonPath("$.message").value("요청에 성공하였습니다."))
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
                                requestParts(
                                        partWithName("saveTeamBasicInformRequest").description("팀 기본 정보 생성 요청 객체"),
                                        partWithName("teamLogoImage")
                                                .description("팀 로고 이미지 파일. 지원되는 형식은 .png, .jpg 등이 있습니다.")
                                ),
                                requestPartFields("saveTeamBasicInformRequest",
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
                                                .attributes(field("constraint", "문자열"))
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
                                        fieldWithPath("result.createdAt")
                                                .type(JsonFieldType.STRING)
                                                .description("팀 생성 시간")
                                                .attributes(field("constraint", "문자열"))
                                )
                        )).andReturn();
    }
}
