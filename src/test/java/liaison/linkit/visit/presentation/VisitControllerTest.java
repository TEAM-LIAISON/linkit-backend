package liaison.linkit.visit.presentation;

import static liaison.linkit.global.restdocs.RestDocsConfiguration.field;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;

import jakarta.servlet.http.Cookie;

import com.fasterxml.jackson.databind.ObjectMapper;
import liaison.linkit.global.ControllerTest;
import liaison.linkit.login.domain.MemberTokens;
import liaison.linkit.profile.presentation.profile.dto.ProfileResponseDTO;
import liaison.linkit.visit.business.service.VisitService;
import liaison.linkit.visit.presentation.dto.VisitResponseDTO;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(VisitController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
public class VisitControllerTest extends ControllerTest {

    private static final MemberTokens MEMBER_TOKENS =
            new MemberTokens("refreshToken", "accessToken");
    private static final Cookie COOKIE =
            new Cookie("refreshToken", MEMBER_TOKENS.getRefreshToken());

    @Autowired private ObjectMapper objectMapper;

    @MockBean private VisitService visitService;

    @BeforeEach
    void setUp() {
        given(refreshTokenRepository.existsById(any())).willReturn(true);
        doNothing().when(jwtProvider).validateTokens(any());
        given(jwtProvider.getSubject(any())).willReturn("1");
    }

    private ResultActions performGetProfileVisits() throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders.get("/api/v2/profile/visit")
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE));
    }

    private ResultActions performGetTeamVisits(final String teamCode) throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders.get("/api/v2/team/{teamCode}/visit", teamCode)
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE));
    }

    @DisplayName("회원이 나의 프로필에 방문한 회원 정보를 조회할 수 있다.")
    @Test
    void getProfileVisitInforms() throws Exception {
        // given
        final VisitResponseDTO.VisitInforms visitInforms =
                VisitResponseDTO.VisitInforms.builder()
                        .visitInforms(
                                Arrays.asList(
                                        VisitResponseDTO.VisitInform.builder()
                                                .profileImagePath("프로필 이미지 경로")
                                                .memberName("회원 이름")
                                                .emailId("방문한 회원의 유저 ID")
                                                .profilePositionDetail(
                                                        ProfileResponseDTO.ProfilePositionDetail
                                                                .builder()
                                                                .majorPosition("포지션 대분류")
                                                                .subPosition("포지션 소분류")
                                                                .build())
                                                .build(),
                                        VisitResponseDTO.VisitInform.builder()
                                                .profileImagePath("프로필 이미지 경로")
                                                .memberName("회원 이름")
                                                .emailId("방문한 회원의 유저 ID")
                                                .profilePositionDetail(
                                                        ProfileResponseDTO.ProfilePositionDetail
                                                                .builder()
                                                                .majorPosition("포지션 대분류")
                                                                .subPosition("포지션 소분류")
                                                                .build())
                                                .build()))
                        .build();
        // when
        when(visitService.getProfileVisitInforms(anyLong())).thenReturn(visitInforms);

        final ResultActions resultActions = performGetProfileVisits();
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
                                                fieldWithPath("result")
                                                        .type(JsonFieldType.OBJECT)
                                                        .description("응답 결과"),
                                                fieldWithPath(
                                                                "result.visitInforms[].profileImagePath")
                                                        .type(JsonFieldType.STRING)
                                                        .description("방문자 프로필 이미지 경로"),
                                                fieldWithPath("result.visitInforms[].memberName")
                                                        .type(JsonFieldType.STRING)
                                                        .description("방문자 프로필 이름"),
                                                fieldWithPath("result.visitInforms[].emailId")
                                                        .type(JsonFieldType.STRING)
                                                        .description("방문자 유저 아이디"),
                                                fieldWithPath(
                                                                "result.visitInforms[].profilePositionDetail")
                                                        .type(JsonFieldType.OBJECT)
                                                        .description("프로필 포지션 정보 객체"),
                                                fieldWithPath(
                                                                "result.visitInforms[].profilePositionDetail.majorPosition")
                                                        .type(JsonFieldType.STRING)
                                                        .description("프로필 대분류 포지션"),
                                                fieldWithPath(
                                                                "result.visitInforms[].profilePositionDetail.subPosition")
                                                        .type(JsonFieldType.STRING)
                                                        .description("프로필 소분류 포지션"))))
                        .andReturn();
    }
}
