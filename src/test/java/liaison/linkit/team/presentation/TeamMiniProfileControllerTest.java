//package liaison.linkit.team.presentation;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import jakarta.servlet.http.Cookie;
//import liaison.linkit.global.ControllerTest;
//import liaison.linkit.login.domain.MemberTokens;
//import liaison.linkit.team.dto.request.miniprofile.TeamMiniProfileCreateRequest;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.MediaType;
//import org.springframework.mock.web.MockMultipartFile;
//import org.springframework.restdocs.payload.JsonFieldType;
//import org.springframework.test.web.servlet.ResultActions;
//
//import java.nio.charset.StandardCharsets;
//import java.util.Arrays;
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
//import static org.springframework.restdocs.payload.PayloadDocumentation.requestPartFields;
//import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
//import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@WebMvcTest(TeamMiniProfileController.class)
//@MockBean(JpaMetamodelMappingContext.class)
//@AutoConfigureRestDocs
//public class TeamMiniProfileControllerTest extends ControllerTest {
//
//    private static final MemberTokens MEMBER_TOKENS = new MemberTokens("refreshToken", "accessToken");
//    private static final Cookie COOKIE = new Cookie("refresh-token", MEMBER_TOKENS.getRefreshToken());
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @MockBean
//    private TeamMiniProfileService teamMiniProfileService;
//
//    @BeforeEach
//    void setUp() {
//        given(refreshTokenRepository.existsById(any())).willReturn(true);
//        doNothing().when(jwtProvider).validateTokens(any());
//        given(jwtProvider.getSubject(any())).willReturn("1");
//        doNothing().when(teamMiniProfileService).validateTeamMiniProfileByMember(1L);
//    }
//
//    protected MockMultipartFile getMockMultipartFile() {
//        String name = "miniProfileImage";
//        String contentType = "multipart/form-data";
//        String path = "./src/test/resources/static/images/logo.png";
//        return new MockMultipartFile(name, path, contentType, path.getBytes(StandardCharsets.UTF_8));
//    }
//
//    @DisplayName("팀 소개서 미니 프로필을 생성할 수 있다.")
//    @Test
//    void createTeamMiniProfile() throws Exception {
//        // given
//        final TeamMiniProfileCreateRequest teamMiniProfileCreateRequest = new TeamMiniProfileCreateRequest(
//                "사이드 프로젝트 함께 할 개발자를 찾고 있어요",
//                Arrays.asList("재택 가능", "Pre-A", "사수 있음", "스톡 제공"),
//                true
//        );
//
//        final MockMultipartFile teamMiniProfileImage = new MockMultipartFile(
//                "teamMiniProfileImage",
//                "logo.png",
//                "multipart/form-data",
//                "./src/test/resources/static/images/logo.png".getBytes()
//        );
//
//        final MockMultipartFile createRequest = new MockMultipartFile(
//                "teamMiniProfileCreateRequest",
//                null,
//                "application/json",
//                objectMapper.writeValueAsString(teamMiniProfileCreateRequest).getBytes(StandardCharsets.UTF_8)
//        );
//
////        MockMultipartHttpServletRequestBuilder customRestDocumentationRequestBuilder =
////                RestDocumentationRequestBuilders.multipart("/team/mini-profile", teamMiniProfileImage, createRequest);
//
//        // when
//
//        final ResultActions resultActions = mockMvc.perform(multipart(HttpMethod.POST, "/team/mini-profile")
//                .file(teamMiniProfileImage)
//                .file(createRequest)
//                .accept(APPLICATION_JSON)
//                .contentType(MediaType.MULTIPART_FORM_DATA)
////                .content(objectMapper.writeValueAsString(miniProfileCreateRequest))
//                .characterEncoding("UTF-8")
//                .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
//                .cookie(COOKIE));
//
//        // then
//        resultActions.andExpect(status().isCreated())
//                .andDo(restDocs.document(
//                        requestCookies(
//                                cookieWithName("refresh-token")
//                                        .description("갱신 토큰")
//                        ),
//                        requestHeaders(
//                                headerWithName("Authorization")
//                                        .description("access token")
//                                        .attributes(field("constraint", "문자열(jwt)"))
//                        ),
//                        requestParts(
//                                partWithName("teamMiniProfileCreateRequest").description("팀 소개서 미니 이력서 생성 객체"),
//                                partWithName("teamMiniProfileImage")
//                                        .description("팀 소개서 미니 프로필 이미지 파일. 지원되는 형식은 .png, .jpg 등이 있습니다.")
//                        ),
//                        requestPartFields("teamMiniProfileCreateRequest",
//                                fieldWithPath("teamProfileTitle").description("팀 소개서 제목"),
//                                fieldWithPath("isTeamActivate").description("팀 소개서 활성화 여부"),
//                                fieldWithPath("teamKeywordNames").type(JsonFieldType.ARRAY).description("팀 소개 항목").attributes(field("constraint", "문자열 배열"))
//                        )
//                ));
//
//    }
//}
