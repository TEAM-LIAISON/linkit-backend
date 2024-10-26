//package liaison.linkit.profile.presentation;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import groovy.util.logging.Slf4j;
//import jakarta.servlet.http.Cookie;
//import liaison.linkit.global.ControllerTest;
//import liaison.linkit.login.domain.MemberTokens;
//import liaison.linkit.profile.dto.request.teamBuilding.ProfileTeamBuildingCreateRequest;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
//import org.springframework.restdocs.payload.JsonFieldType;
//import org.springframework.test.web.servlet.ResultActions;
//
//import java.util.Arrays;
//import java.util.List;
//
//import static liaison.linkit.global.restdocs.RestDocsConfiguration.field;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.BDDMockito.given;
//import static org.mockito.Mockito.doNothing;
//import static org.springframework.http.HttpHeaders.AUTHORIZATION;
//import static org.springframework.http.MediaType.APPLICATION_JSON;
//import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
//import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
//import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
//import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
//import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
//import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@WebMvcTest(TeamBuildingFieldController.class)
//@MockBean(JpaMetamodelMappingContext.class)
//@AutoConfigureRestDocs
//@Slf4j
//public class TeamBuildingFieldControllerTest extends ControllerTest {
//    private static final MemberTokens MEMBER_TOKENS = new MemberTokens("refreshToken", "accessToken");
//    private static final Cookie COOKIE = new Cookie("refresh-token", MEMBER_TOKENS.getRefreshToken());
//
//    @Autowired
//    private ObjectMapper objectMapper;
//    @MockBean
//    private TeamBuildingFieldService teamBuildingFieldService;
//
//    @BeforeEach
//    void setUp() {
//        given(refreshTokenRepository.existsById(any())).willReturn(true);
//        doNothing().when(jwtProvider).validateTokens(any());
//        given(jwtProvider.getSubject(any())).willReturn("1");
//        doNothing().when(teamBuildingFieldService).validateProfileTeamBuildingFieldByMember(1L);
//    }
//
//    // 희망 팀빌딩 분야 생성/수정 테스트
//    private ResultActions performPostTeamBuildingFieldRequest(final ProfileTeamBuildingCreateRequest createRequest) throws Exception {
//        return mockMvc.perform(
//                post("/private/team_building_field")
//                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
//                        .cookie(COOKIE)
//                        .contentType(APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(createRequest))
//        );
//    }
//
//    // 1.5.2. 희망 팀빌딩 분야 생성
//    @DisplayName("희망 팀빌딩 분야 항목들을 생성할 수 있다.")
//    @Test
//    void createProfileTeamBuildingField() throws Exception {
//        // given
//        List<String> teamBuildingFieldNames = Arrays.asList("공모전", "대회", "창업");
//        final ProfileTeamBuildingCreateRequest createRequest = new ProfileTeamBuildingCreateRequest(teamBuildingFieldNames);
//
//        // when
//        final ResultActions resultActions = performPostTeamBuildingFieldRequest(createRequest);
//
//        // then
//        resultActions.andExpect(status().isOk())
//                .andDo(
//                        restDocs.document(
//                                requestCookies(
//                                        cookieWithName("refresh-token")
//                                                .description("갱신 토큰")
//                                ),
//                                requestHeaders(
//                                        headerWithName("Authorization")
//                                                .description("access token")
//                                                .attributes(field("constraint", "문자열(jwt)"))
//                                ),
//                                requestFields(
//                                        fieldWithPath("teamBuildingFieldNames")
//                                                .type(JsonFieldType.ARRAY)
//                                                .description("희망 팀빋딩 분야(7가지 항목)")
//                                                .attributes(field("constraint", "문자열의 배열"))
//                                )
//                        ));
//    }
//}
