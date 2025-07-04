package liaison.linkit.member.presentation;

import static liaison.linkit.global.restdocs.RestDocsConfiguration.field;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;

import jakarta.servlet.http.Cookie;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import groovy.util.logging.Slf4j;
import liaison.linkit.common.presentation.CommonResponse;
import liaison.linkit.global.ControllerTest;
import liaison.linkit.login.domain.MemberTokens;
import liaison.linkit.member.business.MemberService;
import liaison.linkit.member.domain.type.Platform;
import liaison.linkit.member.presentation.dto.MemberBasicInformRequestDTO;
import liaison.linkit.member.presentation.dto.MemberBasicInformRequestDTO.AuthCodeVerificationRequest;
import liaison.linkit.member.presentation.dto.MemberBasicInformRequestDTO.MailReAuthenticationRequest;
import liaison.linkit.member.presentation.dto.MemberBasicInformRequestDTO.UpdateConsentMarketingRequest;
import liaison.linkit.member.presentation.dto.MemberBasicInformRequestDTO.UpdateConsentServiceUseRequest;
import liaison.linkit.member.presentation.dto.MemberBasicInformRequestDTO.UpdateMemberBasicInformRequest;
import liaison.linkit.member.presentation.dto.MemberBasicInformRequestDTO.UpdateMemberContactRequest;
import liaison.linkit.member.presentation.dto.MemberBasicInformRequestDTO.UpdateMemberNameRequest;
import liaison.linkit.member.presentation.dto.MemberBasicInformResponseDTO;
import liaison.linkit.member.presentation.dto.MemberBasicInformResponseDTO.MailReAuthenticationResponse;
import liaison.linkit.member.presentation.dto.MemberBasicInformResponseDTO.MailVerificationResponse;
import liaison.linkit.member.presentation.dto.MemberBasicInformResponseDTO.MemberBasicInformDetail;
import liaison.linkit.member.presentation.dto.MemberBasicInformResponseDTO.UpdateConsentMarketingResponse;
import liaison.linkit.member.presentation.dto.MemberBasicInformResponseDTO.UpdateConsentServiceUseResponse;
import liaison.linkit.member.presentation.dto.MemberBasicInformResponseDTO.UpdateMemberBasicInformResponse;
import liaison.linkit.member.presentation.dto.MemberBasicInformResponseDTO.UpdateMemberContactResponse;
import liaison.linkit.member.presentation.dto.MemberBasicInformResponseDTO.UpdateMemberNameResponse;
import liaison.linkit.member.presentation.dto.MemberRequestDTO;
import liaison.linkit.member.presentation.dto.MemberRequestDTO.UpdateMemberUserIdRequest;
import liaison.linkit.member.presentation.dto.MemberResponseDTO;
import liaison.linkit.member.presentation.dto.MemberResponseDTO.UpdateMemberUserIdResponse;
import org.junit.jupiter.api.BeforeEach;
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

