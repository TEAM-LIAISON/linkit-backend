package liaison.linkit.search.presentation;

import static liaison.linkit.global.restdocs.RestDocsConfiguration.field;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Arrays;

import jakarta.servlet.http.Cookie;

import com.fasterxml.jackson.databind.ObjectMapper;
import liaison.linkit.global.ControllerTest;
import liaison.linkit.login.domain.MemberTokens;
import liaison.linkit.search.business.service.LogSearchService;
import liaison.linkit.search.presentation.dto.LogResponseDTO;
import liaison.linkit.search.presentation.dto.LogResponseDTO.LogInformDetails;
import liaison.linkit.search.presentation.dto.LogResponseDTO.LogInformMenu;
import liaison.linkit.search.presentation.dto.LogResponseDTO.LogInformMenus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(LogSearchController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
public class LogSearchControllerTest extends ControllerTest {

    private static final MemberTokens MEMBER_TOKENS =
            new MemberTokens("refreshToken", "accessToken");
    private static final Cookie COOKIE =
            new Cookie("refreshToken", MEMBER_TOKENS.getRefreshToken());

    @Autowired private ObjectMapper objectMapper;

    @MockBean private LogSearchService logSearchService;

    private ResultActions performLogSearch() throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders.get("/api/v1/home/logs")
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE));
    }

    @DisplayName("홈화면의 인기 로그를 조회할 수 있다.")
    @Test
    void getHomeLogInformMenus() throws Exception {
        // given
        final LogResponseDTO.LogInformMenus logInformMenus =
                LogInformMenus.builder()
                        .logInformMenus(
                                Arrays.asList(
                                        LogInformMenu.builder()
                                                .id(1L)
                                                .domainType("PROFILE")
                                                .createdAt(LocalDateTime.now())
                                                .logTitle("로그 제목")
                                                .logContent("로그 내용")
                                                .logInformDetails(
                                                        LogInformDetails.profileLogType(
                                                                "회원 이름", 1L, "회원 이름", "프로필 이미지 경로"))
                                                .build(),
                                        LogInformMenu.builder()
                                                .id(2L)
                                                .domainType("TEAM")
                                                .createdAt(LocalDateTime.now())
                                                .logTitle("로그 제목")
                                                .logContent("로그 내용")
                                                .logInformDetails(
                                                        LogInformDetails.teamLogType(
                                                                "팀 ID (팀 코드)",
                                                                1L,
                                                                "팀 이름",
                                                                "팀 로고 이미지 경로"))
                                                .build()))
                        .build();
        // when
        when(logSearchService.getHomeLogInformMenus()).thenReturn(logInformMenus);

        final ResultActions resultActions = performLogSearch();
        // then
        final MvcResult mvcResult =
                resultActions
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.isSuccess").value("true"))
                        .andExpect(jsonPath("$.code").value("1000"))
                        .andExpect(jsonPath("$.message").value("요청에 성공하였습니다."))
                        .andDo(
                                restDocs.document(
                                        responseFields(
                                                fieldWithPath("isSuccess")
                                                        .type(JsonFieldType.BOOLEAN)
                                                        .description("요청 성공 여부")
                                                        .attributes(
                                                                field("constraint", "boolean 값")),
                                                fieldWithPath("code")
                                                        .type(JsonFieldType.STRING)
                                                        .description("요청 성공 코드")
                                                        .attributes(field("constraint", "문자열")),
                                                fieldWithPath("message")
                                                        .type(JsonFieldType.STRING)
                                                        .description("요청 성공 메시지")
                                                        .attributes(field("constraint", "문자열")),

                                                // Result 필드
                                                fieldWithPath("result")
                                                        .type(JsonFieldType.OBJECT)
                                                        .description("결과 데이터 객체"),

                                                // logInformMenus 배열
                                                fieldWithPath("result.logInformMenus")
                                                        .type(JsonFieldType.ARRAY)
                                                        .description("인기 로그 목록 배열"),

                                                // 각 logInformMenu 객체의 필드
                                                fieldWithPath("result.logInformMenus[].id")
                                                        .type(JsonFieldType.NUMBER)
                                                        .description("로그 ID"),
                                                fieldWithPath("result.logInformMenus[].domainType")
                                                        .type(JsonFieldType.STRING)
                                                        .description(
                                                                "로그의 도메인 타입 (PROFILE 또는 TEAM)"),
                                                fieldWithPath("result.logInformMenus[].createdAt")
                                                        .type(JsonFieldType.STRING)
                                                        .description("로그 생성 시간 (ISO 8601 형식)"),
                                                fieldWithPath("result.logInformMenus[].logTitle")
                                                        .type(JsonFieldType.STRING)
                                                        .description("로그 제목"),
                                                fieldWithPath("result.logInformMenus[].logContent")
                                                        .type(JsonFieldType.STRING)
                                                        .description("로그 내용"),

                                                // logInformDetails 내부 필드 (도메인별 구분)
                                                fieldWithPath(
                                                                "result.logInformMenus[].logInformDetails")
                                                        .type(JsonFieldType.OBJECT)
                                                        .description("로그 상세 정보"),

                                                // PROFILE 도메인 관련 필드
                                                fieldWithPath(
                                                                "result.logInformMenus[].logInformDetails.emailId")
                                                        .type(JsonFieldType.STRING)
                                                        .description("유저 아이디 (이메일 ID)")
                                                        .optional(),
                                                fieldWithPath(
                                                                "result.logInformMenus[].logInformDetails.profileLogId")
                                                        .type(JsonFieldType.NUMBER)
                                                        .description("프로필 로그 아이디 (PK)")
                                                        .optional(),
                                                fieldWithPath(
                                                                "result.logInformMenus[].logInformDetails.memberName")
                                                        .type(JsonFieldType.STRING)
                                                        .description("회원 이름 (PROFILE 도메인일 경우)")
                                                        .optional(),
                                                fieldWithPath(
                                                                "result.logInformMenus[].logInformDetails.profileImagePath")
                                                        .type(JsonFieldType.STRING)
                                                        .description("프로필 이미지 경로 (PROFILE 도메인일 경우)")
                                                        .optional(),

                                                // TEAM 도메인 관련 필드
                                                fieldWithPath(
                                                                "result.logInformMenus[].logInformDetails.teamCode")
                                                        .type(JsonFieldType.STRING)
                                                        .description("팀 아이디 (팀 코드)")
                                                        .optional(),
                                                fieldWithPath(
                                                                "result.logInformMenus[].logInformDetails.teamLogId")
                                                        .type(JsonFieldType.NUMBER)
                                                        .description("팀 로그 아이디 (PK)")
                                                        .optional(),
                                                fieldWithPath(
                                                                "result.logInformMenus[].logInformDetails.teamName")
                                                        .type(JsonFieldType.STRING)
                                                        .description("팀 이름 (TEAM 도메인일 경우)")
                                                        .optional(),
                                                fieldWithPath(
                                                                "result.logInformMenus[].logInformDetails.teamLogoImagePath")
                                                        .type(JsonFieldType.STRING)
                                                        .description("팀 로고 이미지 경로 (TEAM 도메인일 경우)")
                                                        .optional())))
                        .andReturn();
    }
}
