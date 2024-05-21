package liaison.linkit.profile.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import liaison.linkit.global.ControllerTest;
import liaison.linkit.login.domain.MemberTokens;
import liaison.linkit.profile.dto.request.miniProfile.MiniProfileCreateRequest;
import liaison.linkit.profile.dto.request.miniProfile.MiniProfileUpdateRequest;
import liaison.linkit.profile.service.MiniProfileService;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

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
import static org.springframework.restdocs.payload.PayloadDocumentation.requestPartFields;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MiniProfileController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
class MiniProfileControllerTest extends ControllerTest {

    private static final MemberTokens MEMBER_TOKENS = new MemberTokens("refreshToken", "accessToken");
    private static final Cookie COOKIE = new Cookie("refresh-token", MEMBER_TOKENS.getRefreshToken());

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MiniProfileService miniProfileService;

    @BeforeEach
    void setUp() {
        given(refreshTokenRepository.existsById(any())).willReturn(true);
        doNothing().when(jwtProvider).validateTokens(any());
        given(jwtProvider.getSubject(any())).willReturn("1");
        given(miniProfileService.validateMiniProfileByMember(1L)).willReturn(1L);
    }

//    private void makeMiniProfile() throws Exception {
//        final MiniProfileCreateRequest miniProfileCreateRequest = new MiniProfileCreateRequest(
//                "안녕하세요.",
//                "Java / Spring Boot / MySQL",
//                "홍익대학교 컴퓨터공학과",
//                "리에종의 개발자입니다."
//        );
//    }

    private ResultActions performGetRequest() throws Exception {
        return mockMvc.perform(
                get("/mini-profile")
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
                        .contentType(APPLICATION_JSON));
    }

    private ResultActions performPostRequest(
            final MiniProfileCreateRequest miniProfileCreateRequest,
            final MultipartFile miniProfileImage
    ) throws Exception {
        return mockMvc.perform(post("/mini-profile")
                .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                .cookie(COOKIE)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(miniProfileCreateRequest)));
    }

    private ResultActions performPutUpdateRequest(
            final MiniProfileUpdateRequest updateRequest
    ) throws Exception {
        return mockMvc.perform(
                put("/mini-profile")
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest))
        );
    }

    private ResultActions performDeleteRequest() throws Exception {
        return mockMvc.perform(delete("/mini-profile")
                .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                .cookie(COOKIE)
        );
    }

