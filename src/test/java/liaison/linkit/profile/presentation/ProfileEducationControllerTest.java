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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import java.util.Arrays;
import liaison.linkit.common.presentation.CommonResponse;
import liaison.linkit.global.ControllerTest;
import liaison.linkit.login.domain.MemberTokens;
import liaison.linkit.profile.presentation.education.ProfileEducationController;
import liaison.linkit.profile.presentation.education.dto.ProfileEducationRequestDTO;
import liaison.linkit.profile.presentation.education.dto.ProfileEducationRequestDTO.AddProfileEducationRequest;
import liaison.linkit.profile.presentation.education.dto.ProfileEducationRequestDTO.UpdateProfileEducationRequest;
import liaison.linkit.profile.presentation.education.dto.ProfileEducationResponseDTO;
import liaison.linkit.profile.presentation.education.dto.ProfileEducationResponseDTO.AddProfileEducationResponse;
import liaison.linkit.profile.presentation.education.dto.ProfileEducationResponseDTO.ProfileEducationCertificationResponse;
import liaison.linkit.profile.presentation.education.dto.ProfileEducationResponseDTO.ProfileEducationDetail;
import liaison.linkit.profile.presentation.education.dto.ProfileEducationResponseDTO.ProfileEducationItem;
import liaison.linkit.profile.presentation.education.dto.ProfileEducationResponseDTO.RemoveProfileEducationCertificationResponse;
import liaison.linkit.profile.presentation.education.dto.ProfileEducationResponseDTO.RemoveProfileEducationResponse;
import liaison.linkit.profile.presentation.education.dto.ProfileEducationResponseDTO.UpdateProfileEducationResponse;
import liaison.linkit.profile.service.ProfileEducationService;
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

