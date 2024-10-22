//package liaison.linkit.profile.presentation;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import jakarta.servlet.http.Cookie;
//import liaison.linkit.global.ControllerTest;
//import liaison.linkit.login.domain.MemberTokens;
//import liaison.linkit.profile.dto.request.miniProfile.MiniProfileRequest;
//import liaison.linkit.profile.service.MiniProfileService;
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
//import org.springframework.test.web.servlet.ResultActions;
//import org.springframework.web.multipart.MultipartFile;
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
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@WebMvcTest(MiniProfileController.class)
//@MockBean(JpaMetamodelMappingContext.class)
//@AutoConfigureRestDocs
//class MiniProfileControllerTest extends ControllerTest {
//
//    private static final MemberTokens MEMBER_TOKENS = new MemberTokens("refreshToken", "accessToken");
//    private static final Cookie COOKIE = new Cookie("refresh-token", MEMBER_TOKENS.getRefreshToken());
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @MockBean
//    private MiniProfileService miniProfileService;
//
//    @BeforeEach
//    void setUp() {
//        given(refreshTokenRepository.existsById(any())).willReturn(true);
//        doNothing().when(jwtProvider).validateTokens(any());
//        given(jwtProvider.getSubject(any())).willReturn("1");
//        doNothing().when(miniProfileService).validateMiniProfileByMember(1L);
//    }
//
//    protected MockMultipartFile getMockMultipartFile() {
//        String name = "miniProfileImage";
//        String contentType = "multipart/form-data";
//        String path = "./src/test/resources/static/images/logo.png";
//
//        return new MockMultipartFile(name, path, contentType, path.getBytes(StandardCharsets.UTF_8));
//    }
//
//    private ResultActions performPostRequest(
//            final MiniProfileRequest miniProfileRequest,
//            final MultipartFile miniProfileImage
//    ) throws Exception {
//        return mockMvc.perform(post("/private/mini-profile")
//                .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
//                .cookie(COOKIE)
//                .contentType(APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(miniProfileRequest)));
//    }
//
//
//    @DisplayName("미니 프로필을 생성할 수 있다.")
//    @Test
//    void createMiniProfile() throws Exception {
//        // given
//        final MiniProfileRequest miniProfileRequest = new MiniProfileRequest(
//                "시니어 소프트웨어 개발자",
//                Arrays.asList("2024 레드닷 수상", "스타트업 경력", "서울대 디자인", "대기업 경력 3년"),
//                true
//        );
//
//        final MockMultipartFile miniProfileImage = new MockMultipartFile(
//                "miniProfileImage",
//                "logo.png",
//                "multipart/form-data",
//                "./src/test/resources/static/images/logo.png".getBytes()
//        );
//
//        final MockMultipartFile createRequest = new MockMultipartFile(
//                "miniProfileRequest",
//                null,
//                "application/json",
//                objectMapper.writeValueAsString(miniProfileRequest).getBytes(StandardCharsets.UTF_8)
//        );
//
//        // when
//        final ResultActions resultActions = mockMvc.perform(multipart(HttpMethod.POST,"/private/mini-profile")
//                .file(miniProfileImage)
//                .file(createRequest)
//                .accept(APPLICATION_JSON)
//                .contentType(MediaType.MULTIPART_FORM_DATA)
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
//                                partWithName("miniProfileRequest").description("미니 프로필 생성 객체"),
//                                partWithName("miniProfileImage")
//                                        .description("미니 프로필 이미지 파일. 지원되는 형식은 .png, .jpg 등이 있습니다.")
//                        ),
//                        requestPartFields("miniProfileRequest",
//                                fieldWithPath("profileTitle").description("프로필 제목"),
//                                fieldWithPath("myKeywordNames").description("나를 소개하는 키워드 목록").attributes(field("constraint", "문자열 배열")),
//                                fieldWithPath("isActivate").description("프로필 활성화 여부")
//                        )
//                ));
//    }
//
//    @DisplayName("미니 프로필을 수정할 수 있다.")
//    @Test
//    void updateMiniProfile() throws Exception {
//        // given
//        final MiniProfileRequest miniProfileRequest = new MiniProfileRequest(
//                "시니어 소프트웨어 개발자",
//                Arrays.asList("2024 레드닷 수상", "스타트업 경력", "서울대 디자인", "대기업 경력 3년"),
//                true
//        );
//
//        final MockMultipartFile miniProfileImage = new MockMultipartFile(
//                "miniProfileImage",
//                "logo.png",
//                "multipart/form-data",
//                "./src/test/resources/static/images/logo.png".getBytes()
//        );
//
//        final MockMultipartFile createRequest = new MockMultipartFile(
//                "miniProfileRequest",
//                null,
//                "application/json",
//                objectMapper.writeValueAsString(miniProfileRequest).getBytes(StandardCharsets.UTF_8)
//        );
//
//        // when
//        final ResultActions resultActions = mockMvc.perform(multipart(HttpMethod.POST,"/private/mini-profile/update")
//                .file(miniProfileImage)
//                .file(createRequest)
//                .accept(APPLICATION_JSON)
//                .contentType(MediaType.MULTIPART_FORM_DATA)
//                .characterEncoding("UTF-8")
//                .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
//                .cookie(COOKIE));
//
//        // then
//        resultActions.andExpect(status().isOk())
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
//                                partWithName("miniProfileRequest").description("미니 프로필 생성 객체"),
//                                partWithName("miniProfileImage")
//                                        .description("미니 프로필 이미지 파일. 지원되는 형식은 .png, .jpg 등이 있습니다.")
//                        ),
//                        requestPartFields("miniProfileRequest",
//                                fieldWithPath("profileTitle").description("프로필 제목"),
//                                fieldWithPath("myKeywordNames").description("나를 소개하는 키워드 목록").attributes(field("constraint", "문자열 배열")),
//                                fieldWithPath("isActivate").description("프로필 활성화 여부")
//                        )
//                ));
//    }
//}
