package liaison.linkit.profile.presentation;

import static liaison.linkit.global.restdocs.RestDocsConfiguration.field;
import static liaison.linkit.profile.domain.type.LogType.GENERAL_LOG;
import static liaison.linkit.profile.domain.type.LogType.REPRESENTATIVE_LOG;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
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

import java.time.LocalDateTime;
import java.util.Arrays;

import jakarta.servlet.http.Cookie;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import groovy.util.logging.Slf4j;
import liaison.linkit.common.presentation.CommonResponse;
import liaison.linkit.global.ControllerTest;
import liaison.linkit.login.domain.MemberTokens;
import liaison.linkit.profile.business.service.ProfileLogService;
import liaison.linkit.profile.presentation.log.ProfileLogController;
import liaison.linkit.profile.presentation.log.dto.ProfileLogRequestDTO;
import liaison.linkit.profile.presentation.log.dto.ProfileLogRequestDTO.AddProfileLogRequest;
import liaison.linkit.profile.presentation.log.dto.ProfileLogRequestDTO.UpdateProfileLogRequest;
import liaison.linkit.profile.presentation.log.dto.ProfileLogResponseDTO;
import liaison.linkit.profile.presentation.log.dto.ProfileLogResponseDTO.AddProfileLogBodyImageResponse;
import liaison.linkit.profile.presentation.log.dto.ProfileLogResponseDTO.AddProfileLogResponse;
import liaison.linkit.profile.presentation.log.dto.ProfileLogResponseDTO.ProfileLogItem;
import liaison.linkit.profile.presentation.log.dto.ProfileLogResponseDTO.ProfileLogItems;
import liaison.linkit.profile.presentation.log.dto.ProfileLogResponseDTO.RemoveProfileLogResponse;
import liaison.linkit.profile.presentation.log.dto.ProfileLogResponseDTO.UpdateProfileLogPublicStateResponse;
import liaison.linkit.profile.presentation.log.dto.ProfileLogResponseDTO.UpdateProfileLogResponse;
import liaison.linkit.profile.presentation.log.dto.ProfileLogResponseDTO.UpdateProfileLogTypeResponse;
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

