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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import liaison.linkit.common.presentation.CommonResponse;
import liaison.linkit.global.ControllerTest;
import liaison.linkit.login.domain.MemberTokens;
import liaison.linkit.scrap.business.TeamScrapService;
import liaison.linkit.scrap.presentation.dto.teamScrap.TeamScrapRequestDTO.UpdateTeamScrapRequest;
import liaison.linkit.scrap.presentation.dto.teamScrap.TeamScrapResponseDTO;
import liaison.linkit.scrap.presentation.dto.teamScrap.TeamScrapResponseDTO.UpdateTeamScrap;
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

    private ResultActions performUpdateTeamScrap(final String teamName, final UpdateTeamScrapRequest request) throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders.post("/api/v1/team/scrap/{teamName}", teamName)
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(objectMapper.writeValueAsString(request))
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
                .teamName("팀 이름")
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
                                        parameterWithName("teamName")
                                                .description("팀 이름")
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

                                        fieldWithPath("result.teamName")
                                                .type(JsonFieldType.STRING)
                                                .description("팀 이름"),
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
}