//    @DisplayName("미니 프로필을 조회할 수 있다.")
//    @Test
//    void getMiniProfile() throws Exception {
//        // given
////        final MiniProfileResponse response = new MiniProfileResponse(
////                "안녕하세요.",
////                "Java / Spring Boot / MySQL",
////                "홍익대학교 컴퓨터공학과",
////                "리에종의 개발자입니다."
////        );
//
//        given(miniProfileService.getMiniProfileDetail(1L))
//                .willReturn(response);
//
//        // when
//        final ResultActions resultActions = performGetRequest();
//        // then
//
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
//                                responseFields(
//                                        fieldWithPath("oneLineIntroduction")
//                                                .type(JsonFieldType.STRING)
//                                                .description("한 줄 소개")
//                                                .attributes(field("constraint", "문자열")),
//                                        fieldWithPath("interests")
//                                                .type(JsonFieldType.STRING)
//                                                .description("관심 분야")
//                                                .attributes(field("constraint", "문자열")),
//                                        fieldWithPath("firstFreeText")
//                                                .type(JsonFieldType.STRING)
//                                                .description("자율 기입 첫 줄")
//                                                .attributes(field("constraint", "문자열")),
//                                        fieldWithPath("secondFreeText")
//                                                .type(JsonFieldType.STRING)
//                                                .description("자율 기입 둘째 줄")
//                                                .attributes(field("constraint", "문자열"))
//                                )
//                        )
//                );
//    }

    @DisplayName("미니 프로필을 생성할 수 있다.")
    @Test
    void createMiniProfile() throws Exception {
        // given
        final MiniProfileCreateRequest miniProfileCreateRequest = new MiniProfileCreateRequest(
                "시니어 소프트웨어 개발자",
                LocalDate.of(2024, 10,20),
                true,
                "혁신, 팀워크, 의지",
                "Java, Spring, AWS, Microservices, Docker"
        );

        final MockMultipartFile miniProfileImage = new MockMultipartFile(
                "miniProfileImage",
                "logo.png",
                "multipart/form-data",
                new FileInputStream("./src/test/resources/static/images/logo.png")
        );

        final MockMultipartFile createRequest = new MockMultipartFile(
                "miniProfileCreateRequest",
                null,
                "application/json",
                objectMapper.writeValueAsString(miniProfileCreateRequest).getBytes(StandardCharsets.UTF_8)
        );

        // when

        final ResultActions resultActions = mockMvc.perform(multipart(HttpMethod.POST,"/mini-profile")
                .file(miniProfileImage)
                .file(createRequest)
                .accept(APPLICATION_JSON)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .characterEncoding("UTF-8")
                .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                .cookie(COOKIE));

        // then
        resultActions.andExpect(status().isCreated())
                .andDo(restDocs.document(
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
                                partWithName("miniProfileCreateRequest").description("미니 프로필 생성 객체"),
                                partWithName("miniProfileImage").description("미니 프로필 이미지")
                        ),
                        requestPartFields("miniProfileCreateRequest",
                                fieldWithPath("profileTitle").description("프로필 제목"),
                                fieldWithPath("uploadPeriod").description("프로필 업로드 기간").attributes(field("constraint", "LocalDate")),
                                fieldWithPath("uploadDeadline").description("마감 선택 여부"),
                                fieldWithPath("myValue").description("협업 시 중요한 나의 가치"),
                                fieldWithPath("skillSets").description("나의 스킬셋")
                        )
                ));

    }

//    @DisplayName("단일 미니 프로필을 수정할 수 있다.")
//    @Test
//    void updateMiniProfile() throws Exception {
//        // given
//        final MiniProfileUpdateRequest request = new MiniProfileUpdateRequest(
//                "안녕하세요",
//                "Figma / UX/UI / 디자인",
//                "홍익대학교 시각디자인전공",
//                "리에종의 디자이너입니다."
//        );
//
//        doNothing().when(miniProfileService).update(anyLong(), any(MiniProfileUpdateRequest.class));
//
//        // when
//        final ResultActions resultActions = performPutUpdateRequest(request);
//
//        // then
//        resultActions.andExpect(status().isNoContent())
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
//                                        fieldWithPath("oneLineIntroduction")
//                                                .type(JsonFieldType.STRING)
//                                                .description("한 줄 소개")
//                                                .attributes(field("constraint", "문자열")),
//                                        fieldWithPath("interests")
//                                                .type(JsonFieldType.STRING)
//                                                .description("관심 분야")
//                                                .attributes(field("constraint", "문자열")),
//                                        fieldWithPath("firstFreeText")
//                                                .type(JsonFieldType.STRING)
//                                                .description("자율 기입 첫 줄")
//                                                .attributes(field("constraint", "문자열")),
//                                        fieldWithPath("secondFreeText")
//                                                .type(JsonFieldType.STRING)
//                                                .description("자율 기입 둘째 줄")
//                                                .attributes(field("constraint", "문자열"))
//                                )
//                        )
//                );
//    }

//    @DisplayName("단일 미니 프로필을 삭제할 수 있다.")
//    @Test
//    void deleteMiniProfile() throws Exception {
//        // given
//        makeMiniProfile();
//        doNothing().when(miniProfileService).delete(anyLong());
//
//        // when
//        final ResultActions resultActions = performDeleteRequest();
//
//        // then
//        resultActions.andExpect(status().isNoContent())
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
//                                )
//                        )
//                );
//    }

}
