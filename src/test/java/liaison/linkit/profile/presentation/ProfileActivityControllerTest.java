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
import groovy.util.logging.Slf4j;
import jakarta.servlet.http.Cookie;
import java.util.Arrays;
import liaison.linkit.common.presentation.CommonResponse;
import liaison.linkit.global.ControllerTest;
import liaison.linkit.login.domain.MemberTokens;
import liaison.linkit.profile.presentation.activity.ProfileActivityController;
import liaison.linkit.profile.presentation.activity.dto.ProfileActivityRequestDTO;
import liaison.linkit.profile.presentation.activity.dto.ProfileActivityRequestDTO.AddProfileActivityRequest;
import liaison.linkit.profile.presentation.activity.dto.ProfileActivityRequestDTO.UpdateProfileActivityRequest;
import liaison.linkit.profile.presentation.activity.dto.ProfileActivityResponseDTO;
import liaison.linkit.profile.presentation.activity.dto.ProfileActivityResponseDTO.AddProfileActivityResponse;
import liaison.linkit.profile.presentation.activity.dto.ProfileActivityResponseDTO.ProfileActivityCertificationResponse;
import liaison.linkit.profile.presentation.activity.dto.ProfileActivityResponseDTO.ProfileActivityDetail;
import liaison.linkit.profile.presentation.activity.dto.ProfileActivityResponseDTO.ProfileActivityItem;
import liaison.linkit.profile.presentation.activity.dto.ProfileActivityResponseDTO.ProfileActivityItems;
import liaison.linkit.profile.presentation.activity.dto.ProfileActivityResponseDTO.RemoveProfileActivityCertificationResponse;
import liaison.linkit.profile.presentation.activity.dto.ProfileActivityResponseDTO.RemoveProfileActivityResponse;
import liaison.linkit.profile.presentation.activity.dto.ProfileActivityResponseDTO.UpdateProfileActivityResponse;
import liaison.linkit.profile.business.service.ProfileActivityService;
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