@WebMvcTest(MemberController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
@Slf4j
class MemberControllerTest extends ControllerTest {

    private static final String REFRESH_TOKEN = "refreshToken";
    private static final String RENEW_ACCESS_TOKEN = "I'mNewAccessToken!";

    private static final MemberTokens MEMBER_TOKENS =
            new MemberTokens("refreshToken", "accessToken");
    private static final Cookie COOKIE =
            new Cookie("refreshToken", MEMBER_TOKENS.getRefreshToken());

    @Autowired private ObjectMapper objectMapper;

    @MockBean private MemberService memberService;

    @BeforeEach
    void setUp() {
        given(refreshTokenRepository.existsById(any())).willReturn(true);
        doNothing().when(jwtProvider).validateTokens(any());
        given(jwtProvider.getSubject(any())).willReturn("1");
    }

    private ResultActions performUpdateMemberBasicInform(
            final MemberBasicInformRequestDTO.UpdateMemberBasicInformRequest request)
            throws Exception {
        return mockMvc.perform(
                post("/api/v1/member/basic-inform")
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));
    }

    private ResultActions performUpdateConsentServiceUse(
            final MemberBasicInformRequestDTO.UpdateConsentServiceUseRequest request)
            throws Exception {
        return mockMvc.perform(
                post("/api/v1/member/consent")
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));
    }

    private ResultActions performGetMemberBasicInformDetail() throws Exception {
        return mockMvc.perform(
                get("/api/v1/member/basic-inform")
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE));
    }

    private ResultActions performUpdateMemberName(
            final MemberBasicInformRequestDTO.UpdateMemberNameRequest updateMemberNameRequest)
            throws Exception {
        return mockMvc.perform(
                post("/api/v1/member/name")
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateMemberNameRequest)));
    }

    private ResultActions performUpdateMemberUserId(
            final MemberRequestDTO.UpdateMemberUserIdRequest updateMemberUserIdRequest)
            throws Exception {
        return mockMvc.perform(
                post("/api/v1/member/userId")
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateMemberUserIdRequest)));
    }

    private ResultActions performUpdateMemberContact(
            final MemberBasicInformRequestDTO.UpdateMemberContactRequest updateMemberContactRequest)
            throws Exception {
        return mockMvc.perform(
                post("/api/v1/member/contact")
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateMemberContactRequest)));
    }

    private ResultActions performUpdateConsentMarketing(
            final MemberBasicInformRequestDTO.UpdateConsentMarketingRequest
                    updateConsentMarketingRequest)
            throws Exception {
        return mockMvc.perform(
                post("/api/v1/member/consent/marketing")
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateConsentMarketingRequest)));
    }

    @DisplayName("회원 기본 정보를 수정 요청할 수 있다.")
    @Test
    void updateMemberBasicInform() throws Exception {
        // given
        final MemberBasicInformRequestDTO.UpdateMemberBasicInformRequest
                updateMemberBasicInformRequest =
                        new UpdateMemberBasicInformRequest("권동민", "kwondm7", "01036614067");

        final MemberBasicInformResponseDTO.UpdateMemberBasicInformResponse
                updateMemberBasicInformResponse =
                        new UpdateMemberBasicInformResponse(
                                1L, "권동민", "kwondm7", "01036614067", "kwondm7@gmail.com");

        // when
        when(memberService.updateMemberBasicInform(anyLong(), any()))
                .thenReturn(updateMemberBasicInformResponse);

        final ResultActions resultActions =
                performUpdateMemberBasicInform(updateMemberBasicInformRequest);

        // then
        final MvcResult mvcResult =
                resultActions
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.isSuccess").value("true"))
                        .andExpect(jsonPath("$.code").value("1000"))
                        .andExpect(jsonPath("$.message").value("요청에 성공하였습니다."))
                        .andDo(
                                restDocs.document(
                                        requestFields(
                                                fieldWithPath("memberName")
                                                        .type(JsonFieldType.STRING)
                                                        .description("회원 이름")
                                                        .attributes(field("constraint", "문자열")),
                                                fieldWithPath("emailId")
                                                        .type(JsonFieldType.STRING)
                                                        .description("유저 아이디")
                                                        .attributes(field("constraint", "문자열")),
                                                fieldWithPath("contact")
                                                        .type(JsonFieldType.STRING)
                                                        .description("회원 연락처")
                                                        .attributes(field("constraint", "문자열"))),
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
                                                fieldWithPath("result.memberBasicInformId")
                                                        .type(JsonFieldType.NUMBER)
                                                        .description("회원 기본 정보 ID"),
                                                fieldWithPath("result.memberName")
                                                        .type(JsonFieldType.STRING)
                                                        .description("회원 이름")
                                                        .attributes(field("constraint", "문자열")),
                                                fieldWithPath("result.emailId")
                                                        .type(JsonFieldType.STRING)
                                                        .description("유저 아이디")
                                                        .attributes(field("constraint", "문자열")),
                                                fieldWithPath("result.contact")
                                                        .type(JsonFieldType.STRING)
                                                        .description("회원 연락처")
                                                        .attributes(field("constraint", "문자열")),
                                                fieldWithPath("result.email")
                                                        .type(JsonFieldType.STRING)
                                                        .description("회원 이메일")
                                                        .attributes(field("constraint", "문자열")))))
                        .andReturn();

        // JSON 응답에서 result 객체를 추출 및 검증
        final String jsonResponse = mvcResult.getResponse().getContentAsString();
        final CommonResponse<MemberBasicInformResponseDTO.UpdateMemberBasicInformResponse> actual =
                objectMapper.readValue(
                        jsonResponse,
                        new TypeReference<
                                CommonResponse<
                                        MemberBasicInformResponseDTO
                                                .UpdateMemberBasicInformResponse>>() {});

        final CommonResponse<MemberBasicInformResponseDTO.UpdateMemberBasicInformResponse>
                expected = CommonResponse.onSuccess(updateMemberBasicInformResponse);

        // then
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @DisplayName("서비스 이용 동의 관련 항목 수정 요청할 수 있다.")
    @Test
    void updateConsentServiceUseResponse() throws Exception {
        // given
        final MemberBasicInformRequestDTO.UpdateConsentServiceUseRequest
                updateConsentServiceUseRequest =
                        new UpdateConsentServiceUseRequest(true, true, true, true);

        final MemberBasicInformResponseDTO.UpdateConsentServiceUseResponse
                updateConsentServiceUseResponse =
                        UpdateConsentServiceUseResponse.builder()
                                .memberBasicInformId(1L)
                                .isServiceUseAgree(true)
                                .isPrivateInformAgree(true)
                                .isAgeCheck(true)
                                .isMarketingAgree(true)
                                .emailId("kwondm7")
                                .memberName("회원 이름")
                                .build();

        // when
        when(memberService.updateConsentServiceUse(anyLong(), any()))
                .thenReturn(updateConsentServiceUseResponse);

        final ResultActions resultActions =
                performUpdateConsentServiceUse(updateConsentServiceUseRequest);

        // then
        final MvcResult mvcResult =
                resultActions
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.isSuccess").value("true"))
                        .andExpect(jsonPath("$.code").value("1000"))
                        .andExpect(jsonPath("$.message").value("요청에 성공하였습니다."))
                        .andDo(
                                restDocs.document(
                                        requestFields(
                                                fieldWithPath("isServiceUseAgree")
                                                        .type(JsonFieldType.BOOLEAN)
                                                        .description("서비스 이용약관 동의")
                                                        .attributes(field("constraint", "boolean")),
                                                fieldWithPath("isPrivateInformAgree")
                                                        .type(JsonFieldType.BOOLEAN)
                                                        .description("개인정보 수집 및 이용 동의")
                                                        .attributes(field("constraint", "boolean")),
                                                fieldWithPath("isAgeCheck")
                                                        .type(JsonFieldType.BOOLEAN)
                                                        .description("만 14세 이상 여부")
                                                        .attributes(field("constraint", "boolean")),
                                                fieldWithPath("isMarketingAgree")
                                                        .type(JsonFieldType.BOOLEAN)
                                                        .description("광고성 정보 수신 동의")
                                                        .attributes(
                                                                field("constraint", "boolean"))),
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
                                                fieldWithPath("result.memberBasicInformId")
                                                        .type(JsonFieldType.NUMBER)
                                                        .description("회원 기본 정보 ID"),
                                                fieldWithPath("result.isServiceUseAgree")
                                                        .type(JsonFieldType.BOOLEAN)
                                                        .description("서비스 이용약관 동의")
                                                        .attributes(field("constraint", "boolean")),
                                                fieldWithPath("result.isPrivateInformAgree")
                                                        .type(JsonFieldType.BOOLEAN)
                                                        .description("개인정보 수집 및 이용 동의")
                                                        .attributes(field("constraint", "boolean")),
                                                fieldWithPath("result.isAgeCheck")
                                                        .type(JsonFieldType.BOOLEAN)
                                                        .description("만 14세 이상 여부")
                                                        .attributes(field("constraint", "boolean")),
                                                fieldWithPath("result.isMarketingAgree")
                                                        .type(JsonFieldType.BOOLEAN)
                                                        .description("광고성 정보 수신 동의")
                                                        .attributes(field("constraint", "boolean")),
                                                fieldWithPath("result.emailId")
                                                        .type(JsonFieldType.STRING)
                                                        .description("회원 유저 아이디")
                                                        .attributes(field("constraint", "문자열")),
                                                fieldWithPath("result.memberName")
                                                        .type(JsonFieldType.STRING)
                                                        .description("회원 이름")
                                                        .attributes(field("constraint", "문자열")))))
                        .andReturn();

        // JSON 응답에서 result 객체를 추출 및 검증
        final String jsonResponse = mvcResult.getResponse().getContentAsString();
        final CommonResponse<MemberBasicInformResponseDTO.UpdateConsentServiceUseResponse> actual =
                objectMapper.readValue(
                        jsonResponse,
                        new TypeReference<
                                CommonResponse<
                                        MemberBasicInformResponseDTO
                                                .UpdateConsentServiceUseResponse>>() {});

        final CommonResponse<MemberBasicInformResponseDTO.UpdateConsentServiceUseResponse>
                expected = CommonResponse.onSuccess(updateConsentServiceUseResponse);

        // then
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @DisplayName("회원 기본 정보를 조회할 수 있다.")
    @Test
    void getMemberBasicInform() throws Exception {
        // given
        final MemberBasicInformResponseDTO.MemberBasicInformDetail memberBasicInformDetail =
                new MemberBasicInformDetail(
                        1L,
                        "권동민",
                        "01036614067",
                        "kwondm7@naver.com",
                        "kwondm7",
                        true,
                        true,
                        true,
                        true,
                        Platform.KAKAO);

        // when
        when(memberService.getMemberBasicInform(anyLong())).thenReturn(memberBasicInformDetail);

        final ResultActions resultActions = performGetMemberBasicInformDetail();

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
                                                fieldWithPath("result.memberBasicInformId")
                                                        .type(JsonFieldType.NUMBER)
                                                        .description("회원 기본 정보 ID"),
                                                fieldWithPath("result.memberName")
                                                        .type(JsonFieldType.STRING)
                                                        .description("회원 이름"),
                                                fieldWithPath("result.contact")
                                                        .type(JsonFieldType.STRING)
                                                        .description("회원 연락처"),
                                                fieldWithPath("result.email")
                                                        .type(JsonFieldType.STRING)
                                                        .description("회원 이메일"),
                                                fieldWithPath("result.emailId")
                                                        .type(JsonFieldType.STRING)
                                                        .description("회원 이메일 아이디"),
                                                fieldWithPath("result.isServiceUseAgree")
                                                        .type(JsonFieldType.BOOLEAN)
                                                        .description("서비스 이용약관 동의")
                                                        .attributes(field("constraint", "boolean")),
                                                fieldWithPath("result.isPrivateInformAgree")
                                                        .type(JsonFieldType.BOOLEAN)
                                                        .description("개인정보 수집 및 이용 동의")
                                                        .attributes(field("constraint", "boolean")),
                                                fieldWithPath("result.isAgeCheck")
                                                        .type(JsonFieldType.BOOLEAN)
                                                        .description("만 14세 이상 여부")
                                                        .attributes(field("constraint", "boolean")),
                                                fieldWithPath("result.isMarketingAgree")
                                                        .type(JsonFieldType.BOOLEAN)
                                                        .description("광고성 정보 수신 동의")
                                                        .attributes(field("constraint", "boolean")),
                                                fieldWithPath("result.platform")
                                                        .type(JsonFieldType.STRING)
                                                        .description("플랫폼 이름")
                                                        .attributes(field("constraint", "문자열")))))
                        .andReturn();

        // JSON 응답에서 result 객체를 추출 및 검증
        final String jsonResponse = mvcResult.getResponse().getContentAsString();
        final CommonResponse<MemberBasicInformResponseDTO.MemberBasicInformDetail> actual =
                objectMapper.readValue(
                        jsonResponse,
                        new TypeReference<
                                CommonResponse<
                                        MemberBasicInformResponseDTO
                                                .MemberBasicInformDetail>>() {});

        final CommonResponse<MemberBasicInformResponseDTO.MemberBasicInformDetail> expected =
                CommonResponse.onSuccess(memberBasicInformDetail);

        // then
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @DisplayName("회원 이름 정보를 수정 요청할 수 있다.")
    @Test
    void updateMemberName() throws Exception {
        // given
        final MemberBasicInformRequestDTO.UpdateMemberNameRequest updateMemberNameRequest =
                new UpdateMemberNameRequest("권동민");

        final MemberBasicInformResponseDTO.UpdateMemberNameResponse updateMemberNameResponse =
                new UpdateMemberNameResponse("권동민");

        // when
        when(memberService.updateMemberName(anyLong(), any())).thenReturn(updateMemberNameResponse);

        final ResultActions resultActions = performUpdateMemberName(updateMemberNameRequest);

        // then
        final MvcResult mvcResult =
                resultActions
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.isSuccess").value("true"))
                        .andExpect(jsonPath("$.code").value("1000"))
                        .andExpect(jsonPath("$.message").value("요청에 성공하였습니다."))
                        .andDo(
                                restDocs.document(
                                        requestFields(
                                                fieldWithPath("memberName")
                                                        .type(JsonFieldType.STRING)
                                                        .description("회원 이름")
                                                        .attributes(field("constraint", "문자열"))),
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
                                                fieldWithPath("result.memberName")
                                                        .type(JsonFieldType.STRING)
                                                        .description("회원 이름")
                                                        .attributes(field("constraint", "문자열")))))
                        .andReturn();

        // JSON 응답에서 result 객체를 추출 및 검증
        final String jsonResponse = mvcResult.getResponse().getContentAsString();
        final CommonResponse<MemberBasicInformResponseDTO.UpdateMemberNameResponse> actual =
                objectMapper.readValue(
                        jsonResponse,
                        new TypeReference<
                                CommonResponse<
                                        MemberBasicInformResponseDTO
                                                .UpdateMemberNameResponse>>() {});

        final CommonResponse<MemberBasicInformResponseDTO.UpdateMemberNameResponse> expected =
                CommonResponse.onSuccess(updateMemberNameResponse);

        // then
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @DisplayName("회원 유저 아이디 정보를 수정 요청할 수 있다.")
    @Test
    void updateMemberUserId() throws Exception {
        // given
        final MemberRequestDTO.UpdateMemberUserIdRequest updateMemberUserIdRequest =
                UpdateMemberUserIdRequest.builder().emailId("변경하고자 하는 유저 아이디").build();

        final MemberResponseDTO.UpdateMemberUserIdResponse updateMemberUserIdResponse =
                UpdateMemberUserIdResponse.builder().emailId("변경된 유저 아이디").build();

        // when
        when(memberService.updateMemberUserId(anyLong(), any()))
                .thenReturn(updateMemberUserIdResponse);

        final ResultActions resultActions = performUpdateMemberUserId(updateMemberUserIdRequest);

        // then
        final MvcResult mvcResult =
                resultActions
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.isSuccess").value("true"))
                        .andExpect(jsonPath("$.code").value("1000"))
                        .andExpect(jsonPath("$.message").value("요청에 성공하였습니다."))
                        .andDo(
                                restDocs.document(
                                        requestFields(
                                                fieldWithPath("emailId")
                                                        .type(JsonFieldType.STRING)
                                                        .description("회원 유저 아이디")
                                                        .attributes(field("constraint", "문자열"))),
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
                                                fieldWithPath("result.emailId")
                                                        .type(JsonFieldType.STRING)
                                                        .description("회원 유저 아이디")
                                                        .attributes(field("constraint", "문자열")))))
                        .andReturn();

        // JSON 응답에서 result 객체를 추출 및 검증
        final String jsonResponse = mvcResult.getResponse().getContentAsString();
        final CommonResponse<UpdateMemberUserIdResponse> actual =
                objectMapper.readValue(
                        jsonResponse,
                        new TypeReference<CommonResponse<UpdateMemberUserIdResponse>>() {});

        final CommonResponse<UpdateMemberUserIdResponse> expected =
                CommonResponse.onSuccess(updateMemberUserIdResponse);

        // then
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @DisplayName("회원 연락처 정보를 수정 요청할 수 있다.")
    @Test
    void updateMemberContact() throws Exception {
        // given
        final MemberBasicInformRequestDTO.UpdateMemberContactRequest updateMemberContactRequest =
                new UpdateMemberContactRequest("수정 전화번호");

        final MemberBasicInformResponseDTO.UpdateMemberContactResponse updateMemberContactResponse =
                new UpdateMemberContactResponse("수정 전화번호");

        // when
        when(memberService.updateMemberContact(anyLong(), any()))
                .thenReturn(updateMemberContactResponse);

        final ResultActions resultActions = performUpdateMemberContact(updateMemberContactRequest);

        // then
        final MvcResult mvcResult =
                resultActions
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.isSuccess").value("true"))
                        .andExpect(jsonPath("$.code").value("1000"))
                        .andExpect(jsonPath("$.message").value("요청에 성공하였습니다."))
                        .andDo(
                                restDocs.document(
                                        requestFields(
                                                fieldWithPath("contact")
                                                        .type(JsonFieldType.STRING)
                                                        .description("회원 이름")
                                                        .attributes(field("constraint", "문자열"))),
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
                                                fieldWithPath("result.contact")
                                                        .type(JsonFieldType.STRING)
                                                        .description("회원 연락처")
                                                        .attributes(field("constraint", "문자열")))))
                        .andReturn();

        // JSON 응답에서 result 객체를 추출 및 검증
        final String jsonResponse = mvcResult.getResponse().getContentAsString();
        final CommonResponse<MemberBasicInformResponseDTO.UpdateMemberContactResponse> actual =
                objectMapper.readValue(
                        jsonResponse,
                        new TypeReference<
                                CommonResponse<
                                        MemberBasicInformResponseDTO
                                                .UpdateMemberContactResponse>>() {});

        final CommonResponse<MemberBasicInformResponseDTO.UpdateMemberContactResponse> expected =
                CommonResponse.onSuccess(updateMemberContactResponse);

        // then
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @DisplayName("회원 마케팅 동의 정보를 수정 요청할 수 있다.")
    @Test
    void updateConsentMarketing() throws Exception {
        // given
        final MemberBasicInformRequestDTO.UpdateConsentMarketingRequest
                updateConsentMarketingRequest = new UpdateConsentMarketingRequest(true);

        final MemberBasicInformResponseDTO.UpdateConsentMarketingResponse
                updateConsentMarketingResponse = new UpdateConsentMarketingResponse(true);

        // when
        when(memberService.updateConsentMarketing(anyLong(), any()))
                .thenReturn(updateConsentMarketingResponse);

        final ResultActions resultActions =
                performUpdateConsentMarketing(updateConsentMarketingRequest);

        // then
        final MvcResult mvcResult =
                resultActions
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.isSuccess").value("true"))
                        .andExpect(jsonPath("$.code").value("1000"))
                        .andExpect(jsonPath("$.message").value("요청에 성공하였습니다."))
                        .andDo(
                                restDocs.document(
                                        requestFields(
                                                fieldWithPath("isMarketingAgree")
                                                        .type(JsonFieldType.BOOLEAN)
                                                        .description("광고성 정보 수신 동의")
                                                        .attributes(
                                                                field("constraint", "boolean"))),
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
                                                fieldWithPath("result.isMarketingAgree")
                                                        .type(JsonFieldType.BOOLEAN)
                                                        .description("광고성 정보 수신 동의")
                                                        .attributes(
                                                                field("constraint", "boolean")))))
                        .andReturn();

        // JSON 응답에서 result 객체를 추출 및 검증
        final String jsonResponse = mvcResult.getResponse().getContentAsString();
        final CommonResponse<MemberBasicInformResponseDTO.UpdateConsentMarketingResponse> actual =
                objectMapper.readValue(
                        jsonResponse,
                        new TypeReference<
                                CommonResponse<
                                        MemberBasicInformResponseDTO
                                                .UpdateConsentMarketingResponse>>() {});

        final CommonResponse<MemberBasicInformResponseDTO.UpdateConsentMarketingResponse> expected =
                CommonResponse.onSuccess(updateConsentMarketingResponse);

        // then
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @DisplayName("회원은 이메일 재인증을 할 수 있다.")
    @Test
    void reAuthenticationEmail() throws Exception {
        // given
        given(refreshTokenRepository.existsById(any())).willReturn(true);
        doNothing().when(jwtProvider).validateTokens(any());
        given(jwtProvider.getSubject(any())).willReturn("1");

        final MemberTokens memberTokens = new MemberTokens(REFRESH_TOKEN, RENEW_ACCESS_TOKEN);
        final Cookie cookie = new Cookie("refreshToken", memberTokens.getRefreshToken());

        final MailReAuthenticationRequest mailReAuthenticationRequest =
                new MailReAuthenticationRequest("kwondm7@gmail.com");

        final MemberBasicInformResponseDTO.MailReAuthenticationResponse
                mailReAuthenticationResponse =
                        new MailReAuthenticationResponse(LocalDateTime.now());

        // when
        when(memberService.reAuthenticationEmail(anyLong(), any()))
                .thenReturn(mailReAuthenticationResponse);

        final ResultActions resultActions =
                mockMvc.perform(
                        RestDocumentationRequestBuilders.post(
                                        "/api/v1/member/email/re-authentication")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(
                                                mailReAuthenticationRequest)));

        final MvcResult mvcResult =
                resultActions
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.isSuccess").value("true"))
                        .andExpect(jsonPath("$.code").value("1000"))
                        .andExpect(jsonPath("$.message").value("요청에 성공하였습니다."))
                        .andDo(
                                restDocs.document(
                                        requestFields(
                                                fieldWithPath("email")
                                                        .type(JsonFieldType.STRING)
                                                        .description("변경하고자 하는 타겟 이메일")
                                                        .attributes(field("constraint", "문자열"))),
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
                                                fieldWithPath("result.reAuthenticationEmailSendAt")
                                                        .type(JsonFieldType.STRING)
                                                        .description("재인증 이메일 발송 시각")
                                                        .attributes(
                                                                field(
                                                                        "constraint",
                                                                        "LocalDateTime")))))
                        .andReturn();

        final String jsonResponse = mvcResult.getResponse().getContentAsString();
        final CommonResponse<MailReAuthenticationResponse> actual =
                objectMapper.readValue(
                        jsonResponse,
                        new TypeReference<CommonResponse<MailReAuthenticationResponse>>() {});

        final CommonResponse<MailReAuthenticationResponse> expected =
                CommonResponse.onSuccess(mailReAuthenticationResponse);

        // then
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
        verify(memberService).reAuthenticationEmail(anyLong(), any());
    }

    @DisplayName("회원은 인증코드를 입력하여 이메일 변경을 진행할 수 있다.")
    @Test
    void verificationAuthCode() throws Exception {
        // given
        given(refreshTokenRepository.existsById(any())).willReturn(true);
        doNothing().when(jwtProvider).validateTokens(any());
        given(jwtProvider.getSubject(any())).willReturn("1");

        final MemberTokens memberTokens = new MemberTokens(REFRESH_TOKEN, RENEW_ACCESS_TOKEN);
        final Cookie cookie = new Cookie("refreshToken", memberTokens.getRefreshToken());

        final AuthCodeVerificationRequest authCodeVerificationRequest =
                new AuthCodeVerificationRequest("kwondm7@gmail.com", "123456");
        final MailVerificationResponse mailVerificationResponse =
                new MailVerificationResponse("kwondm7@gmail.com", LocalDateTime.now());

        // when
        when(memberService.verifyAuthCodeAndChangeAccountEmail(anyLong(), any()))
                .thenReturn(mailVerificationResponse);

        final ResultActions resultActions =
                mockMvc.perform(
                        RestDocumentationRequestBuilders.post("/api/v1/member/email/verification")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(
                                                authCodeVerificationRequest)));

        final MvcResult mvcResult =
                resultActions
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.isSuccess").value("true"))
                        .andExpect(jsonPath("$.code").value("1000"))
                        .andExpect(jsonPath("$.message").value("요청에 성공하였습니다."))
                        .andDo(
                                restDocs.document(
                                        requestFields(
                                                fieldWithPath("changeRequestEmail")
                                                        .type(JsonFieldType.STRING)
                                                        .description("변경하고자 하는 타겟 이메일")
                                                        .attributes(field("constraint", "문자열")),
                                                fieldWithPath("authCode")
                                                        .type(JsonFieldType.STRING)
                                                        .description("인증 코드")
                                                        .attributes(field("constraint", "문자열"))),
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
                                                fieldWithPath("result.changedEmail")
                                                        .type(JsonFieldType.STRING)
                                                        .description("변경된 이메일")
                                                        .attributes(field("constraint", "문자열")),
                                                fieldWithPath("result.verificationSuccessAt")
                                                        .type(JsonFieldType.STRING)
                                                        .description("변경 성공 시각")
                                                        .attributes(
                                                                field(
                                                                        "constraint",
                                                                        "LocalDateTime")))))
                        .andReturn();

        final String jsonResponse = mvcResult.getResponse().getContentAsString();
        final CommonResponse<MailVerificationResponse> actual =
                objectMapper.readValue(
                        jsonResponse,
                        new TypeReference<CommonResponse<MailVerificationResponse>>() {});

        final CommonResponse<MailVerificationResponse> expected =
                CommonResponse.onSuccess(mailVerificationResponse);

        // then
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
        verify(memberService).verifyAuthCodeAndChangeAccountEmail(anyLong(), any());
    }
}
