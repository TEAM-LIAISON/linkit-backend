package liaison.linkit.team.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import liaison.linkit.global.ControllerTest;
import liaison.linkit.login.domain.MemberTokens;
import liaison.linkit.team.dto.request.memberIntroduction.TeamMemberIntroductionCreateRequest;
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
import org.springframework.test.web.servlet.ResultActions;

import java.util.Arrays;
import java.util.List;

import static liaison.linkit.global.restdocs.RestDocsConfiguration.field;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TeamMemberIntroductionController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
class TeamMemberIntroductionControllerTest extends ControllerTest {
    private static final MemberTokens MEMBER_TOKENS = new MemberTokens("refreshToken", "accessToken");
    private static final Cookie COOKIE = new Cookie("refresh-token", MEMBER_TOKENS.getRefreshToken());

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TeamMemberIntroductionService teamMemberIntroductionService;

    @BeforeEach
    void setUp() {
        given(refreshTokenRepository.existsById(any())).willReturn(true);
        doNothing().when(jwtProvider).validateTokens(any());
        given(jwtProvider.getSubject(any())).willReturn("1");
        doNothing().when(teamMemberIntroductionService).validateTeamMemberIntroductionByMember(anyLong());
    }

    private void makeTeamMemberIntroduction() throws Exception {
        final TeamMemberIntroductionCreateRequest firstTeamMemberIntroductionCreateRequest = new TeamMemberIntroductionCreateRequest(
                "김서연",
                "디자이너",
                "레드닷 상 받았어요"
        );

        final TeamMemberIntroductionCreateRequest secondTeamMemberIntroductionCreateRequest = new TeamMemberIntroductionCreateRequest(
                "권동민",
                "백엔드 개발자",
                "백엔드 개발자에요"
        );

        final List<TeamMemberIntroductionCreateRequest> teamMemberIntroductionCreateRequestList = Arrays.asList(firstTeamMemberIntroductionCreateRequest, secondTeamMemberIntroductionCreateRequest);

        doNothing().when(teamMemberIntroductionService).saveTeamMemberIntroductions(1L, teamMemberIntroductionCreateRequestList);
        performPostTeamMemberIntroductionRequest(teamMemberIntroductionCreateRequestList);
    }

