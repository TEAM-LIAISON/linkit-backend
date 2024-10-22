//package liaison.linkit.team.presentation;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import jakarta.servlet.http.Cookie;
//import liaison.linkit.global.ControllerTest;
//import liaison.linkit.login.domain.MemberTokens;
//import liaison.linkit.team.dto.request.activity.ActivityCreateRequest;
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
//@WebMvcTest(ActivityController.class)
//@MockBean(JpaMetamodelMappingContext.class)
//@AutoConfigureRestDocs
//public class ActivityControllerTest extends ControllerTest {
//
//    private static final MemberTokens MEMBER_TOKENS = new MemberTokens("refreshToken", "accessToken");
//    private static final Cookie COOKIE = new Cookie("refresh-token", MEMBER_TOKENS.getRefreshToken());
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @MockBean
//    private ActivityService activityService;
//
//    @BeforeEach
//    void setUp() {
//        given(refreshTokenRepository.existsById(any())).willReturn(true);
//        doNothing().when(jwtProvider).validateTokens(any());
//        given(jwtProvider.getSubject(any())).willReturn("1");
//    }
//
//    private ResultActions performPostRequest(final ActivityCreateRequest activityCreateRequest) throws Exception {
//        return mockMvc.perform(
//                post("/team/activity")
//                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
//                        .cookie(COOKIE)
//                        .contentType(APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(activityCreateRequest))
//        );
//    }
//
//    @DisplayName("활동 방식 및 분야를 생성할 수 있다.")
//    @Test
//    void postActivity() throws Exception {
//        // given
//        List<String> activityTagNames = Arrays.asList("사무실 있음", "대면 활동 선호");
//
//        final ActivityCreateRequest activityCreateRequest = new ActivityCreateRequest(
//                activityTagNames,
//                "서울특별시",
//                "강남구"
//        );
//
//        // when
//        final ResultActions resultActions = performPostRequest(activityCreateRequest);
//
//        // then
//        resultActions.andExpect(status().isCreated())
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
//                                        fieldWithPath("activityTagNames")
//                                                .type(JsonFieldType.ARRAY)
//                                                .description("활동 방식")
//                                                .attributes(field("constraint", "문자열의 배열")),
//                                        fieldWithPath("cityName")
//                                                .type(JsonFieldType.STRING)
//                                                .description("시/구")
//                                                .attributes(field("constraint", "문자열")),
//                                        fieldWithPath("divisionName")
//                                                .type(JsonFieldType.STRING)
//                                                .description("시/군/구")
//                                                .attributes(field("constraint", "문자열"))
//                                )
//                        )
//                );
//    }
//}