@WebMvcTest(ProfileActivityController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
@Slf4j
public class ProfileActivityControllerTest extends ControllerTest {

    private static final MemberTokens MEMBER_TOKENS = new MemberTokens("accessToken", "refreshToken");
    private static final Cookie COOKIE = new Cookie("refreshToken", MEMBER_TOKENS.getRefreshToken());

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProfileActivityService profileActivityService;

    @BeforeEach
    void setUp() {
        given(refreshTokenRepository.existsById(any())).willReturn(true);
        doNothing().when(jwtProvider).validateTokens(any());
        given(jwtProvider.getSubject(any())).willReturn("1");
    }

    private ResultActions performGetProfileActivityItems() throws Exception {
        return mockMvc.perform(
                get("/api/v1/profile/activity")
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
        );
    }

    private ResultActions performGetProfileActivityDetail(final Long profileActivityId) throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders.get("/api/v1/profile/activity/{profileActivityId}", profileActivityId)
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
        );
    }

    private ResultActions performAddProfileActivity(final AddProfileActivityRequest request) throws Exception {
        return mockMvc.perform(
                post("/api/v1/profile/activity")
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));
    }

    private ResultActions performUpdateProfileActivity(final Long profileActivityId, final UpdateProfileActivityRequest request) throws Exception {
        return mockMvc.perform(
                post("/api/v1/profile/activity/{profileActivityId}", profileActivityId)
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));
    }

    private ResultActions performRemoveProfileActivity(final Long profileActivityId) throws Exception {
        return mockMvc.perform(
                delete("/api/v1/profile/activity/{profileActivityId}", profileActivityId)
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE));
    }

    private ResultActions performRemoveActivityCertification(final Long profileActivityId) throws Exception {
        return mockMvc.perform(
                delete("/api/v1/profile/activity/certification/{profileActivityId}", profileActivityId)
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE));
    }

    @DisplayName("회원이 나의 이력을 전체 조회할 수 있다.")
    @Test
    void getProfileActivityItems() throws Exception {
        // given
        final ProfileActivityResponseDTO.ProfileActivityItem firstProfileActivityItem
                = new ProfileActivityItem(1L, "리에종", "PO", "2022.06", "2026.06", true, "이력 설명 1");

        final ProfileActivityResponseDTO.ProfileActivityItem secondProfileActivityItem
                = new ProfileActivityItem(2L, "리에종", "디자이너", "2024.10", "2024.12", true, "이력 설명 2");

        final ProfileActivityResponseDTO.ProfileActivityItems profileActivityItems
                = new ProfileActivityItems(Arrays.asList(firstProfileActivityItem, secondProfileActivityItem));

        // when
        when(profileActivityService.getProfileActivityItems(anyLong())).thenReturn(profileActivityItems);

        final ResultActions resultActions = performGetProfileActivityItems();

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
                                        subsectionWithPath("result.profileActivityItems[]")
                                                .type(JsonFieldType.ARRAY)
                                                .description("프로필 이력 아이템 배열"),
                                        fieldWithPath("result.profileActivityItems[].profileActivityId")
                                                .type(JsonFieldType.NUMBER)
                                                .description("내 이력 ID"),
                                        fieldWithPath("result.profileActivityItems[].activityName")
                                                .type(JsonFieldType.STRING)
                                                .description("이력 활동명"),
                                        fieldWithPath("result.profileActivityItems[].activityRole")
                                                .type(JsonFieldType.STRING)
                                                .description("이력 역할"),
                                        fieldWithPath("result.profileActivityItems[].activityStartDate")
                                                .type(JsonFieldType.STRING)
                                                .description("이력 시작 기간"),
                                        fieldWithPath("result.profileActivityItems[].activityEndDate")
                                                .type(JsonFieldType.STRING)
                                                .description("이력 종료 기간"),
                                        fieldWithPath("result.profileActivityItems[].isActivityVerified")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("이력 증명서 인증 완료 여부")
                                )
                        )).andReturn();

        // JSON 응답에서 result 객체를 추출 및 검증
        final String jsonResponse = mvcResult.getResponse().getContentAsString();
        final CommonResponse<ProfileActivityItems> actual = objectMapper.readValue(
                jsonResponse,
                new TypeReference<CommonResponse<ProfileActivityItems>>() {
                }
        );

        final CommonResponse<ProfileActivityItems> expected = CommonResponse.onSuccess(profileActivityItems);

        // then
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @DisplayName("회원이 나의 이력을 상세 조회할 수 있다.")
    @Test
    void getProfileActivityDetail() throws Exception {
        // given
        final ProfileActivityResponseDTO.ProfileActivityDetail profileActivityDetail
                = new ProfileActivityDetail(1L, "리에종", "PO", "2022.06", "2026.06", false, "이력 설명", true, false, "증명서.pdf", "https://file.linkit.im/해시값.pdf");

        // when
        when(profileActivityService.getProfileActivityDetail(anyLong(), anyLong())).thenReturn(profileActivityDetail);

        final ResultActions resultActions = performGetProfileActivityDetail(1L);

        // then
        final MvcResult mvcResult = resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value("true"))
                .andExpect(jsonPath("$.code").value("1000"))
                .andExpect(jsonPath("$.message").value("요청에 성공하였습니다."))
                .andDo(
                        restDocs.document(
                                pathParameters(
                                        parameterWithName("profileActivityId")
                                                .description("프로필 이력 ID")
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
                                        fieldWithPath("result.profileActivityId")
                                                .type(JsonFieldType.NUMBER)
                                                .description("프로필 이력 ID"),
                                        fieldWithPath("result.activityName")
                                                .type(JsonFieldType.STRING)
                                                .description("이력 활동명"),
                                        fieldWithPath("result.activityRole")
                                                .type(JsonFieldType.STRING)
                                                .description("이력 역할"),
                                        fieldWithPath("result.activityStartDate")
                                                .type(JsonFieldType.STRING)
                                                .description("이력 시작 기간"),
                                        fieldWithPath("result.activityEndDate")
                                                .type(JsonFieldType.STRING)
                                                .description("이력 종료 기간"),
                                        fieldWithPath("result.isActivityInProgress")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("이력 진행 여부"),
                                        fieldWithPath("result.activityDescription")
                                                .type(JsonFieldType.STRING)
                                                .description("이력 설명"),
                                        fieldWithPath("result.isActivityCertified")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("증명서 존재 여부"),
                                        fieldWithPath("result.isActivityVerified")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("증명서 인증 여부"),
                                        fieldWithPath("result.activityCertificationAttachFileName")
                                                .type(JsonFieldType.STRING)
                                                .description("증명서 파일 이름"),
                                        fieldWithPath("result.activityCertificationAttachFilePath")
                                                .type(JsonFieldType.STRING)
                                                .description("증명서 파일 경로")
                                )
                        )).andReturn();

    }

    @DisplayName("회원이 나의 이력을 생성할 수 있다.")
    @Test
    void addProfileActivity() throws Exception {
        // given
        final ProfileActivityRequestDTO.AddProfileActivityRequest addProfileActivityRequest
                = new AddProfileActivityRequest("리에종", "PO", "2022.06", "2026.06", false, "이력 설명");

        final AddProfileActivityResponse addProfileActivityResponse
                = new AddProfileActivityResponse(1L, "리에종", "PO", "2022.06", "2026.06", false, "이력 설명");

        // when
        when(profileActivityService.addProfileActivity(anyLong(), any())).thenReturn(addProfileActivityResponse);

        final ResultActions resultActions = performAddProfileActivity(addProfileActivityRequest);

        // then
        final MvcResult mvcResult = resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value("true"))
                .andExpect(jsonPath("$.code").value("1000"))
                .andExpect(jsonPath("$.message").value("요청에 성공하였습니다."))
                .andDo(
                        restDocs.document(
                                requestFields(
                                        fieldWithPath("activityName")
                                                .type(JsonFieldType.STRING)
                                                .description("이력 활동명"),
                                        fieldWithPath("activityRole")
                                                .type(JsonFieldType.STRING)
                                                .description("이력 역할"),
                                        fieldWithPath("activityStartDate")
                                                .type(JsonFieldType.STRING)
                                                .description("이력 시작 기간"),
                                        fieldWithPath("activityEndDate")
                                                .type(JsonFieldType.STRING)
                                                .description("이력 종료 기간"),
                                        fieldWithPath("isActivityInProgress")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("이력 진행 여부"),
                                        fieldWithPath("activityDescription")
                                                .type(JsonFieldType.STRING)
                                                .description("이력 설명")
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
                                        fieldWithPath("result.profileActivityId")
                                                .type(JsonFieldType.NUMBER)
                                                .description("프로필 이력 ID"),
                                        fieldWithPath("result.activityName")
                                                .type(JsonFieldType.STRING)
                                                .description("이력 활동명"),
                                        fieldWithPath("result.activityRole")
                                                .type(JsonFieldType.STRING)
                                                .description("이력 역할"),
                                        fieldWithPath("result.activityStartDate")
                                                .type(JsonFieldType.STRING)
                                                .description("이력 시작 기간"),
                                        fieldWithPath("result.activityEndDate")
                                                .type(JsonFieldType.STRING)
                                                .description("이력 종료 기간"),
                                        fieldWithPath("result.isActivityInProgress")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("이력 진행 여부"),
                                        fieldWithPath("result.activityDescription")
                                                .type(JsonFieldType.STRING)
                                                .description("이력 설명")
                                )
                        )).andReturn();

        // JSON 응답에서 result 객체를 추출 및 검증
        final String jsonResponse = mvcResult.getResponse().getContentAsString();
        final CommonResponse<AddProfileActivityResponse> actual = objectMapper.readValue(
                jsonResponse,
                new TypeReference<CommonResponse<AddProfileActivityResponse>>() {
                }
        );

        final CommonResponse<AddProfileActivityResponse> expected = CommonResponse.onSuccess(addProfileActivityResponse);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @DisplayName("회원이 나의 이력을 수정할 수 있다.")
    @Test
    void updateProfileActivity() throws Exception {
        // given

        final ProfileActivityRequestDTO.UpdateProfileActivityRequest updateProfileActivityRequest
                = new UpdateProfileActivityRequest("리에종2", "BE", "2023.09", "2024.08", false, "이력 설명2");

        final UpdateProfileActivityResponse updateProfileActivityResponse
                = new UpdateProfileActivityResponse(2L, "리에종2", "BE", "2023.09", "2024.08", false, "이력 설명2");

        // when
        when(profileActivityService.updateProfileActivity(anyLong(), anyLong(), any())).thenReturn(updateProfileActivityResponse);

        final ResultActions resultActions = performUpdateProfileActivity(1L, updateProfileActivityRequest);

        // then
        final MvcResult mvcResult = resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value("true"))
                .andExpect(jsonPath("$.code").value("1000"))
                .andExpect(jsonPath("$.message").value("요청에 성공하였습니다."))
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("profileActivityId")
                                        .description("프로필 이력 ID")
                        ),
                        requestFields(
                                fieldWithPath("activityName")
                                        .type(JsonFieldType.STRING)
                                        .description("이력 활동명"),
                                fieldWithPath("activityRole")
                                        .type(JsonFieldType.STRING)
                                        .description("이력 역할"),
                                fieldWithPath("activityStartDate")
                                        .type(JsonFieldType.STRING)
                                        .description("이력 시작 기간"),
                                fieldWithPath("activityEndDate")
                                        .type(JsonFieldType.STRING)
                                        .description("이력 종료 기간"),
                                fieldWithPath("isActivityInProgress")
                                        .type(JsonFieldType.BOOLEAN)
                                        .description("이력 진행 여부"),
                                fieldWithPath("activityDescription")
                                        .type(JsonFieldType.STRING)
                                        .description("이력 설명")
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
                                fieldWithPath("result.profileActivityId")
                                        .type(JsonFieldType.NUMBER)
                                        .description("프로필 이력 ID"),
                                fieldWithPath("result.activityName")
                                        .type(JsonFieldType.STRING)
                                        .description("이력 활동명"),
                                fieldWithPath("result.activityRole")
                                        .type(JsonFieldType.STRING)
                                        .description("이력 역할"),
                                fieldWithPath("result.activityStartDate")
                                        .type(JsonFieldType.STRING)
                                        .description("이력 시작 기간"),
                                fieldWithPath("result.activityEndDate")
                                        .type(JsonFieldType.STRING)
                                        .description("이력 종료 기간"),
                                fieldWithPath("result.isActivityInProgress")
                                        .type(JsonFieldType.BOOLEAN)
                                        .description("이력 진행 여부"),
                                fieldWithPath("result.activityDescription")
                                        .type(JsonFieldType.STRING)
                                        .description("이력 설명")
                        )
                )).andReturn();

        // JSON 응답에서 result 객체를 추출 및 검증
        final String jsonResponse = mvcResult.getResponse().getContentAsString();
        final CommonResponse<UpdateProfileActivityResponse> actual = objectMapper.readValue(
                jsonResponse,
                new TypeReference<CommonResponse<UpdateProfileActivityResponse>>() {
                }
        );

        final CommonResponse<UpdateProfileActivityResponse> expected = CommonResponse.onSuccess(updateProfileActivityResponse);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @DisplayName("회원이 나의 이력을 삭제할 수 있다.")
    @Test
    void removeProfileActivity() throws Exception {
        // given
        final ProfileActivityResponseDTO.RemoveProfileActivityResponse removeProfileActivityResponse
                = new RemoveProfileActivityResponse(1L);
        // when
        when(profileActivityService.removeProfileActivity(anyLong(), anyLong())).thenReturn(removeProfileActivityResponse);

        final ResultActions resultActions = performRemoveProfileActivity(1L);

        // then
        final MvcResult mvcResult = resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value("true"))
                .andExpect(jsonPath("$.code").value("1000"))
                .andExpect(jsonPath("$.message").value("요청에 성공하였습니다."))
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("profileActivityId")
                                        .description("프로필 이력 ID")
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
                                fieldWithPath("result.profileActivityId")
                                        .type(JsonFieldType.NUMBER)
                                        .description("프로필 이력 ID")
                        )
                )).andReturn();

        // JSON 응답에서 result 객체를 추출 및 검증
        final String jsonResponse = mvcResult.getResponse().getContentAsString();
        final CommonResponse<RemoveProfileActivityResponse> actual = objectMapper.readValue(
                jsonResponse,
                new TypeReference<CommonResponse<RemoveProfileActivityResponse>>() {
                }
        );

        final CommonResponse<RemoveProfileActivityResponse> expected = CommonResponse.onSuccess(removeProfileActivityResponse);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @DisplayName("회원이 나의 이력 증명서를 추가할 수 있다.")
    @Test
    void addProfileActivityCertification() throws Exception {
        // given
        final ProfileActivityCertificationResponse profileActivityCertificationResponse
                = new ProfileActivityCertificationResponse(true, false, "증명서.pdf", "https://file.linkit.im/해시값.pdf");

        final MockMultipartFile profileActivityCertificationFile = new MockMultipartFile(
                "profileActivityCertificationFile",
                "증명서.pdf",
                "multipart/form-data",
                "./src/test/resources/static/증명서.pdf".getBytes()
        );

        final Long profileActivityId = 1L;

        // when
        when(profileActivityService.addProfileActivityCertification(anyLong(), anyLong(), any())).thenReturn(profileActivityCertificationResponse);

        final ResultActions resultActions = mockMvc.perform(RestDocumentationRequestBuilders.multipart("/api/v1/profile/activity/certification/{profileActivityId}", profileActivityId)
                .file(profileActivityCertificationFile)
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
                                parameterWithName("profileActivityId")
                                        .description("프로필 이력 ID")
                        ),
                        requestParts(
                                partWithName("profileActivityCertificationFile").description("이력 증명 파일")
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
                                fieldWithPath("result.isActivityCertified")
                                        .type(JsonFieldType.BOOLEAN)
                                        .description("증명서 존재 여부"),
                                fieldWithPath("result.isActivityVerified")
                                        .type(JsonFieldType.BOOLEAN)
                                        .description("증명서 인증 여부"),
                                fieldWithPath("result.activityCertificationAttachFileName")
                                        .type(JsonFieldType.STRING)
                                        .description("증명서 파일 이름"),
                                fieldWithPath("result.activityCertificationAttachFilePath")
                                        .type(JsonFieldType.STRING)
                                        .description("증명서 파일 경로")
                        )
                )).andReturn();

        // JSON 응답에서 result 객체를 추출 및 검증
        final String jsonResponse = mvcResult.getResponse().getContentAsString();
        final CommonResponse<ProfileActivityCertificationResponse> actual = objectMapper.readValue(
                jsonResponse,
                new TypeReference<CommonResponse<ProfileActivityCertificationResponse>>() {
                }
        );

        final CommonResponse<ProfileActivityCertificationResponse> expected = CommonResponse.onSuccess(profileActivityCertificationResponse);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @DisplayName("회원이 나의 이력 증명서를 추가할 수 있다.")
    @Test
    void removeProfileActivityCertification() throws Exception {
        // given
        final ProfileActivityResponseDTO.RemoveProfileActivityCertificationResponse removeProfileActivityCertificationResponse
                = new RemoveProfileActivityCertificationResponse(1L);

        // when
        when(profileActivityService.removeProfileActivityCertification(anyLong(), anyLong())).thenReturn(removeProfileActivityCertificationResponse);

        final ResultActions resultActions = performRemoveActivityCertification(1L);

        // then
        final MvcResult mvcResult = resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value("true"))
                .andExpect(jsonPath("$.code").value("1000"))
                .andExpect(jsonPath("$.message").value("요청에 성공하였습니다."))
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("profileActivityId")
                                        .description("프로필 이력 ID")
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
                                fieldWithPath("result.profileActivityId")
                                        .type(JsonFieldType.NUMBER)
                                        .description("프로필 이력 ID")
                        )
                )).andReturn();

        // JSON 응답에서 result 객체를 추출 및 검증
        final String jsonResponse = mvcResult.getResponse().getContentAsString();
        final CommonResponse<RemoveProfileActivityCertificationResponse> actual = objectMapper.readValue(
                jsonResponse,
                new TypeReference<CommonResponse<RemoveProfileActivityCertificationResponse>>() {
                }
        );

        final CommonResponse<RemoveProfileActivityCertificationResponse> expected = CommonResponse.onSuccess(removeProfileActivityCertificationResponse);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }
}
