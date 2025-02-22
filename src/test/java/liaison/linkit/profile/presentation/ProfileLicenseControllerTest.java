package liaison.linkit.profile.presentation;

import static liaison.linkit.global.restdocs.RestDocsConfiguration.field;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;

import jakarta.servlet.http.Cookie;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import groovy.util.logging.Slf4j;
import liaison.linkit.common.presentation.CommonResponse;
import liaison.linkit.global.ControllerTest;
import liaison.linkit.login.domain.MemberTokens;
import liaison.linkit.profile.business.service.ProfileLicenseService;
import liaison.linkit.profile.presentation.license.ProfileLicenseController;
import liaison.linkit.profile.presentation.license.dto.ProfileLicenseRequestDTO;
import liaison.linkit.profile.presentation.license.dto.ProfileLicenseRequestDTO.AddProfileLicenseRequest;
import liaison.linkit.profile.presentation.license.dto.ProfileLicenseRequestDTO.UpdateProfileLicenseRequest;
import liaison.linkit.profile.presentation.license.dto.ProfileLicenseResponseDTO;
import liaison.linkit.profile.presentation.license.dto.ProfileLicenseResponseDTO.AddProfileLicenseResponse;
import liaison.linkit.profile.presentation.license.dto.ProfileLicenseResponseDTO.ProfileLicenseCertificationResponse;
import liaison.linkit.profile.presentation.license.dto.ProfileLicenseResponseDTO.ProfileLicenseDetail;
import liaison.linkit.profile.presentation.license.dto.ProfileLicenseResponseDTO.ProfileLicenseItem;
import liaison.linkit.profile.presentation.license.dto.ProfileLicenseResponseDTO.ProfileLicenseItems;
import liaison.linkit.profile.presentation.license.dto.ProfileLicenseResponseDTO.RemoveProfileLicenseCertificationResponse;
import liaison.linkit.profile.presentation.license.dto.ProfileLicenseResponseDTO.RemoveProfileLicenseResponse;
import liaison.linkit.profile.presentation.license.dto.ProfileLicenseResponseDTO.UpdateProfileLicenseResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(ProfileLicenseController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
@Slf4j
public class ProfileLicenseControllerTest extends ControllerTest {
    private static final MemberTokens MEMBER_TOKENS =
            new MemberTokens("accessToken", "refreshToken");
    private static final Cookie COOKIE =
            new Cookie("refreshToken", MEMBER_TOKENS.getRefreshToken());

    @Autowired private ObjectMapper objectMapper;

    @MockBean private ProfileLicenseService profileLicenseService;

    @BeforeEach
    void setUp() {
        given(refreshTokenRepository.existsById(any())).willReturn(true);
        doNothing().when(jwtProvider).validateTokens(any());
        given(jwtProvider.getSubject(any())).willReturn("1");
    }

    private ResultActions performGetProfileLicenseItems() throws Exception {
        return mockMvc.perform(
                get("/api/v1/profile/license")
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE));
    }

    private ResultActions performGetProfileLicenseDetail(final Long profileLicenseId)
            throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders.get(
                                "/api/v1/profile/license/{profileLicenseId}", profileLicenseId)
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE));
    }

    private ResultActions performAddProfileLicense(final AddProfileLicenseRequest request)
            throws Exception {
        return mockMvc.perform(
                post("/api/v1/profile/license")
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));
    }

    private ResultActions performUpdateProfileLicense(
            final Long profileLicenseId, final UpdateProfileLicenseRequest request)
            throws Exception {
        return mockMvc.perform(
                post("/api/v1/profile/license/{profileLicenseId}", profileLicenseId)
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));
    }

    private ResultActions performRemoveProfileLicense(final Long profileLicenseId)
            throws Exception {
        return mockMvc.perform(
                delete("/api/v1/profile/license/{profileLicenseId}", profileLicenseId)
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE));
    }

    private ResultActions performRemoveLicenseCertification(final Long profileLicenseId)
            throws Exception {
        return mockMvc.perform(
                delete("/api/v1/profile/license/certification/{profileLicenseId}", profileLicenseId)
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE));
    }

    @DisplayName("회원이 나의 자격증을 전체 조회할 수 있다.")
    @Test
    void getProfileLicenseItems() throws Exception {
        // given
        final ProfileLicenseResponseDTO.ProfileLicenseItem firstProfileLicenseItem =
                new ProfileLicenseItem(
                        1L, "자격증 자격명 1", "자격증 관련 부처 1", "자격증 취득 시기 1", true, "자격증 설명 1");

        final ProfileLicenseResponseDTO.ProfileLicenseItem secondProfileLicenseItem =
                new ProfileLicenseItem(
                        2L, "자격증 자격명 2", "자격증 관련 부처 2", "자격증 취득 시기 2", true, "자격증 설명 2");

        final ProfileLicenseResponseDTO.ProfileLicenseItems profileLicenseItems =
                new ProfileLicenseItems(
                        Arrays.asList(firstProfileLicenseItem, secondProfileLicenseItem));

        // when
        when(profileLicenseService.getProfileLicenseItems(anyLong()))
                .thenReturn(profileLicenseItems);

        final ResultActions resultActions = performGetProfileLicenseItems();

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
                                                subsectionWithPath("result.profileLicenseItems[]")
                                                        .type(JsonFieldType.ARRAY)
                                                        .description("프로필 자격증 아이템 배열"),
                                                fieldWithPath(
                                                                "result.profileLicenseItems[].profileLicenseId")
                                                        .type(JsonFieldType.NUMBER)
                                                        .description("내 자격증 ID"),
                                                fieldWithPath(
                                                                "result.profileLicenseItems[].licenseName")
                                                        .type(JsonFieldType.STRING)
                                                        .description("자격증 자격명"),
                                                fieldWithPath(
                                                                "result.profileLicenseItems[].licenseInstitution")
                                                        .type(JsonFieldType.STRING)
                                                        .description("자격증 관련 부처"),
                                                fieldWithPath(
                                                                "result.profileLicenseItems[].licenseAcquisitionDate")
                                                        .type(JsonFieldType.STRING)
                                                        .description("자격증 취득 시기"),
                                                fieldWithPath(
                                                                "result.profileLicenseItems[].isLicenseVerified")
                                                        .type(JsonFieldType.BOOLEAN)
                                                        .description("자격증 증명서 인증 완료 여부"))))
                        .andReturn();

        // JSON 응답에서 result 객체를 추출 및 검증
        final String jsonResponse = mvcResult.getResponse().getContentAsString();
        final CommonResponse<ProfileLicenseItems> actual =
                objectMapper.readValue(
                        jsonResponse, new TypeReference<CommonResponse<ProfileLicenseItems>>() {});

        final CommonResponse<ProfileLicenseItems> expected =
                CommonResponse.onSuccess(profileLicenseItems);

        // then
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @DisplayName("회원이 나의 자격증을 상세 조회할 수 있다.")
    @Test
    void getProfileLicenseDetail() throws Exception {
        // given
        final ProfileLicenseResponseDTO.ProfileLicenseDetail profileLicenseDetail =
                new ProfileLicenseDetail(
                        1L,
                        "자격증 자격이름",
                        "자격증 관련 부처",
                        "자격증 취득 시기",
                        "자격증 설명",
                        true,
                        false,
                        "증명서.pdf",
                        "https://file.linkit.im/해시값.pdf");

        // when
        when(profileLicenseService.getProfileLicenseDetail(anyLong(), anyLong()))
                .thenReturn(profileLicenseDetail);

        final ResultActions resultActions = performGetProfileLicenseDetail(1L);

        // then
        final MvcResult mvcResult =
                resultActions
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.isSuccess").value("true"))
                        .andExpect(jsonPath("$.code").value("1000"))
                        .andExpect(jsonPath("$.message").value("요청에 성공하였습니다."))
                        .andDo(
                                restDocs.document(
                                        pathParameters(
                                                parameterWithName("profileLicenseId")
                                                        .description("프로필 자격증 ID")),
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
                                                fieldWithPath("result.profileLicenseId")
                                                        .type(JsonFieldType.NUMBER)
                                                        .description("프로필 자격증 ID"),
                                                fieldWithPath("result.licenseName")
                                                        .type(JsonFieldType.STRING)
                                                        .description("자격증 활동명"),
                                                fieldWithPath("result.licenseInstitution")
                                                        .type(JsonFieldType.STRING)
                                                        .description("자격증 관련 부처"),
                                                fieldWithPath("result.licenseAcquisitionDate")
                                                        .type(JsonFieldType.STRING)
                                                        .description("자격증 취득 시기"),
                                                fieldWithPath("result.licenseDescription")
                                                        .type(JsonFieldType.STRING)
                                                        .description("자격증 설명"),
                                                fieldWithPath("result.isLicenseCertified")
                                                        .type(JsonFieldType.BOOLEAN)
                                                        .description("증명서 존재 여부"),
                                                fieldWithPath("result.isLicenseVerified")
                                                        .type(JsonFieldType.BOOLEAN)
                                                        .description("증명서 인증 여부"),
                                                fieldWithPath(
                                                                "result.licenseCertificationAttachFileName")
                                                        .type(JsonFieldType.STRING)
                                                        .description("증명서 파일 이름"),
                                                fieldWithPath(
                                                                "result.licenseCertificationAttachFilePath")
                                                        .type(JsonFieldType.STRING)
                                                        .description("증명서 파일 경로"))))
                        .andReturn();
    }

    @DisplayName("회원이 나의 자격증을 생성할 수 있다.")
    @Test
    void addProfileLicense() throws Exception {
        // given
        final ProfileLicenseRequestDTO.AddProfileLicenseRequest addProfileLicenseRequest =
                new AddProfileLicenseRequest("자격증 자격명", "자격증 관련 부처", "자격증 취득 시기", "자격증 설명");

        final AddProfileLicenseResponse addProfileLicenseResponse =
                new AddProfileLicenseResponse(1L, "자격증 자격명", "자격증 관련 부처", "자격증 취득 시기", "자격증 설명");

        // when
        when(profileLicenseService.addProfileLicense(anyLong(), any()))
                .thenReturn(addProfileLicenseResponse);

        final ResultActions resultActions = performAddProfileLicense(addProfileLicenseRequest);

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
                                                fieldWithPath("licenseName")
                                                        .type(JsonFieldType.STRING)
                                                        .description("자격증 자격명"),
                                                fieldWithPath("licenseInstitution")
                                                        .type(JsonFieldType.STRING)
                                                        .description("자격증 관련 부처"),
                                                fieldWithPath("licenseAcquisitionDate")
                                                        .type(JsonFieldType.STRING)
                                                        .description("자격증 취득 시기"),
                                                fieldWithPath("licenseDescription")
                                                        .type(JsonFieldType.STRING)
                                                        .description("자격증 설명")),
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
                                                fieldWithPath("result.profileLicenseId")
                                                        .type(JsonFieldType.NUMBER)
                                                        .description("프로필 자격증 ID"),
                                                fieldWithPath("result.licenseName")
                                                        .type(JsonFieldType.STRING)
                                                        .description("자격증 활동명"),
                                                fieldWithPath("result.licenseInstitution")
                                                        .type(JsonFieldType.STRING)
                                                        .description("자격증 관련 부처"),
                                                fieldWithPath("result.licenseAcquisitionDate")
                                                        .type(JsonFieldType.STRING)
                                                        .description("자격증 취득 시기"),
                                                fieldWithPath("result.licenseDescription")
                                                        .type(JsonFieldType.STRING)
                                                        .description("자격증 설명"))))
                        .andReturn();

        // JSON 응답에서 result 객체를 추출 및 검증
        final String jsonResponse = mvcResult.getResponse().getContentAsString();
        final CommonResponse<AddProfileLicenseResponse> actual =
                objectMapper.readValue(
                        jsonResponse,
                        new TypeReference<CommonResponse<AddProfileLicenseResponse>>() {});

        final CommonResponse<AddProfileLicenseResponse> expected =
                CommonResponse.onSuccess(addProfileLicenseResponse);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @DisplayName("회원이 나의 자격증을 수정할 수 있다.")
    @Test
    void updateProfileLicense() throws Exception {
        // given

        final ProfileLicenseRequestDTO.UpdateProfileLicenseRequest updateProfileLicenseRequest =
                new UpdateProfileLicenseRequest(
                        "자격증 자격명 2", "자격증 관련 부처 2", "자격증 취득 시기 2", "자격증 설명 2");

        final UpdateProfileLicenseResponse updateProfileLicenseResponse =
                new UpdateProfileLicenseResponse(
                        2L, "자격증 자격명 2", "자격증 관련 부처 2", "자격증 취득 시기 2", "자격증 설명 2");

        // when
        when(profileLicenseService.updateProfileLicense(anyLong(), anyLong(), any()))
                .thenReturn(updateProfileLicenseResponse);

        final ResultActions resultActions =
                performUpdateProfileLicense(1L, updateProfileLicenseRequest);

        // then
        final MvcResult mvcResult =
                resultActions
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.isSuccess").value("true"))
                        .andExpect(jsonPath("$.code").value("1000"))
                        .andExpect(jsonPath("$.message").value("요청에 성공하였습니다."))
                        .andDo(
                                restDocs.document(
                                        pathParameters(
                                                parameterWithName("profileLicenseId")
                                                        .description("프로필 자격증 ID")),
                                        requestFields(
                                                fieldWithPath("licenseName")
                                                        .type(JsonFieldType.STRING)
                                                        .description("자격증 자격명"),
                                                fieldWithPath("licenseInstitution")
                                                        .type(JsonFieldType.STRING)
                                                        .description("자격증 관련 부처"),
                                                fieldWithPath("licenseAcquisitionDate")
                                                        .type(JsonFieldType.STRING)
                                                        .description("자격증 취득 시기"),
                                                fieldWithPath("licenseDescription")
                                                        .type(JsonFieldType.STRING)
                                                        .description("자격증 설명")),
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
                                                fieldWithPath("result.profileLicenseId")
                                                        .type(JsonFieldType.NUMBER)
                                                        .description("프로필 자격증 ID"),
                                                fieldWithPath("result.licenseName")
                                                        .type(JsonFieldType.STRING)
                                                        .description("자격증 활동명"),
                                                fieldWithPath("result.licenseInstitution")
                                                        .type(JsonFieldType.STRING)
                                                        .description("자격증 관련 부처"),
                                                fieldWithPath("result.licenseAcquisitionDate")
                                                        .type(JsonFieldType.STRING)
                                                        .description("자격증 취득 시기"),
                                                fieldWithPath("result.licenseDescription")
                                                        .type(JsonFieldType.STRING)
                                                        .description("자격증 설명"))))
                        .andReturn();

        // JSON 응답에서 result 객체를 추출 및 검증
        final String jsonResponse = mvcResult.getResponse().getContentAsString();
        final CommonResponse<UpdateProfileLicenseResponse> actual =
                objectMapper.readValue(
                        jsonResponse,
                        new TypeReference<CommonResponse<UpdateProfileLicenseResponse>>() {});

        final CommonResponse<UpdateProfileLicenseResponse> expected =
                CommonResponse.onSuccess(updateProfileLicenseResponse);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @DisplayName("회원이 나의 자격증을 삭제할 수 있다.")
    @Test
    void removeProfileLicense() throws Exception {
        // given
        final ProfileLicenseResponseDTO.RemoveProfileLicenseResponse removeProfileLicenseResponse =
                new RemoveProfileLicenseResponse(1L);
        // when
        when(profileLicenseService.removeProfileLicense(anyLong(), anyLong()))
                .thenReturn(removeProfileLicenseResponse);

        final ResultActions resultActions = performRemoveProfileLicense(1L);

        // then
        final MvcResult mvcResult =
                resultActions
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.isSuccess").value("true"))
                        .andExpect(jsonPath("$.code").value("1000"))
                        .andExpect(jsonPath("$.message").value("요청에 성공하였습니다."))
                        .andDo(
                                restDocs.document(
                                        pathParameters(
                                                parameterWithName("profileLicenseId")
                                                        .description("프로필 자격증 ID")),
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
                                                fieldWithPath("result.profileLicenseId")
                                                        .type(JsonFieldType.NUMBER)
                                                        .description("프로필 자격증 ID"))))
                        .andReturn();

        // JSON 응답에서 result 객체를 추출 및 검증
        final String jsonResponse = mvcResult.getResponse().getContentAsString();
        final CommonResponse<RemoveProfileLicenseResponse> actual =
                objectMapper.readValue(
                        jsonResponse,
                        new TypeReference<CommonResponse<RemoveProfileLicenseResponse>>() {});

        final CommonResponse<RemoveProfileLicenseResponse> expected =
                CommonResponse.onSuccess(removeProfileLicenseResponse);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @DisplayName("회원이 나의 자격증 증명서를 추가할 수 있다.")
    @Test
    void addProfileLicenseCertification() throws Exception {
        // given
        final ProfileLicenseCertificationResponse profileLicenseCertificationResponse =
                new ProfileLicenseCertificationResponse(
                        true, false, "증명서.pdf", "https://file.linkit.im/해시값.pdf");

        final MockMultipartFile profileLicenseCertificationFile =
                new MockMultipartFile(
                        "profileLicenseCertificationFile",
                        "증명서.pdf",
                        "multipart/form-data",
                        "./src/test/resources/static/증명서.pdf".getBytes());

        final Long profileLicenseId = 1L;

        // when
        when(profileLicenseService.addProfileLicenseCertification(anyLong(), anyLong(), any()))
                .thenReturn(profileLicenseCertificationResponse);

        final ResultActions resultActions =
                mockMvc.perform(
                        RestDocumentationRequestBuilders.multipart(
                                        "/api/v1/profile/license/certification/{profileLicenseId}",
                                        profileLicenseId)
                                .file(profileLicenseCertificationFile)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .characterEncoding("UTF-8")
                                .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                                .cookie(COOKIE));

        // then
        final MvcResult mvcResult =
                resultActions
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.isSuccess").value("true"))
                        .andExpect(jsonPath("$.code").value("1000"))
                        .andExpect(jsonPath("$.message").value("요청에 성공하였습니다."))
                        .andDo(
                                restDocs.document(
                                        pathParameters(
                                                parameterWithName("profileLicenseId")
                                                        .description("프로필 자격증 ID")),
                                        requestParts(
                                                partWithName("profileLicenseCertificationFile")
                                                        .description("자격증 증명 파일")),
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
                                                fieldWithPath("result.isLicenseCertified")
                                                        .type(JsonFieldType.BOOLEAN)
                                                        .description("증명서 존재 여부"),
                                                fieldWithPath("result.isLicenseVerified")
                                                        .type(JsonFieldType.BOOLEAN)
                                                        .description("증명서 인증 여부"),
                                                fieldWithPath(
                                                                "result.licenseCertificationAttachFileName")
                                                        .type(JsonFieldType.STRING)
                                                        .description("증명서 파일 이름"),
                                                fieldWithPath(
                                                                "result.licenseCertificationAttachFilePath")
                                                        .type(JsonFieldType.STRING)
                                                        .description("증명서 파일 경로"))))
                        .andReturn();

        // JSON 응답에서 result 객체를 추출 및 검증
        final String jsonResponse = mvcResult.getResponse().getContentAsString();
        final CommonResponse<ProfileLicenseCertificationResponse> actual =
                objectMapper.readValue(
                        jsonResponse,
                        new TypeReference<
                                CommonResponse<ProfileLicenseCertificationResponse>>() {});

        final CommonResponse<ProfileLicenseCertificationResponse> expected =
                CommonResponse.onSuccess(profileLicenseCertificationResponse);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @DisplayName("회원이 나의 자격증 증명서를 제거할 수 있다.")
    @Test
    void removeProfileLicenseCertification() throws Exception {
        // given
        final ProfileLicenseResponseDTO.RemoveProfileLicenseCertificationResponse
                removeProfileLicenseCertificationResponse =
                        new RemoveProfileLicenseCertificationResponse(1L);

        // when
        when(profileLicenseService.removeProfileLicenseCertification(anyLong(), anyLong()))
                .thenReturn(removeProfileLicenseCertificationResponse);

        final ResultActions resultActions = performRemoveLicenseCertification(1L);

        // then
        final MvcResult mvcResult =
                resultActions
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.isSuccess").value("true"))
                        .andExpect(jsonPath("$.code").value("1000"))
                        .andExpect(jsonPath("$.message").value("요청에 성공하였습니다."))
                        .andDo(
                                restDocs.document(
                                        pathParameters(
                                                parameterWithName("profileLicenseId")
                                                        .description("프로필 자격증 ID")),
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
                                                fieldWithPath("result.profileLicenseId")
                                                        .type(JsonFieldType.NUMBER)
                                                        .description("프로필 자격증 ID"))))
                        .andReturn();

        // JSON 응답에서 result 객체를 추출 및 검증
        final String jsonResponse = mvcResult.getResponse().getContentAsString();
        final CommonResponse<RemoveProfileLicenseCertificationResponse> actual =
                objectMapper.readValue(
                        jsonResponse,
                        new TypeReference<
                                CommonResponse<RemoveProfileLicenseCertificationResponse>>() {});

        final CommonResponse<RemoveProfileLicenseCertificationResponse> expected =
                CommonResponse.onSuccess(removeProfileLicenseCertificationResponse);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }
}
