package liaison.linkit.team.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import liaison.linkit.global.ControllerTest;
import liaison.linkit.login.domain.MemberTokens;
import liaison.linkit.team.dto.request.onBoarding.OnBoardingFieldTeamInformRequest;
import liaison.linkit.team.service.TeamMiniProfileService;
import liaison.linkit.team.service.TeamProfileService;
import liaison.linkit.team.service.TeamProfileTeamBuildingFieldService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

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
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(TeamProfileController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
public class TeamProfileControllerTest extends ControllerTest {

    private static final MemberTokens MEMBER_TOKENS = new MemberTokens("refreshToken", "accessToken");
    private static final Cookie COOKIE = new Cookie("refresh-token", MEMBER_TOKENS.getRefreshToken());

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TeamProfileService teamProfileService;

    @MockBean
    private TeamProfileTeamBuildingFieldService teamProfileTeamBuildingFieldService;

    @MockBean
    private TeamMiniProfileService teamMiniProfileService;

    @BeforeEach
    void setUp() {
        given(refreshTokenRepository.existsById(any())).willReturn(true);
        doNothing().when(jwtProvider).validateTokens(any());
        given(jwtProvider.getSubject(any())).willReturn("1");
    }

    private ResultActions performPostRequest (final OnBoardingFieldTeamInformRequest onBoardingFieldTeamInformRequest) throws Exception {
        return mockMvc.perform(
                post("/team_profile/field/basic-team")
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(onBoardingFieldTeamInformRequest))
        );
    }

    @DisplayName("희망 팀빌딩 분야 및 미니프로필 정보 생성")
    @Test
    void postOnBoardingFieldTeamInform() throws Exception {
        // given
        List<String> teamBuildingFieldNames = Arrays.asList("공모전", "대회");

        final OnBoardingFieldTeamInformRequest onBoardingFieldTeamInformRequest = new OnBoardingFieldTeamInformRequest(
                teamBuildingFieldNames,
                "리에종",
                "1-5인",
                "플랫폼"
        );

        // when
        final ResultActions resultActions = performPostRequest(onBoardingFieldTeamInformRequest);

        // then
        resultActions.andExpect(status().isCreated())
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
                                requestFields(
                                        fieldWithPath("teamBuildingFieldNames")
                                                .type(JsonFieldType.ARRAY)
                                                .description("희망 팀빋딩 분야(7가지 항목)")
                                                .attributes(field("constraint", "문자열의 배열")),
                                        fieldWithPath("teamName")
                                                .type(JsonFieldType.STRING)
                                                .description("팀이름")
                                                .attributes(field("constraint", "문자열")),
                                        fieldWithPath("sizeType")
                                                .type(JsonFieldType.STRING)
                                                .description("팀 규모")
                                                .attributes(field("constraint", "문자열")),
                                        fieldWithPath("sectorName")
                                                .type(JsonFieldType.STRING)
                                                .description("분야 이름")
                                                .attributes(field("constraint", "문자열"))
                                )
                        )
                );

    }
}
