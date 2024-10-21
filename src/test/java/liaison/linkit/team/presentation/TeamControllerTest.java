package liaison.linkit.team.presentation;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import java.nio.charset.StandardCharsets;
import liaison.linkit.global.ControllerTest;
import liaison.linkit.login.domain.MemberTokens;
import liaison.linkit.team.presentation.team.TeamController;
import liaison.linkit.team.presentation.team.dto.TeamRequestDTO.SaveTeamBasicInformRequest;
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

        // when
        final ResultActions resultActions = mockMvc.perform(multipart(HttpMethod.POST, "/api/v1/team")
                .file(teamLogoImage)
                .file(createRequest)
                .accept(APPLICATION_JSON)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .characterEncoding("UTF-8")
                .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                .cookie(COOKIE));

        // then
    }
}
