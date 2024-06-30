package liaison.linkit.profile.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import liaison.linkit.global.ControllerTest;
import liaison.linkit.login.domain.MemberTokens;
import liaison.linkit.profile.dto.request.education.EducationCreateRequest;
import liaison.linkit.profile.dto.request.education.EducationListCreateRequest;
import liaison.linkit.profile.service.EducationService;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EducationController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
public class EducationControllerTest extends ControllerTest {
    private static final MemberTokens MEMBER_TOKENS = new MemberTokens("refreshToken", "accessToken");
    private static final Cookie COOKIE = new Cookie("refresh-token", MEMBER_TOKENS.getRefreshToken());

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EducationService educationService;

    @BeforeEach
    void setUp() {
        given(refreshTokenRepository.existsById(any())).willReturn(true);
        doNothing().when(jwtProvider).validateTokens(any());
        given(jwtProvider.getSubject(any())).willReturn("1");
        doNothing().when(educationService).validateEducationByMember(1L);
    }
    private void makeEducation() throws Exception {
        // given
        final EducationCreateRequest firstEducationCreateRequest = new EducationCreateRequest(
                2022,
                2025,
                "홍익대학교",
                "컴퓨터공학과",
                "졸업"
        );

        final EducationCreateRequest secondEducationCreateRequest = new EducationCreateRequest(
                2022,
                2025,
                "연세대학교",
                "경영학과",
                "졸업"
        );
        final List<EducationCreateRequest> educationCreateRequests = Arrays.asList(firstEducationCreateRequest, secondEducationCreateRequest);

        final EducationListCreateRequest educationListCreateRequest = new EducationListCreateRequest(
                educationCreateRequests
        );

        doNothing().when(educationService).save(anyLong(), anyList());
        performPostRequest(educationListCreateRequest);
    }

    // 학력 항목 생성/수정 테스트
    private ResultActions performPostRequest(final EducationListCreateRequest educationListCreateRequest) throws Exception {
        return mockMvc.perform(
                post("/private/education")
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(educationListCreateRequest))
        );
    }

    // 경력 항목 삭제 테스트
    private ResultActions performDeleteRequest(final int educationId) throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders.delete("/private/{educationId}", educationId)
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
                        .contentType(APPLICATION_JSON)
        );
    }

    // 1.5.6. 학력 생성 테스트
    @DisplayName("학력 항목을 생성할 수 있다.")
    @Test
    void createEducation() throws Exception {
        // given
        final EducationCreateRequest firstEducationCreateRequest = new EducationCreateRequest(
                2022,
                2025,
                "홍익대학교",
                "컴퓨터공학과",
                "졸업"
        );

        final EducationCreateRequest secondEducationCreateRequest = new EducationCreateRequest(
                2022,
                2025,
                "연세대학교",
                "경영학과",
                "졸업"
        );
        final List<EducationCreateRequest> educationCreateRequests = Arrays.asList(firstEducationCreateRequest, secondEducationCreateRequest);

        final EducationListCreateRequest educationListCreateRequest = new EducationListCreateRequest(
                educationCreateRequests
        );

        doNothing().when(educationService).save(anyLong(), anyList());

        // when
        final ResultActions resultActions = performPostRequest(educationListCreateRequest);

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
                                requestFields(
                                        subsectionWithPath("educationList").description("학력 정보 배열").attributes(field("constraint", "객체(배열)")),
                                        fieldWithPath("educationList[].admissionYear")
                                                .type(JsonFieldType.NUMBER)
                                                .description("입학 연도")
                                                .attributes(field("constraint", "4자리 숫자")),
                                        fieldWithPath("educationList[].graduationYear")
                                                .type(JsonFieldType.NUMBER)
                                                .description("졸업 연도")
                                                .attributes(field("constraint", "4자리 숫자")),
                                        fieldWithPath("educationList[].universityName")
                                                .type(JsonFieldType.STRING)
                                                .description("학교명")
                                                .attributes(field("constraint", "문자열")),
                                        fieldWithPath("educationList[].majorName")
                                                .type(JsonFieldType.STRING)
                                                .description("전공명")
                                                .attributes(field("constraint", "문자열")),
                                        fieldWithPath("educationList[].degreeName")
                                                .type(JsonFieldType.STRING)
                                                .description("학위명")
                                                .attributes(field("constraint", "문자열"))
                                )
                        )
                );
    }

    @DisplayName("학력 항목 1개를 삭제할 수 있다.")
    @Test
    void deleteEducation() throws Exception {
        // given
        makeEducation();
        doNothing().when(educationService).validateEducationByMember(anyLong());

        // when
        final ResultActions resultActions = performDeleteRequest(1);

        // then
        verify(educationService).delete(1L, 1L);

        resultActions.andExpect(status().isNoContent())
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("educationId")
                                        .description("학력 항목 ID")
                        )
                ));
    }

}