@WebMvcTest(ProfileLogController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
@Slf4j
public class ProfileLogControllerTest extends ControllerTest {
    private static final MemberTokens MEMBER_TOKENS =
            new MemberTokens("accessToken", "refreshToken");
    private static final Cookie COOKIE =
            new Cookie("refreshToken", MEMBER_TOKENS.getRefreshToken());

    @Autowired private ObjectMapper objectMapper;

    @MockBean private ProfileLogService profileLogService;

    @BeforeEach
    void setUp() {
        given(refreshTokenRepository.existsById(any())).willReturn(true);
        doNothing().when(jwtProvider).validateTokens(any());
        given(jwtProvider.getSubject(any())).willReturn("1");
    }

    private ResultActions performGetProfileLogItems() throws Exception {
        return mockMvc.perform(
                get("/api/v1/profile/log")
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE));
    }

    private ResultActions performGetProfileLogItem(final Long profileLogId) throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders.get(
                                "/api/v1/profile/log/{profileLogId}", profileLogId)
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE));
    }

    private ResultActions performAddProfileLog(final AddProfileLogRequest addProfileLogRequest)
            throws Exception {
        return mockMvc.perform(
                post("/api/v1/profile/log")
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addProfileLogRequest)));
    }

    private ResultActions performUpdateProfileLog(
            final Long profileLogId, final UpdateProfileLogRequest updateProfileLogRequest)
            throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders.post(
                                "/api/v1/profile/log/{profileLogId}", profileLogId)
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateProfileLogRequest)));
    }

    private ResultActions performDeleteProfileLog(final Long profileLogId) throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders.delete(
                                "/api/v1/profile/log/{profileLogId}", profileLogId)
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE));
    }

    private ResultActions performUpdateProfileLogType(final Long profileLogId) throws Exception {
        return mockMvc.perform(
                post("/api/v1/profile/log/type/{profileLogId}", profileLogId)
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE));
    }

    private ResultActions performUpdateProfileLogPublicState(final Long profileLogId)
            throws Exception {
        return mockMvc.perform(
                RestDocumentationRequestBuilders.post(
                                "/api/v1/profile/log/state/{profileLogId}", profileLogId)
                        .header(AUTHORIZATION, MEMBER_TOKENS.getAccessToken())
                        .cookie(COOKIE));
    }

    @DisplayName("회원이 로그 본문에 들어가는 이미지 첨부를 할 수 있다.")
    @Test
    void addProfileLogBodyImage() throws Exception {
        // given
        final ProfileLogResponseDTO.AddProfileLogBodyImageResponse addProfileLogBodyImageResponse =
                new AddProfileLogBodyImageResponse("https://image.linkit.im/logo.png");

        final MockMultipartFile profileLogBodyImage =
                new MockMultipartFile(
                        "profileLogBodyImage",
                        "logo.png",
                        "multipart/form-data",
                        "./src/test/resources/static/images/logo.png".getBytes());

        // when
        when(profileLogService.addProfileLogBodyImage(anyLong(), any()))
                .thenReturn(addProfileLogBodyImageResponse);

        final ResultActions resultActions =
                mockMvc.perform(
                        RestDocumentationRequestBuilders.multipart("/api/v1/profile/log/body/image")
                                .file(profileLogBodyImage)
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
                                        requestParts(
                                                partWithName("profileLogBodyImage")
                                                        .description("프로필 본문 이미지")),
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
                                                fieldWithPath("result.profileLogBodyImagePath")
                                                        .type(JsonFieldType.STRING)
                                                        .description("프로필 로그 본문 이미지 경로"))))
                        .andReturn();

        // JSON 응답에서 result 객체를 추출 및 검증
        final String jsonResponse = mvcResult.getResponse().getContentAsString();
        final CommonResponse<AddProfileLogBodyImageResponse> actual =
                objectMapper.readValue(
                        jsonResponse,
                        new TypeReference<CommonResponse<AddProfileLogBodyImageResponse>>() {});

        final CommonResponse<AddProfileLogBodyImageResponse> expected =
                CommonResponse.onSuccess(addProfileLogBodyImageResponse);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @DisplayName("회원이 나의 로그를 전체 조회할 수 있다.")
    @Test
    void getProfileLogItems() throws Exception {
        // given
        final ProfileLogResponseDTO.ProfileLogItem firstProfileLogItem =
                ProfileLogItem.builder()
                        .profileLogId(1L)
                        .isLogPublic(true)
                        .logType(REPRESENTATIVE_LOG)
                        .createdAt("1시간 전")
                        .modifiedAt(LocalDateTime.now())
                        .logTitle("로그 제목")
                        .logContent("로그 내용")
                        .logViewCount(10L)
                        .commentCount(15L)
                        .build();

        final ProfileLogResponseDTO.ProfileLogItem secondProfileLogItem =
                ProfileLogItem.builder()
                        .profileLogId(1L)
                        .isLogPublic(true)
                        .logType(REPRESENTATIVE_LOG)
                        .createdAt("1시간 전")
                        .modifiedAt(LocalDateTime.now())
                        .logTitle("로그 제목")
                        .logContent("로그 내용")
                        .logViewCount(10L)
                        .commentCount(15L)
                        .build();

        final ProfileLogResponseDTO.ProfileLogItems profileLogItems =
                new ProfileLogItems(Arrays.asList(firstProfileLogItem, secondProfileLogItem));

        // when
        when(profileLogService.getProfileLogItems(anyLong())).thenReturn(profileLogItems);

        final ResultActions resultActions = performGetProfileLogItems();

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
                                                subsectionWithPath("result.profileLogItems[]")
                                                        .type(JsonFieldType.ARRAY)
                                                        .description("프로필 로그 아이템 배열"),
                                                fieldWithPath(
                                                                "result.profileLogItems[].profileLogId")
                                                        .type(JsonFieldType.NUMBER)
                                                        .description("내 로그 ID"),
                                                fieldWithPath(
                                                                "result.profileLogItems[].isLogPublic")
                                                        .type(JsonFieldType.BOOLEAN)
                                                        .description("로그 공개 여부"),
                                                fieldWithPath("result.profileLogItems[].logType")
                                                        .type(JsonFieldType.STRING)
                                                        .description("로그 유형 (대표글 여부)"),
                                                fieldWithPath("result.profileLogItems[].modifiedAt")
                                                        .type(JsonFieldType.STRING)
                                                        .description("로그 수정 시간"),
                                                fieldWithPath("result.profileLogItems[].logTitle")
                                                        .type(JsonFieldType.STRING)
                                                        .description("로그 제목"),
                                                fieldWithPath("result.profileLogItems[].logContent")
                                                        .type(JsonFieldType.STRING)
                                                        .description("로그 내용"))))
                        .andReturn();

        // JSON 응답에서 result 객체를 추출 및 검증
        final String jsonResponse = mvcResult.getResponse().getContentAsString();
        final CommonResponse<ProfileLogResponseDTO.ProfileLogItems> actual =
                objectMapper.readValue(
                        jsonResponse, new TypeReference<CommonResponse<ProfileLogItems>>() {});

        final CommonResponse<ProfileLogItems> expected = CommonResponse.onSuccess(profileLogItems);

        // then
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @DisplayName("회원이 로그를 상세 조회할 수 있다.")
    @Test
    void getProfileLogItem() throws Exception {
        // given
        final ProfileLogResponseDTO.ProfileLogItem profileLogItem =
                ProfileLogItem.builder()
                        .profileLogId(1L)
                        .isLogPublic(true)
                        .logType(REPRESENTATIVE_LOG)
                        .createdAt("1시간 전")
                        .modifiedAt(LocalDateTime.now())
                        .logTitle("로그 제목")
                        .logContent("로그 내용")
                        .logViewCount(10L)
                        .commentCount(15L)
                        .build();

        // when
        when(profileLogService.getProfileLogItem(anyLong(), anyLong())).thenReturn(profileLogItem);

        final ResultActions resultActions = performGetProfileLogItem(1L);

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
                                                parameterWithName("profileLogId")
                                                        .description("프로필 로그 ID")),
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
                                                fieldWithPath("result.profileLogId")
                                                        .type(JsonFieldType.NUMBER)
                                                        .description("내 로그 ID"),
                                                fieldWithPath("result.isLogPublic")
                                                        .type(JsonFieldType.BOOLEAN)
                                                        .description("로그 공개 여부"),
                                                fieldWithPath("result.logType")
                                                        .type(JsonFieldType.STRING)
                                                        .description("로그 유형 (대표글 여부)"),
                                                fieldWithPath("result.createdAt")
                                                        .type(JsonFieldType.STRING)
                                                        .description("로그 생성 시간 (동적)"),
                                                fieldWithPath("result.modifiedAt")
                                                        .type(JsonFieldType.STRING)
                                                        .description("로그 수정 시간"),
                                                fieldWithPath("result.logTitle")
                                                        .type(JsonFieldType.STRING)
                                                        .description("로그 제목"),
                                                fieldWithPath("result.logContent")
                                                        .type(JsonFieldType.STRING)
                                                        .description("로그 내용"),
                                                fieldWithPath("result.logViewCount")
                                                        .type(JsonFieldType.NUMBER)
                                                        .description("로그 조회수"),
                                                fieldWithPath("result.commentCount")
                                                        .type(JsonFieldType.NUMBER)
                                                        .description("로그 댓글수"))))
                        .andReturn();
        final String jsonResponse = mvcResult.getResponse().getContentAsString();
        final CommonResponse<ProfileLogResponseDTO.ProfileLogItem> actual =
                objectMapper.readValue(
                        jsonResponse, new TypeReference<CommonResponse<ProfileLogItem>>() {});

        final CommonResponse<ProfileLogItem> expected = CommonResponse.onSuccess(profileLogItem);

        // then
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @DisplayName("회원이 로그를 추가할 수 있다.")
    @Test
    void addProfileLog() throws Exception {
        // given
        final ProfileLogRequestDTO.AddProfileLogRequest addProfileLogRequest =
                new AddProfileLogRequest("제목제목제목", "내용내용내용", true);

        final ProfileLogResponseDTO.AddProfileLogResponse addProfileLogResponse =
                new AddProfileLogResponse(
                        1L, "제목제목제목", "내용내용내용", LocalDateTime.now(), GENERAL_LOG, true);
        // when
        when(profileLogService.addProfileLog(anyLong(), any())).thenReturn(addProfileLogResponse);

        final ResultActions resultActions = performAddProfileLog(addProfileLogRequest);

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
                                                fieldWithPath("logTitle")
                                                        .type(JsonFieldType.STRING)
                                                        .description("로그 제목"),
                                                fieldWithPath("logContent")
                                                        .type(JsonFieldType.STRING)
                                                        .description("로그 내용"),
                                                fieldWithPath("isLogPublic")
                                                        .type(JsonFieldType.BOOLEAN)
                                                        .description("로그 공개 여부")),
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
                                                fieldWithPath("result.profileLogId")
                                                        .type(JsonFieldType.NUMBER)
                                                        .description("내 로그 ID"),
                                                fieldWithPath("result.logTitle")
                                                        .type(JsonFieldType.STRING)
                                                        .description("로그 제목"),
                                                fieldWithPath("result.logContent")
                                                        .type(JsonFieldType.STRING)
                                                        .description("로그 내용"),
                                                fieldWithPath("result.createdAt")
                                                        .type(JsonFieldType.STRING)
                                                        .description("로그 생성 시간"),
                                                fieldWithPath("result.logType")
                                                        .type(JsonFieldType.STRING)
                                                        .description("로그 유형 (대표글 여부)"),
                                                fieldWithPath("result.isLogPublic")
                                                        .type(JsonFieldType.BOOLEAN)
                                                        .description("로그 공개 여부"))))
                        .andReturn();

        // JSON 응답에서 result 객체를 추출 및 검증
        final String jsonResponse = mvcResult.getResponse().getContentAsString();
        final CommonResponse<ProfileLogResponseDTO.AddProfileLogResponse> actual =
                objectMapper.readValue(
                        jsonResponse,
                        new TypeReference<CommonResponse<AddProfileLogResponse>>() {});

        final CommonResponse<AddProfileLogResponse> expected =
                CommonResponse.onSuccess(addProfileLogResponse);

        // then
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @DisplayName("회원이 로그를 추가할 수 있다.")
    @Test
    void updateProfileLog() throws Exception {
        // given
        final ProfileLogRequestDTO.UpdateProfileLogRequest updateProfileLogRequest =
                new UpdateProfileLogRequest("제목제목제목", "내용내용내용", true);

        final ProfileLogResponseDTO.UpdateProfileLogResponse updateProfileLogResponse =
                new UpdateProfileLogResponse(
                        1L, "제목제목제목", "내용내용내용", LocalDateTime.now(), GENERAL_LOG, true);
        // when
        when(profileLogService.updateProfileLog(anyLong(), anyLong(), any()))
                .thenReturn(updateProfileLogResponse);

        final ResultActions resultActions = performUpdateProfileLog(1L, updateProfileLogRequest);

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
                                                parameterWithName("profileLogId")
                                                        .description("프로필 로그 ID")),
                                        requestFields(
                                                fieldWithPath("logTitle")
                                                        .type(JsonFieldType.STRING)
                                                        .description("로그 제목"),
                                                fieldWithPath("logContent")
                                                        .type(JsonFieldType.STRING)
                                                        .description("로그 내용"),
                                                fieldWithPath("isLogPublic")
                                                        .type(JsonFieldType.BOOLEAN)
                                                        .description("로그 공개 여부")),
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
                                                fieldWithPath("result.profileLogId")
                                                        .type(JsonFieldType.NUMBER)
                                                        .description("내 로그 ID"),
                                                fieldWithPath("result.logTitle")
                                                        .type(JsonFieldType.STRING)
                                                        .description("로그 제목"),
                                                fieldWithPath("result.logContent")
                                                        .type(JsonFieldType.STRING)
                                                        .description("로그 내용"),
                                                fieldWithPath("result.createdAt")
                                                        .type(JsonFieldType.STRING)
                                                        .description("로그 생성 시간"),
                                                fieldWithPath("result.logType")
                                                        .type(JsonFieldType.STRING)
                                                        .description("로그 유형 (대표글 여부)"),
                                                fieldWithPath("result.isLogPublic")
                                                        .type(JsonFieldType.BOOLEAN)
                                                        .description("로그 공개 여부"))))
                        .andReturn();

        // JSON 응답에서 result 객체를 추출 및 검증
        final String jsonResponse = mvcResult.getResponse().getContentAsString();
        final CommonResponse<ProfileLogResponseDTO.UpdateProfileLogResponse> actual =
                objectMapper.readValue(
                        jsonResponse,
                        new TypeReference<CommonResponse<UpdateProfileLogResponse>>() {});

        final CommonResponse<UpdateProfileLogResponse> expected =
                CommonResponse.onSuccess(updateProfileLogResponse);

        // then
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @DisplayName("회원이 나의 로그를 단일 삭제할 수 있다.")
    @Test
    void removeProfileLog() throws Exception {
        // given
        final ProfileLogResponseDTO.RemoveProfileLogResponse removeProfileLogResponse =
                new ProfileLogResponseDTO.RemoveProfileLogResponse(1L);

        // when
        when(profileLogService.removeProfileLog(anyLong(), anyLong()))
                .thenReturn(removeProfileLogResponse);

        final ResultActions resultActions = performDeleteProfileLog(1L);

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
                                                parameterWithName("profileLogId")
                                                        .description("프로필 로그 ID")),
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
                                                fieldWithPath("result.profileLogId")
                                                        .type(JsonFieldType.NUMBER)
                                                        .description("해당 로그 ID"))))
                        .andReturn();
        // JSON 응답에서 result 객체를 추출 및 검증
        final String jsonResponse = mvcResult.getResponse().getContentAsString();
        final CommonResponse<ProfileLogResponseDTO.RemoveProfileLogResponse> actual =
                objectMapper.readValue(
                        jsonResponse,
                        new TypeReference<CommonResponse<RemoveProfileLogResponse>>() {});

        final CommonResponse<RemoveProfileLogResponse> expected =
                CommonResponse.onSuccess(removeProfileLogResponse);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @DisplayName("회원이 나의 로그를 대표글 설정을 변경할 수 있다.")
    @Test
    void updateProfileLogType() throws Exception {
        // given

        final ProfileLogResponseDTO.UpdateProfileLogTypeResponse updateProfileLogTypeResponse =
                new UpdateProfileLogTypeResponse(1L, REPRESENTATIVE_LOG);

        // when
        when(profileLogService.updateProfileLogType(anyLong(), anyLong()))
                .thenReturn(updateProfileLogTypeResponse);

        final ResultActions resultActions = performUpdateProfileLogType(1L);

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
                                                parameterWithName("profileLogId")
                                                        .description("프로필 로그 ID")),
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
                                                fieldWithPath("result.profileLogId")
                                                        .type(JsonFieldType.NUMBER)
                                                        .description("해당 로그 ID"),
                                                fieldWithPath("result.logType")
                                                        .type(JsonFieldType.STRING)
                                                        .description("해당 로그 대표글 설정 여부"))))
                        .andReturn();
        // JSON 응답에서 result 객체를 추출 및 검증
        final String jsonResponse = mvcResult.getResponse().getContentAsString();
        final CommonResponse<ProfileLogResponseDTO.UpdateProfileLogTypeResponse> actual =
                objectMapper.readValue(
                        jsonResponse,
                        new TypeReference<CommonResponse<UpdateProfileLogTypeResponse>>() {});

        final CommonResponse<UpdateProfileLogTypeResponse> expected =
                CommonResponse.onSuccess(updateProfileLogTypeResponse);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @DisplayName("회원이 나의 로그를 공개 여부 설정을 변경할 수 있다.")
    @Test
    void updateProfileLogPublicState() throws Exception {
        // given
        final ProfileLogResponseDTO.UpdateProfileLogPublicStateResponse
                updateProfileLogPublicStateResponse =
                        new UpdateProfileLogPublicStateResponse(1L, true);

        // when
        when(profileLogService.updateProfileLogPublicState(anyLong(), anyLong()))
                .thenReturn(updateProfileLogPublicStateResponse);

        final ResultActions resultActions = performUpdateProfileLogPublicState(1L);

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
                                                parameterWithName("profileLogId")
                                                        .description("프로필 로그 ID")),
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
                                                fieldWithPath("result.profileLogId")
                                                        .type(JsonFieldType.NUMBER)
                                                        .description("해당 로그 ID"),
                                                fieldWithPath("result.isLogPublic")
                                                        .type(JsonFieldType.BOOLEAN)
                                                        .description("해당 로그 변경된 로그 공개 여부"))))
                        .andReturn();
        // JSON 응답에서 result 객체를 추출 및 검증
        final String jsonResponse = mvcResult.getResponse().getContentAsString();
        final CommonResponse<ProfileLogResponseDTO.UpdateProfileLogPublicStateResponse> actual =
                objectMapper.readValue(
                        jsonResponse,
                        new TypeReference<
                                CommonResponse<UpdateProfileLogPublicStateResponse>>() {});

        final CommonResponse<UpdateProfileLogPublicStateResponse> expected =
                CommonResponse.onSuccess(updateProfileLogPublicStateResponse);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }
}