@WebMvcTest(ProfileEducationController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
public class ProfileEducationControllerTest extends ControllerTest {
    private static final MemberTokens MEMBER_TOKENS = new MemberTokens("accessToken", "refreshToken");
    private static final Cookie COOKIE = new Cookie("refreshToken", MEMBER_TOKENS.getRefreshToken());

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProfileEducationService profileEducationService;

    @BeforeEach
    void setUp() {
        given(refreshTokenRepository.existsById(any())).willReturn(true);
        doNothing().when(jwtProvider).validateTokens(any());
        given(jwtProvider.getSubject(any())).willReturn("1");
    }

    private ResultActions performGetProfileEducationItems() throws Exception {
        return mockMvc.perform(
                get("/api/v1/profile/education")
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
        );
    }

    private ResultActions performGetProfileEducationDetail(final Long profileEducationId) throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders.get("/api/v1/profile/education/{profileEducationId}", profileEducationId)
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
        );
    }

    private ResultActions performAddProfileEducation(final AddProfileEducationRequest request) throws Exception {
        return mockMvc.perform(
                post("/api/v1/profile/education")
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));
    }

    private ResultActions performUpdateProfileEducation(final Long profileEducationId, final UpdateProfileEducationRequest request) throws Exception {
        return mockMvc.perform(
                post("/api/v1/profile/education/{profileEducationId}", profileEducationId)
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));
    }

    private ResultActions performRemoveProfileEducation(final Long profileEducationId) throws Exception {
        return mockMvc.perform(
                delete("/api/v1/profile/education/{profileEducationId}", profileEducationId)
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE));
    }

    private ResultActions performRemoveEducationCertification(final Long profileEducationId) throws Exception {
        return mockMvc.perform(
                delete("/api/v1/profile/education/certification/{profileEducationId}", profileEducationId)
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE));
    }

    @DisplayName("회원이 나의 학력을 전체 조회할 수 있다.")
    @Test
    void getProfileEducationItems() throws Exception {
        // given
        final ProfileEducationResponseDTO.ProfileEducationItem firstProfileEducationItem
                = new ProfileEducationItem(1L, "대학 이름 1", "전공 이름 1", "입학 연도 1", "졸업 연도 1", false, true);

        final ProfileEducationResponseDTO.ProfileEducationItem secondProfileEducationItem
                = new ProfileEducationItem(2L, "대학 이름 2", "전공 이름 2", "입학 연도 2", "졸업 연도 2", false, true);

        final ProfileEducationResponseDTO.ProfileEducationItems profileEducationItems
                = new ProfileEducationResponseDTO.ProfileEducationItems(Arrays.asList(firstProfileEducationItem, secondProfileEducationItem));

        // when
        when(profileEducationService.getProfileEducationItems(anyLong())).thenReturn(profileEducationItems);

        final ResultActions resultActions = performGetProfileEducationItems();

        // then
        final MvcResult mvcResult = resultActions
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
                                                .attributes(field("constraint", "boolean 값")),
                                        fieldWithPath("code")
                                                .type(JsonFieldType.STRING)
                                                .description("요청 성공 코드")
                                                .attributes(field("constraint", "문자열")),
                                        fieldWithPath("message")
                                                .type(JsonFieldType.STRING)
                                                .description("요청 성공 메시지")
                                                .attributes(field("constraint", "문자열")),
                                        subsectionWithPath("result.profileEducationItems[]")
                                                .type(JsonFieldType.ARRAY)
                                                .description("프로필 학력 아이템 배열"),
                                        fieldWithPath("result.profileEducationItems[].profileEducationId")
                                                .type(JsonFieldType.NUMBER)
                                                .description("내 학력 ID"),

                                        fieldWithPath("result.profileEducationItems[].universityName")
                                                .type(JsonFieldType.STRING)
                                                .description("대학교 이름"),
                                        fieldWithPath("result.profileEducationItems[].majorName")
                                                .type(JsonFieldType.STRING)
                                                .description("전공 이름"),
                                        fieldWithPath("result.profileEducationItems[].admissionYear")
                                                .type(JsonFieldType.STRING)
                                                .description("대학 입학 연도"),
                                        fieldWithPath("result.profileEducationItems[].graduationYear")
                                                .type(JsonFieldType.STRING)
                                                .description("대학 졸업 연도"),
                                        fieldWithPath("result.profileEducationItems[].isAttendUniversity")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("대학 재학 여부 (true: 재학 중, false: 재학 X)"),
                                        fieldWithPath("result.profileEducationItems[].isEducationVerified")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("학력 증명서 인증 여부")
                                )
                        )).andReturn();

        // JSON 응답에서 result 객체를 추출 및 검증
        final String jsonResponse = mvcResult.getResponse().getContentAsString();
        final CommonResponse<ProfileEducationResponseDTO.ProfileEducationItems> actual = objectMapper.readValue(
                jsonResponse,
                new TypeReference<CommonResponse<ProfileEducationResponseDTO.ProfileEducationItems>>() {
                }
        );

        final CommonResponse<ProfileEducationResponseDTO.ProfileEducationItems> expected = CommonResponse.onSuccess(profileEducationItems);

        // then
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @DisplayName("회원이 나의 학력을 상세 조회할 수 있다.")
    @Test
    void getProfileEducationDetail() throws Exception {
        // given
        final ProfileEducationResponseDTO.ProfileEducationDetail profileEducationDetail
                = new ProfileEducationResponseDTO.ProfileEducationDetail(1L, "대학 이름 1", "전공 이름 1", "입학 연도 1", "졸업 연도 1", false, "학력 설명", true, true, "증명서 파일 이름", "증명서 파일 경로");

        // when
        when(profileEducationService.getProfileEducationDetail(anyLong(), anyLong())).thenReturn(profileEducationDetail);

        final ResultActions resultActions = performGetProfileEducationDetail(1L);

        // then
        final MvcResult mvcResult = resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value("true"))
                .andExpect(jsonPath("$.code").value("1000"))
                .andExpect(jsonPath("$.message").value("요청에 성공하였습니다."))
                .andDo(
                        restDocs.document(
                                pathParameters(
                                        parameterWithName("profileEducationId")
                                                .description("프로필 학력 ID")
                                ),
                                responseFields(
                                        fieldWithPath("isSuccess")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("요청 성공 여부")
                                                .attributes(field("constraint", "boolean 값")),
                                        fieldWithPath("code")
                                                .type(JsonFieldType.STRING)
                                                .description("요청 성공 코드")
                                                .attributes(field("constraint", "문자열")),
                                        fieldWithPath("message")
                                                .type(JsonFieldType.STRING)
                                                .description("요청 성공 메시지")
                                                .attributes(field("constraint", "문자열")),
                                        fieldWithPath("result.profileEducationId")
                                                .type(JsonFieldType.NUMBER)
                                                .description("내 학력 ID"),
                                        fieldWithPath("result.universityName")
                                                .type(JsonFieldType.STRING)
                                                .description("대학교 이름"),
                                        fieldWithPath("result.majorName")
                                                .type(JsonFieldType.STRING)
                                                .description("전공 이름"),
                                        fieldWithPath("result.admissionYear")
                                                .type(JsonFieldType.STRING)
                                                .description("대학 입학 연도"),
                                        fieldWithPath("result.graduationYear")
                                                .type(JsonFieldType.STRING)
                                                .description("대학 졸업 연도"),
                                        fieldWithPath("result.isAttendUniversity")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("대학 재학 여부 (true: 재학 중, false: 재학 X)"),
                                        fieldWithPath("result.educationDescription")
                                                .type(JsonFieldType.STRING)
                                                .description("학력 설명"),

                                        fieldWithPath("result.isEducationCertified")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("학력 증명서 존재 여부"),
                                        fieldWithPath("result.isEducationVerified")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("학력 증명서 인증 여부"),
                                        fieldWithPath("result.educationCertificationAttachFileName")
                                                .type(JsonFieldType.STRING)
                                                .description("학력 증명서 파일 이름"),
                                        fieldWithPath("result.educationCertificationAttachFilePath")
                                                .type(JsonFieldType.STRING)
                                                .description("학력 증명서 파일 경로")
                                )
                        )).andReturn();

        // JSON 응답에서 result 객체를 추출 및 검증
        final String jsonResponse = mvcResult.getResponse().getContentAsString();
        final CommonResponse<ProfileEducationDetail> actual = objectMapper.readValue(
                jsonResponse,
                new TypeReference<CommonResponse<ProfileEducationDetail>>() {
                }
        );

        final CommonResponse<ProfileEducationDetail> expected = CommonResponse.onSuccess(profileEducationDetail);

        // then
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @DisplayName("회원이 나의 학력을 생성할 수 있다.")
    @Test
    void addProfileEducation() throws Exception {
        // given
        final ProfileEducationRequestDTO.AddProfileEducationRequest addProfileEducationRequest
                = new AddProfileEducationRequest("대학 이름", "전공 이름", "입학 연도", "졸업 연도", false, "학력 설명");

        final AddProfileEducationResponse addProfileEducationResponse
                = new AddProfileEducationResponse(1L, "대학 이름", "전공 이름", "입학 연도", "졸업 연도", false, "학력 설명");

        // when
        when(profileEducationService.addProfileEducation(anyLong(), any())).thenReturn(addProfileEducationResponse);

        final ResultActions resultActions = performAddProfileEducation(addProfileEducationRequest);

        // then
        final MvcResult mvcResult = resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value("true"))
                .andExpect(jsonPath("$.code").value("1000"))
                .andExpect(jsonPath("$.message").value("요청에 성공하였습니다."))
                .andDo(
                        restDocs.document(
                                requestFields(
                                        fieldWithPath("universityName")
                                                .type(JsonFieldType.STRING)
                                                .description("대학 이름"),
                                        fieldWithPath("majorName")
                                                .type(JsonFieldType.STRING)
                                                .description("전공 이름"),
                                        fieldWithPath("admissionYear")
                                                .type(JsonFieldType.STRING)
                                                .description("입학 연도"),
                                        fieldWithPath("graduationYear")
                                                .type(JsonFieldType.STRING)
                                                .description("졸업 연도"),
                                        fieldWithPath("isAttendUniversity")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("재학 여부"),
                                        fieldWithPath("educationDescription")
                                                .type(JsonFieldType.STRING)
                                                .description("학력 설명")
                                ),
                                responseFields(
                                        fieldWithPath("isSuccess")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("요청 성공 여부")
                                                .attributes(field("constraint", "boolean 값")),
                                        fieldWithPath("code")
                                                .type(JsonFieldType.STRING)
                                                .description("요청 성공 코드")
                                                .attributes(field("constraint", "문자열")),
                                        fieldWithPath("message")
                                                .type(JsonFieldType.STRING)
                                                .description("요청 성공 메시지")
                                                .attributes(field("constraint", "문자열")),
                                        fieldWithPath("result.profileEducationId")
                                                .type(JsonFieldType.NUMBER)
                                                .description("프로필 학력 ID"),
                                        fieldWithPath("result.universityName")
                                                .type(JsonFieldType.STRING)
                                                .description("대학교 이름"),
                                        fieldWithPath("result.majorName")
                                                .type(JsonFieldType.STRING)
                                                .description("전공 이름"),
                                        fieldWithPath("result.admissionYear")
                                                .type(JsonFieldType.STRING)
                                                .description("대학 입학 연도"),
                                        fieldWithPath("result.graduationYear")
                                                .type(JsonFieldType.STRING)
                                                .description("대학 졸업 연도"),
                                        fieldWithPath("result.isAttendUniversity")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("대학 재학 여부 (true: 재학 중, false: 재학 X)"),
                                        fieldWithPath("result.educationDescription")
                                                .type(JsonFieldType.STRING)
                                                .description("학력 설명")
                                )
                        )).andReturn();

        // JSON 응답에서 result 객체를 추출 및 검증
        final String jsonResponse = mvcResult.getResponse().getContentAsString();
        final CommonResponse<AddProfileEducationResponse> actual = objectMapper.readValue(
                jsonResponse,
                new TypeReference<CommonResponse<AddProfileEducationResponse>>() {
                }
        );

        final CommonResponse<AddProfileEducationResponse> expected = CommonResponse.onSuccess(addProfileEducationResponse);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @DisplayName("회원이 나의 학력을 수정할 수 있다.")
    @Test
    void updateProfileEducation() throws Exception {
        // given
        final ProfileEducationRequestDTO.UpdateProfileEducationRequest updateProfileEducationRequest
                = new UpdateProfileEducationRequest("수정 대학 이름", "수정 전공 이름", "수정 입학 연도", "수정 졸업 연도", false, "수정 학력 설명");

        final UpdateProfileEducationResponse updateProfileActivityResponse
                = new UpdateProfileEducationResponse(1L, "수정 대학 이름", "수정 전공 이름", "수정 입학 연도", "수정 졸업 연도", false, "수정 학력 설명");

        // when
        when(profileEducationService.updateProfileEducation(anyLong(), anyLong(), any())).thenReturn(updateProfileActivityResponse);

        final ResultActions resultActions = performUpdateProfileEducation(1L, updateProfileEducationRequest);

        // then
        final MvcResult mvcResult = resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value("true"))
                .andExpect(jsonPath("$.code").value("1000"))
                .andExpect(jsonPath("$.message").value("요청에 성공하였습니다."))
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("profileEducationId")
                                        .description("프로필 학력 ID")
                        ),
                        requestFields(
                                fieldWithPath("universityName")
                                        .type(JsonFieldType.STRING)
                                        .description("대학 이름"),
                                fieldWithPath("majorName")
                                        .type(JsonFieldType.STRING)
                                        .description("전공 이름"),
                                fieldWithPath("admissionYear")
                                        .type(JsonFieldType.STRING)
                                        .description("입학 연도"),
                                fieldWithPath("graduationYear")
                                        .type(JsonFieldType.STRING)
                                        .description("졸업 연도"),
                                fieldWithPath("isAttendUniversity")
                                        .type(JsonFieldType.BOOLEAN)
                                        .description("재학 여부"),
                                fieldWithPath("educationDescription")
                                        .type(JsonFieldType.STRING)
                                        .description("학력 설명")
                        ),
                        responseFields(
                                fieldWithPath("isSuccess")
                                        .type(JsonFieldType.BOOLEAN)
                                        .description("요청 성공 여부")
                                        .attributes(field("constraint", "boolean 값")),
                                fieldWithPath("code")
                                        .type(JsonFieldType.STRING)
                                        .description("요청 성공 코드")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("message")
                                        .type(JsonFieldType.STRING)
                                        .description("요청 성공 메시지")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("result.profileEducationId")
                                        .type(JsonFieldType.NUMBER)
                                        .description("프로필 학력 ID"),
                                fieldWithPath("result.universityName")
                                        .type(JsonFieldType.STRING)
                                        .description("대학교 이름"),
                                fieldWithPath("result.majorName")
                                        .type(JsonFieldType.STRING)
                                        .description("전공 이름"),
                                fieldWithPath("result.admissionYear")
                                        .type(JsonFieldType.STRING)
                                        .description("대학 입학 연도"),
                                fieldWithPath("result.graduationYear")
                                        .type(JsonFieldType.STRING)
                                        .description("대학 졸업 연도"),
                                fieldWithPath("result.isAttendUniversity")
                                        .type(JsonFieldType.BOOLEAN)
                                        .description("대학 재학 여부 (true: 재학 중, false: 재학 X)"),
                                fieldWithPath("result.educationDescription")
                                        .type(JsonFieldType.STRING)
                                        .description("학력 설명")
                        )
                )).andReturn();

        // JSON 응답에서 result 객체를 추출 및 검증
        final String jsonResponse = mvcResult.getResponse().getContentAsString();
        final CommonResponse<UpdateProfileEducationResponse> actual = objectMapper.readValue(
                jsonResponse,
                new TypeReference<CommonResponse<UpdateProfileEducationResponse>>() {
                }
        );

        final CommonResponse<UpdateProfileEducationResponse> expected = CommonResponse.onSuccess(updateProfileActivityResponse);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @DisplayName("회원이 나의 학력을 삭제할 수 있다.")
    @Test
    void removeProfileEducation() throws Exception {
        // given
        final ProfileEducationResponseDTO.RemoveProfileEducationResponse removeProfileEducationResponse
                = new RemoveProfileEducationResponse(1L);

        // when
        when(profileEducationService.removeProfileEducation(anyLong(), anyLong())).thenReturn(removeProfileEducationResponse);

        final ResultActions resultActions = performRemoveProfileEducation(1L);

        // then
        final MvcResult mvcResult = resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value("true"))
                .andExpect(jsonPath("$.code").value("1000"))
                .andExpect(jsonPath("$.message").value("요청에 성공하였습니다."))
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("profileEducationId")
                                        .description("프로필 학력 ID")
                        ),
                        responseFields(fieldWithPath("isSuccess")
                                        .type(JsonFieldType.BOOLEAN)
                                        .description("요청 성공 여부")
                                        .attributes(field("constraint", "boolean 값")),
                                fieldWithPath("code")
                                        .type(JsonFieldType.STRING)
                                        .description("요청 성공 코드")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("message")
                                        .type(JsonFieldType.STRING)
                                        .description("요청 성공 메시지")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("result.profileEducationId")
                                        .type(JsonFieldType.NUMBER)
                                        .description("프로필 학력 ID")
                        )
                )).andReturn();

        // JSON 응답에서 result 객체를 추출 및 검증
        final String jsonResponse = mvcResult.getResponse().getContentAsString();
        final CommonResponse<RemoveProfileEducationResponse> actual = objectMapper.readValue(
                jsonResponse,
                new TypeReference<CommonResponse<RemoveProfileEducationResponse>>() {
                }
        );

        final CommonResponse<RemoveProfileEducationResponse> expected = CommonResponse.onSuccess(removeProfileEducationResponse);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @DisplayName("회원이 나의 학력 증명서를 추가할 수 있다.")
    @Test
    void addProfileEducationCertification() throws Exception {
        // given
        final ProfileEducationCertificationResponse profileEducationCertificationResponse
                = new ProfileEducationCertificationResponse(true, false, "증명서.pdf", "https://file.linkit.im/해시값.pdf");

        final MockMultipartFile profileEducationCertificationFile = new MockMultipartFile(
                "profileEducationCertificationFile",
                "증명서.pdf",
                "multipart/form-data",
                "./src/test/resources/static/증명서.pdf".getBytes()
        );

        final Long profileEducationId = 1L;

        // when
        when(profileEducationService.addProfileEducationCertification(anyLong(), anyLong(), any())).thenReturn(profileEducationCertificationResponse);

        final ResultActions resultActions = mockMvc.perform(RestDocumentationRequestBuilders.multipart("/api/v1/profile/education/certification/{profileEducationId}", profileEducationId)
                .file(profileEducationCertificationFile)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .characterEncoding("UTF-8")
                .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                .cookie(COOKIE));

        // then
        final MvcResult mvcResult = resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value("true"))
                .andExpect(jsonPath("$.code").value("1000"))
                .andExpect(jsonPath("$.message").value("요청에 성공하였습니다."))
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("profileEducationId")
                                        .description("프로필 학력 ID")
                        ),
                        requestParts(
                                partWithName("profileEducationCertificationFile").description("학력 증명 파일")
                        ),
                        responseFields(fieldWithPath("isSuccess")
                                        .type(JsonFieldType.BOOLEAN)
                                        .description("요청 성공 여부")
                                        .attributes(field("constraint", "boolean 값")),
                                fieldWithPath("code")
                                        .type(JsonFieldType.STRING)
                                        .description("요청 성공 코드")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("message")
                                        .type(JsonFieldType.STRING)
                                        .description("요청 성공 메시지")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("result.isEducationCertified")
                                        .type(JsonFieldType.BOOLEAN)
                                        .description("학력 증명서 존재 여부"),
                                fieldWithPath("result.isEducationVerified")
                                        .type(JsonFieldType.BOOLEAN)
                                        .description("학력 증명서 인증 여부"),
                                fieldWithPath("result.educationCertificationAttachFileName")
                                        .type(JsonFieldType.STRING)
                                        .description("학력 증명서 파일 이름"),
                                fieldWithPath("result.educationCertificationAttachFilePath")
                                        .type(JsonFieldType.STRING)
                                        .description("학력 증명서 파일 경로")
                        )
                )).andReturn();

        // JSON 응답에서 result 객체를 추출 및 검증
        final String jsonResponse = mvcResult.getResponse().getContentAsString();
        final CommonResponse<ProfileEducationCertificationResponse> actual = objectMapper.readValue(
                jsonResponse,
                new TypeReference<CommonResponse<ProfileEducationCertificationResponse>>() {
                }
        );

        final CommonResponse<ProfileEducationCertificationResponse> expected = CommonResponse.onSuccess(profileEducationCertificationResponse);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @DisplayName("회원이 나의 학력 증명서를 추가할 수 있다.")
    @Test
    void removeProfileEducationCertification() throws Exception {
        // given
        final ProfileEducationResponseDTO.RemoveProfileEducationCertificationResponse removeProfileEducationCertificationResponse
                = new RemoveProfileEducationCertificationResponse(1L);

        // when
        when(profileEducationService.removeProfileEducationCertification(anyLong(), anyLong())).thenReturn(removeProfileEducationCertificationResponse);

        final ResultActions resultActions = performRemoveEducationCertification(1L);

        // then
        final MvcResult mvcResult = resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value("true"))
                .andExpect(jsonPath("$.code").value("1000"))
                .andExpect(jsonPath("$.message").value("요청에 성공하였습니다."))
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("profileEducationId")
                                        .description("프로필 학력 ID")
                        ),
                        responseFields(fieldWithPath("isSuccess")
                                        .type(JsonFieldType.BOOLEAN)
                                        .description("요청 성공 여부")
                                        .attributes(field("constraint", "boolean 값")),
                                fieldWithPath("code")
                                        .type(JsonFieldType.STRING)
                                        .description("요청 성공 코드")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("message")
                                        .type(JsonFieldType.STRING)
                                        .description("요청 성공 메시지")
                                        .attributes(field("constraint", "문자열")),
                                fieldWithPath("result.profileEducationId")
                                        .type(JsonFieldType.NUMBER)
                                        .description("프로필 학력 ID")
                        )
                )).andReturn();

        // JSON 응답에서 result 객체를 추출 및 검증
        final String jsonResponse = mvcResult.getResponse().getContentAsString();
        final CommonResponse<RemoveProfileEducationCertificationResponse> actual = objectMapper.readValue(
                jsonResponse,
                new TypeReference<CommonResponse<RemoveProfileEducationCertificationResponse>>() {
                }
        );

        final CommonResponse<RemoveProfileEducationCertificationResponse> expected = CommonResponse.onSuccess(removeProfileEducationCertificationResponse);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }
}
