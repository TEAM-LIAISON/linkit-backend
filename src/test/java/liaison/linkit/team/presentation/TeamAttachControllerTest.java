package liaison.linkit.team.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import liaison.linkit.global.ControllerTest;
import liaison.linkit.login.domain.MemberTokens;
import liaison.linkit.team.dto.request.attach.TeamAttachUrlCreateRequest;
import liaison.linkit.team.service.TeamAttachService;
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
import static org.mockito.ArgumentMatchers.anyLong;
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

@WebMvcTest(TeamAttachController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
public class TeamAttachControllerTest extends ControllerTest {
    private static final MemberTokens MEMBER_TOKENS = new MemberTokens("refreshToken", "accessToken");
    private static final Cookie COOKIE = new Cookie("refresh-token", MEMBER_TOKENS.getRefreshToken());

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TeamAttachService teamAttachService;

    @BeforeEach
    void setUp() {
        given(refreshTokenRepository.existsById(any())).willReturn(true);
        doNothing().when(jwtProvider).validateTokens(any());
        given(jwtProvider.getSubject(any())).willReturn("1");
        doNothing().when(teamAttachService).validateTeamAttachUrlByMember(anyLong());
        doNothing().when(teamAttachService).validateTeamAttachFileByMember(anyLong());
    }

    private ResultActions performPostTeamUrlRequest(final List<TeamAttachUrlCreateRequest> teamAttachUrlCreateRequests) throws Exception {
        return mockMvc.perform(
                post("/team/attach/url")
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(teamAttachUrlCreateRequests))
        );
    }

    @DisplayName("팀 첨부 링크 리스트를 생성할 수 있다.")
    @Test
    void createTeamAttachUrl() throws Exception {
        // given
        final TeamAttachUrlCreateRequest firstTeamAttachUrlCreateRequest = new TeamAttachUrlCreateRequest(
                "깃허브",
                "https://github.com/TEAM-LIAISON"
        );

        final TeamAttachUrlCreateRequest secondTeamAttachUrlCreateRequest = new TeamAttachUrlCreateRequest(
                "노션",
                "https://www.notion.no"
        );

        final List<TeamAttachUrlCreateRequest> teamAttachUrlCreateRequestList = Arrays.asList(firstTeamAttachUrlCreateRequest, secondTeamAttachUrlCreateRequest);

        // when
        final ResultActions resultActions = performPostTeamUrlRequest(teamAttachUrlCreateRequestList);

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
                                        fieldWithPath("[].teamAttachUrlName")
                                                .type(JsonFieldType.STRING)
                                                .description("팀 첨부 웹 링크 이름")
                                                .attributes(field("constraint", "문자열")),
                                        fieldWithPath("[].teamAttachUrlPath")
                                                .type(JsonFieldType.STRING)
                                                .description("팀 첨부 웹 링크 경로")
                                                .attributes(field("constraint", "문자열"))
                                )
                        )
                );
    }
}