    private ResultActions performUpdateTeamMemberIntroduction(final int teamMemberIntroductionId, final TeamMemberIntroductionCreateRequest teamMemberIntroductionCreateRequest) throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders.post("/team/member/{teamMemberIntroductionId}", teamMemberIntroductionId)
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(teamMemberIntroductionCreateRequest))
        );
    }

    private ResultActions performPostTeamMemberIntroduction(final TeamMemberIntroductionCreateRequest teamMemberIntroductionCreateRequest) throws Exception {
        return mockMvc.perform(
                post("/team/member")
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(teamMemberIntroductionCreateRequest))
        );
    }

    private ResultActions performPostTeamMemberIntroductionRequest(final List<TeamMemberIntroductionCreateRequest> teamMemberIntroductionCreateRequests) throws Exception {
        return mockMvc.perform(
                post("/team/members")
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(teamMemberIntroductionCreateRequests))
        );
    }

    private ResultActions performDeleteTeamMemberIntroductionRequest(
            final int teamMemberIntroductionId
    ) throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders.delete("/team/members/{teamMemberIntroductionId}", teamMemberIntroductionId)
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
                        .contentType(APPLICATION_JSON)
        );
    }

    @DisplayName("팀원 소개 단일 항목을 수정할 수 있다.")
    @Test
    void updateTeamMemberIntroduction() throws Exception {
        // given
        final TeamMemberIntroductionCreateRequest teamMemberIntroductionCreateRequest = new TeamMemberIntroductionCreateRequest(
                "김서연",
                "디자이너",
                "레드닷 상 받았어요"
        );
        // when
        final ResultActions resultActions = performUpdateTeamMemberIntroduction(1, teamMemberIntroductionCreateRequest);

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
                                pathParameters(
                                        parameterWithName("teamMemberIntroductionId")
                                                .description("팀원 소개 ID")
                                ),
                                requestFields(
                                        fieldWithPath("teamMemberName")
                                                .type(JsonFieldType.STRING)
                                                .description("팀원 이름")
                                                .attributes(field("constraint", "문자열")),
                                        fieldWithPath("teamMemberRole")
                                                .type(JsonFieldType.STRING)
                                                .description("팀원 직무/역할")
                                                .attributes(field("constraint", "문자열")),
                                        fieldWithPath("teamMemberIntroductionText")
                                                .type(JsonFieldType.STRING)
                                                .description("팀원 소개")
                                                .attributes(field("constraint", "문자열"))
                                )
                        )
                );
    }

    @DisplayName("팀원 소개를 생성할 수 있다.")
    @Test
    void createTeamMemberIntroduction() throws Exception {
        // given
        final TeamMemberIntroductionCreateRequest teamMemberIntroductionCreateRequest = new TeamMemberIntroductionCreateRequest(
                "김서연",
                "디자이너",
                "레드닷 상 받았어요"
        );

        // when
        final ResultActions resultActions = performPostTeamMemberIntroduction(teamMemberIntroductionCreateRequest);

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
                                        fieldWithPath("teamMemberName")
                                                .type(JsonFieldType.STRING)
                                                .description("팀원 이름")
                                                .attributes(field("constraint", "문자열")),
                                        fieldWithPath("teamMemberRole")
                                                .type(JsonFieldType.STRING)
                                                .description("팀원 직무/역할")
                                                .attributes(field("constraint", "문자열")),
                                        fieldWithPath("teamMemberIntroductionText")
                                                .type(JsonFieldType.STRING)
                                                .description("팀원 소개")
                                                .attributes(field("constraint", "문자열"))
                                )
                        )
                );
    }

    @DisplayName("팀원 소개 리스트를 생성할 수 있다,")
    @Test
    void createTeamMemberIntroductions() throws Exception {
        // given
        final TeamMemberIntroductionCreateRequest firstTeamMemberIntroductionCreateRequest = new TeamMemberIntroductionCreateRequest(
                "김서연",
                "디자이너",
                "레드닷 상 받았어요"
        );

        final TeamMemberIntroductionCreateRequest secondTeamMemberIntroductionCreateRequest = new TeamMemberIntroductionCreateRequest(
                "권동민",
                "백엔드 개발자",
                "백엔드 개발자에요"
        );

        final List<TeamMemberIntroductionCreateRequest> teamMemberIntroductionCreateRequestList = Arrays.asList(firstTeamMemberIntroductionCreateRequest, secondTeamMemberIntroductionCreateRequest);

        // when
        final ResultActions resultActions = performPostTeamMemberIntroductionRequest(teamMemberIntroductionCreateRequestList);

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
                                        fieldWithPath("[].teamMemberName")
                                                .type(JsonFieldType.STRING)
                                                .description("팀원 이름")
                                                .attributes(field("constraint", "문자열")),
                                        fieldWithPath("[].teamMemberRole")
                                                .type(JsonFieldType.STRING)
                                                .description("팀원 직무/역할")
                                                .attributes(field("constraint", "문자열")),
                                        fieldWithPath("[].teamMemberIntroductionText")
                                                .type(JsonFieldType.STRING)
                                                .description("팀원 소개")
                                                .attributes(field("constraint", "문자열"))
                                )
                        )
                );
    }

    @DisplayName("팀원 소개 1개를 삭제할 수 있다.")
    @Test
    void deleteTeamMemberIntroduction() throws Exception {
        // given
        makeTeamMemberIntroduction();
        doNothing().when(teamMemberIntroductionService).validateTeamMemberIntroductionByMember(anyLong());

        // when
        final ResultActions resultActions = performDeleteTeamMemberIntroductionRequest(1);

        // then
        verify(teamMemberIntroductionService).deleteTeamMemberIntroduction(1L, 1L);

        resultActions.andExpect(status().isNoContent())
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("teamMemberIntroductionId")
                                        .description("팀원 소개 ID")
                        )
                ));
    }
}
