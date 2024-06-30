package liaison.linkit.profile.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import liaison.linkit.global.ControllerTest;
import liaison.linkit.login.domain.MemberTokens;
import liaison.linkit.region.dto.request.ProfileRegionCreateRequest;
import liaison.linkit.region.service.ProfileRegionService;
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

@WebMvcTest(RegionController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
public class RegionControllerTest extends ControllerTest {

    private static final MemberTokens MEMBER_TOKENS = new MemberTokens("refreshToken", "accessToken");
    private static final Cookie COOKIE = new Cookie("refresh-token", MEMBER_TOKENS.getRefreshToken());

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProfileRegionService profileRegionService;

    @BeforeEach
    void setUp() {
        given(refreshTokenRepository.existsById(any())).willReturn(true);
        doNothing().when(jwtProvider).validateTokens(any());
        given(jwtProvider.getSubject(any())).willReturn("1");
//        given(profileService.validateProfileByMember(1L)).willReturn(1L);
    }

    private void makeProfileRegion() throws Exception {
        final ProfileRegionCreateRequest profileRegionCreateRequest = new ProfileRegionCreateRequest(
                "서울특별시",
                "강남구"
        );
    }

    private ResultActions performPostRequest(final ProfileRegionCreateRequest profileRegionCreateRequest) throws Exception {
        return mockMvc.perform(
                post("/private/region")
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(profileRegionCreateRequest))
        );
    }

    @DisplayName("내 이력서의 지역 정보를 생성할 수 있다.")
    @Test
    void createProfileRegion() throws Exception {

        // given
        final ProfileRegionCreateRequest profileRegionCreateRequest = new ProfileRegionCreateRequest(
                "서울특별시",
                "강남구"
        );

        // when
        final ResultActions resultActions = performPostRequest(profileRegionCreateRequest);

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
                                        fieldWithPath("cityName")
                                                .type(JsonFieldType.STRING)
                                                .description("시/구 이름")
                                                .attributes(field("constraint", "문자열")),
                                        fieldWithPath("divisionName")
                                                .type(JsonFieldType.STRING)
                                                .description("시/군/구 이름")
                                                .attributes(field("constraint", "문자열"))
                                )
                        )
                );
    }
}
